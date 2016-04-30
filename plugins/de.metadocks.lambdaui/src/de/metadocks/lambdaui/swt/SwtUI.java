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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public abstract class SwtUI<T extends Control> {

	private static String ID = "id";

	// no need to be thread-safe as UI is always constructed within the UI
	// thread
	protected static Object currentParent;

	public abstract T control();

	public SwtUI<T> id(String id) {
		control().setData(ID, id);
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
}