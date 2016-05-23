/*******************************************************************************
 * Copyright (c) 2016 Erdal Karaca and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erdal Karaca - initial API and implementation
 *******************************************************************************/
package de.metadocks.xprbinds.internal.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.metadocks.xprbinds.conversion.ConvertersRegistry;
import de.metadocks.xprbinds.internal.expr.ExprEvaluator;
import de.metadocks.xprbinds.internal.expr.ExprParser;
import de.metadocks.xprbinds.internal.expr.ExprParser.Element;
import de.metadocks.xprbinds.internal.expr.ExprParser.Node;
import de.metadocks.xprbinds.internal.expr.ExprParser.TextNode;

@Component(service = BindingFactoryRegistry.class)
public class BindingFactoryRegistry {
	private static BindingFactoryRegistry INSTANCE;

	private List<ObservableFactory> factories = new ArrayList<>();

	public BindingFactoryRegistry() {
		INSTANCE = this;
	}

	@Reference
	public void addFactory(ObservableFactory factory) {
		factories.add(factory);
	}

	public void removeFactory(ObservableFactory factory) {
		factories.remove(factory);
	}

	public <T> IObservableFactory getFactory(T object) {
		return null;
	}

	/**
	 * INTERNAL API: this method only exists for environments where OSGi is not
	 * available. Consumers should be aware of not all factories being
	 * available.
	 */
	public static BindingFactoryRegistry getInstance() {
		if (INSTANCE == null) {
			synchronized (BindingFactoryRegistry.class) {
				if (INSTANCE == null) {
					INSTANCE = new BindingFactoryRegistry();
					INSTANCE.addFactory(new EMFObservableFactory());
					INSTANCE.addFactory(new BeansObservableFactory());
				}
			}
		}

		return INSTANCE;
	}

	public IObservable observe(Object dataContext, String path) {
		if (dataContext instanceof IObservableValue) {
			IObservableValue observableValue = (IObservableValue) dataContext;
			IObservableValue detailValue = MasterDetailObservables.detailValue(observableValue, target -> {
				return observe(target, path);
			}, null);
			return detailValue;
		}

		IObservable ret = null;
		String[] paths = path.split(".");

		return ret == null ? observeProperty(dataContext, path) : ret;
	}

	private IObservable observeProperty(Object dataContext, String path) {
		for (ObservableFactory observableFactory : factories) {
			if (observableFactory.canHandle(dataContext, path)) {
				return observableFactory.createObservable(dataContext, path);
			}
		}

		return null;
	}

	public org.eclipse.core.databinding.Binding bind(DataBindingContext dbc, Object dataContext, String expr,
			Function<Integer, IObservable> obsFunc) {
		List<Node> nodes = new ExprParser().parseTree(expr);
		boolean hasObservables = false;
		List<Object> obs = new ArrayList<>();
		int delay = 0;

		for (Node node : nodes) {
			if (node instanceof Element) {
				Element element = (Element) node;
				Binding binding = ExprEvaluator.getInstance().evaluate(element, Binding.class);
				binding.setExpression(element.expr);
				delay = Math.max(delay, binding.getDelay());
				WritableValue wv = new WritableValue();

				if (dataContext != null) {
					// this binding element has a path to the root data context
					wv.setValue(dataContext);
				}

				IObservable modelObservableValue = observe(wv, binding.getPath());
				obs.add(modelObservableValue);
				hasObservables = true;
			} else if (node instanceof TextNode) {
				obs.add(((TextNode) node).value);
			}
		}

		if (!hasObservables) {
			// fail early as there is no observable involved in the provided
			// expression
			return null;
		}

		UpdateValueStrategy t2m = new UpdateValueStrategy();
		UpdateValueStrategy m2t = new UpdateValueStrategy();
		IObservable modelObservable;

		if (obs.size() == 1) {
			modelObservable = (IObservable) obs.get(0);
		} else {
			// aggregate to string observable by concatenating all values
			// the value will be automatically updated once one of the
			// observables change
			modelObservable = constructComputedValue(obs);
		}

		if (modelObservable instanceof IObservableValue) {
			IObservableValue modelObservableValue = (IObservableValue) modelObservable;
			IObservableValue targetObservable = (IObservableValue) obsFunc.apply(delay);
			org.eclipse.core.databinding.Binding binding = dbc.bindValue(targetObservable, modelObservableValue, t2m,
					m2t);
			return binding;
		} else {
			throw new UnsupportedOperationException(
					"Observable type not supported yet: " + modelObservable.getClass().getName());
		}
	}

	private IObservable constructComputedValue(List<Object> obs) {
		IObservable modelObservable;
		modelObservable = new ComputedValue(String.class) {

			@Override
			protected Object calculate() {
				StringBuilder sb = new StringBuilder();

				for (Object object : obs) {
					if (object instanceof String) {
						sb.append((String) object);
					} else if (object instanceof IObservableValue) {
						Object obValue = ((IObservableValue) object).getValue();
						obValue = ConvertersRegistry.getInstance().convert(obValue, String.class);
						sb.append((String) obValue);
					}
				}

				return sb.toString();
			}
		};
		return modelObservable;
	}
}
