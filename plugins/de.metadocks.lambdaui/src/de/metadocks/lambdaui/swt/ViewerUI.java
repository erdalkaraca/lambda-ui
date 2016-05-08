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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ViewerUI<V extends Viewer> extends SwtUI<Control> {
	private V viewer;

	@Override
	public ViewerUI<V> id(String id) {
		Control control = control();
		SwtUI.wrap(control).id(id);
		control.setData("viewer", viewer);
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
}
