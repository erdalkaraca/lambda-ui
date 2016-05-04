/*******************************************************************************
 * Copyright (c) 2016 Erdal Karaca.
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
import java.util.function.Supplier;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class SwtUI<T extends Control> {

	protected static final String VIEWER = "viewer";
	protected static String ID = "id";

	// no need to be thread-safe as UI is always constructed within the UI
	// thread
	protected static Object currentParent;

	public abstract T control();

	public SwtUI<T> id(String id) {
		control().setData(ID, id);
		return this;
	}

	public SwtUI<T> text(String text) {
		WidgetProperties.text().setValue(control(), text);
		return this;
	}

	public <V> SwtUI<T> prop(Supplier<IWidgetValueProperty> propSupplier, V value) {
		propSupplier.get().setValue(control(), value);
		return this;
	}

	public SwtUI<T> layout(Layout layout) {
		((Composite) control()).setLayout(layout);
		return this;
	}

	public SwtUI<T> layoutData(Object layoutData) {
		((Control) control()).setLayoutData(layoutData);
		return this;
	}

	public <C extends Control> SwtUI<T> child(ControlSupplier supplier) {
		currentParent = control();
		supplier.getControlUI();
		return this;
	}

	public <C extends Viewer> SwtUI<T> child(ViewerSupplier supplier) {
		currentParent = control();
		supplier.getViewerUI();
		return this;
	}

	public SwtUI<T> customize(Consumer<T> consumer) {
		consumer.accept(control());
		return this;
	}

	public SwtUI<T> observeText(int event, Consumer<IObservableValue> obsConsumer) {
		IWidgetValueProperty prop = WidgetProperties.text(event);
		return observeValue(() -> prop, obsConsumer);
	}

	public SwtUI<T> observeValue(Supplier<IWidgetValueProperty> propSupplier, Consumer<IObservableValue> obsConsumer) {
		IWidgetValueProperty prop = propSupplier.get();
		ISWTObservableValue observe = prop.observe(control());
		obsConsumer.accept(observe);
		return this;
	}

	public SwtUI<T> on(int swtEvent, Listener listener) {
		control().addListener(swtEvent, listener);
		return this;
	}

	public <C extends Control> SwtUI<T> withChild(String id, Consumer<C> consumer) {
		C found = find(id, control());

		if (found == null) {
			throw new IllegalArgumentException("Control not found: " + id);
		}

		syncExec(() -> consumer.accept(found));
		return this;
	}

	public SwtUI<T> syncExec(Runnable code) {
		T control = control();

		if (Thread.currentThread() != control.getDisplay().getThread()) {
			control.getDisplay().syncExec(code);
		} else {
			code.run();
		}

		return this;
	}

	public SwtUI<T> asyncExec(Runnable code) {
		T control = control();

		control.getDisplay().asyncExec(() -> {
			if (!control.isDisposed()) {
				code.run();
			}
		});

		return this;
	}

	public SwtUI<T> timerExec(int delay, Runnable code) {
		T control = control();

		control.getDisplay().timerExec(delay, () -> {
			if (!control.isDisposed()) {
				code.run();
			}
		});

		return this;
	}

	public <C extends Control> C find(String id) {
		return find(id, control());
	}

	@SuppressWarnings("unchecked")
	private <C extends Control> C find(String id, T context) {
		String contextId = (String) context.getData(ID);

		if (id.equals(contextId)) {
			return (C) context;
		}

		if (context instanceof Composite) {
			for (Control c : ((Composite) context).getChildren()) {
				Control found = find(id, (T) c);

				if (found != null) {
					return (C) found;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public <V extends Viewer> V findViewer(String id) {
		Control control = control();
		Control found = find(id, (T) control);

		if (found.getData(VIEWER) instanceof Viewer) {
			return (V) found.getData(VIEWER);
		}

		return null;
	}

	public static <T extends Control> SwtUI<T> create(BiFunction<Composite, Integer, T> ctor) {
		return create(ctor, SWT.None);
	}

	public static <T extends Control> SwtUI<T> create(BiFunction<Composite, Integer, T> ctor, int style) {
		if (currentParent == null) {
			throw new IllegalStateException("Invalid child creation context.");
		}

		Composite parent = (Composite) currentParent;

		try {
			return create(ctor, parent, style);
		} finally {
			currentParent = null;
		}
	}

	public static <T extends Control> SwtUI<T> create(BiFunction<Composite, Integer, T> ctor, Composite parent) {
		return create(ctor, parent, SWT.None);
	}

	public static <T extends Control> SwtUI<T> create(BiFunction<Composite, Integer, T> ctor, Composite parent,
			int style) {
		T widget = ctor.apply(parent, style);
		return wrap(widget);
	}

	public static <T extends Control> SwtUI<T> wrap(T control) {
		SwtUI<T> builder = new SwtUI<T>() {
			@Override
			public T control() {
				return control;
			}
		};
		return builder;
	}

	public static interface ViewerSupplier {
		ViewerUI<? extends Viewer> getViewerUI();
	}

	public static interface ControlSupplier {
		SwtUI<? extends Control> getControlUI();
	}

	public static void openInShell(Consumer<SwtUI<Shell>> uiConsumer) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		uiConsumer.accept(SwtUI.wrap(shell));
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}