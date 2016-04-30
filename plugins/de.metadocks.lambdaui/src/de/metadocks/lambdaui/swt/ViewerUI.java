package de.metadocks.lambdaui.swt;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class ViewerUI<V extends Viewer> extends SwtUI<Control> {
	private V viewer;

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
