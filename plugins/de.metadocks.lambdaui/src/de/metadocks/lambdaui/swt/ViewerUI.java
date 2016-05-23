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
package de.metadocks.lambdaui.swt;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.IViewerValueProperty;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ViewerUI<V extends Viewer> extends SwtUI<Control> {
	private V viewer;

	public V viewer() {
		return viewer;
	}

	@Override
	public ViewerUI<V> id(String id) {
		Control control = control();
		SwtUI.wrap(control).id(id);
		control.setData(VIEWER, viewer);
		return this;
	}

	public ViewerUI<V> customizeViewer(Consumer<V> consumer) {
		consumer.accept(viewer);
		return this;
	}

	public static <V extends Viewer> ViewerUI<V> createViewer(BiFunction<Composite, Integer, V> ctor) {
		return createViewer(ctor, SWT.None);
	}

	public static <V extends Viewer> ViewerUI<V> createViewer(BiFunction<Composite, Integer, V> ctor, int style) {
		if (currentParent == null) {
			throw new IllegalStateException("Invalid child creation context.");
		}

		Composite parent = (Composite) currentParent;

		try {
			return createViewer(ctor, parent, style);
		} finally {
			currentParent = null;
		}
	}

	public static <V extends Viewer> ViewerUI<V> createViewer(BiFunction<Composite, Integer, V> ctor,
			Composite parent) {
		return createViewer(ctor, parent, SWT.None);
	}

	public static <V extends Viewer> ViewerUI<V> createViewer(BiFunction<Composite, Integer, V> ctor, Composite parent,
			int style) {
		V widget = ctor.apply(parent, style);
		return wrapViewer(widget);
	}

	public static <V extends Viewer> ViewerUI<V> wrapViewer(V viewer) {
		ViewerUI<V> builder = new ViewerUI<V>() {
			@Override
			public Control control() {
				return viewer.getControl();
			}
		};
		builder.viewer = viewer;
		return builder;
	}

	public ViewerUI<V> prop(IViewerValueProperty prop, Object value) {
		if (value instanceof String) {
			bind(prop, (String) value);
		} else {
			// no binding expression
			prop.setValue(control(), value);
		}

		return this;
	}

	public ViewerUI<V> input(Object value) {
		return prop(ViewerProperties.input(), value);
	}

	private void bind(IViewerValueProperty prop, String expr) {
		Object dataContext = findTagged(DATA_CONTEXT, null);
		DataBindingContext dbc = findTagged(DataBindingContext.class);
		org.eclipse.core.databinding.Binding binding = bindingFactoryRegistry.bind(dbc, dataContext, expr, delay -> {
			return prop.observe(viewer);
		});

		if (binding == null) {
			// no observables have been parsed, just use the value
			prop.setValue(control(), expr);
		}
	}
}
