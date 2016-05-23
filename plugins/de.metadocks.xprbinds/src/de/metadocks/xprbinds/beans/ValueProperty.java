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
package de.metadocks.xprbinds.beans;

import java.util.function.Supplier;

public class ValueProperty<T> extends Property {
	private T value;

	public ValueProperty(String name, Bean bean) {
		super(name, bean);
	}

	public void setValue(T value) {
		T oldValue = value;
		this.value = value;
		getBean().firePropertyChange(getName(), oldValue, value);
	}

	public T getValue() {
		return value;
	}

	public T getValue(Supplier<T> defaultValue) {
		if (value == null) {
			value = defaultValue.get();
		}

		return value;
	}

	public T getValue(T defaultValue) {
		if (value == null) {
			value = defaultValue;
		}

		return value;
	}
}