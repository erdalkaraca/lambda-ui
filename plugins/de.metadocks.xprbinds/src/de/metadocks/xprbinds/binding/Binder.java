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
package de.metadocks.xprbinds.binding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.internal.databinding.observable.DelayedObservableValue;
import org.eclipse.core.internal.databinding.observable.masterdetail.DetailObservableValue;

import de.metadocks.xprbinds.binding.internal.Binding;
import de.metadocks.xprbinds.binding.internal.ObservableFactoriesRegistry;
import de.metadocks.xprbinds.conversion.ConvertersRegistry;
import de.metadocks.xprbinds.internal.expr.ExprEvaluator;
import de.metadocks.xprbinds.internal.expr.ExprParser;
import de.metadocks.xprbinds.internal.expr.ExprParser.Element;
import de.metadocks.xprbinds.internal.expr.ExprParser.Node;
import de.metadocks.xprbinds.internal.expr.ExprParser.TextNode;

@SuppressWarnings("restriction")
public class Binder {
	// this is a hack used to late initialize update value strategies for
	// details observable values when their master observable changes
	private static Field innerObservableValueField;
	static {
		try {
			innerObservableValueField = DetailObservableValue.class.getDeclaredField("innerObservableValue");
			if (!innerObservableValueField.isAccessible()) {
				innerObservableValueField.setAccessible(true);
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private DataBindingContext dbc = new DataBindingContext();
	private ObservableFactoriesRegistry registry;

	public Binder(ObservableFactoriesRegistry registry) {
		this.registry = registry;
	}

	public org.eclipse.core.databinding.Binding bind(IObservableValue targetObservableValue, Object dataContext,
			String expr) {
		List<Object> obs = parseExpression(dataContext, expr);
		return bind(targetObservableValue, dataContext, obs.toArray());
	}

	public org.eclipse.core.databinding.Binding bind(IObservableValue targetObservableValue, Object dataContext,
			Object... bindings) {
		List<Object> obs = new ArrayList<>();
		int delay = 0;
		boolean hasObservables = false;

		for (Object object : bindings) {
			if (object instanceof Binding) {
				Binding binding = (Binding) object;
				IObservable observable = toObservable(dataContext, binding);
				obs.add(observable);
				delay = Math.max(delay, binding.getDelay());
				hasObservables = true;
			} else {
				obs.add(object);
			}
		}

		if (!hasObservables) {
			// fail early as there is no observable involved in the provided
			// expression
			return null;
		}

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
			IObservableValue targetObservable = (IObservableValue) targetObservableValue;

			if (delay > 0) {
				targetObservable = new DelayedObservableValue(delay, targetObservable);
			}

			DynamicUpdateValueStrategy t2m = new DynamicUpdateValueStrategy();
			DynamicUpdateValueStrategy m2t = new DynamicUpdateValueStrategy();

			if (modelObservableValue instanceof DetailObservableValue) {
				final IObservableValue targetObs = targetObservable;
				// detail observables are initially constructed without a detail
				// type, so, the update strategies will not be filled with
				// default converters, we have to manually fill the defaults
				// whenever the detail value changes
				modelObservableValue.addValueChangeListener(evt -> {
					t2m.fillDefaults(targetObs, modelObservableValue);
					m2t.fillDefaults(modelObservableValue, targetObs);
				});
			}

			org.eclipse.core.databinding.Binding binding = dbc.bindValue(targetObservable, modelObservableValue, t2m,
					m2t);
			return binding;
		} else {
			throw new UnsupportedOperationException(
					"Observable type not supported yet: " + modelObservable.getClass().getName());
		}
	}

	private IObservable toObservable(Object dataContext, Binding binding) {
		WritableValue wv = new WritableValue();

		if (dataContext != null) {
			// this binding element has a path to the root data context
			wv.setValue(dataContext);
		}

		IObservable modelObservableValue = observe(wv, binding.getPath());
		return modelObservableValue;
	}

	private List<Object> parseExpression(Object dataContext, String expr) {
		List<Node> nodes = new ExprParser().parseTree(expr);
		List<Object> obs = new ArrayList<>();

		for (Node node : nodes) {
			if (node instanceof Element) {
				Element element = (Element) node;
				Binding binding = ExprEvaluator.getInstance().evaluate(element, Binding.class);
				binding.setExpression(element.expr);
				obs.add(binding);
			} else if (node instanceof TextNode) {
				obs.add(((TextNode) node).value);
			}
		}

		return obs;
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

	private IObservable observe(Object dataContext, String path) {
		if (dataContext instanceof IObservableValue) {
			IObservableValue observableValue = (IObservableValue) dataContext;
			IObservableFactory factory = target -> {
				return observe(target, path);
			};
			@SuppressWarnings("restriction")
			IObservableValue detailValue = new DetailObservableValue(observableValue, factory, null) {

				@Override
				public Object getValueType() {
					try {
						IObservableValue innerObservableValue = (IObservableValue) innerObservableValueField.get(this);

						if (innerObservableValue != null) {
							return innerObservableValue.getValueType();
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}

					return super.getValueType();
				}
			};
			return detailValue;
		}

		IObservable ret = null;
		String[] paths = path.split(".");

		return ret == null ? observeProperty(dataContext, path) : ret;
	}

	private IObservable observeProperty(Object dataContext, String path) {
		ObservableFactory factory = registry.getFactory(dataContext, path);

		if (factory == null) {
			return null;
		}

		return factory.createObservable(dataContext, path);
	}

	public void dispose() {
		dbc.dispose();
	}

	/**
	 * 
	 * @param registry
	 *            the registry to use, <code>null</code> if to use the default
	 *            registry (which may be incomplete)
	 * @return a new {@link Binder}
	 */
	public static Binder create(ObservableFactoriesRegistry registry) {
		if (registry == null) {
			registry = ObservableFactoriesRegistry.getInstance();
		}

		return new Binder(registry);
	}

	private static final class DynamicUpdateValueStrategy extends UpdateValueStrategy {
		@Override
		public void fillDefaults(IObservableValue source, IObservableValue destination) {
			super.fillDefaults(source, destination);
		}
	}
}
