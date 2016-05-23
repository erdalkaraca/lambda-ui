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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class Bean {
	private static final Logger LOG = Logger.getLogger(Bean.class.getName());
	private static Map<Class<?>, BiFunction<String, Bean, Object>> inits = new HashMap<>();
	static {
		inits.put(ValueProperty.class, ValueProperty::new);
		inits.put(ListProperty.class, ListProperty::new);
	}

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Bean() {
		initPropertyFields(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	private static void initPropertyFields(Bean bean) {
		Class<? extends Bean> beanType = bean.getClass();
		initPropertyFields(bean, beanType);
	}

	@SuppressWarnings("unchecked")
	private static void initPropertyFields(Bean bean, Class<? extends Bean> beanType) {
		try {
			for (Field field : beanType.getDeclaredFields()) {
				Class<?> type = field.getType();

				if (!Property.class.isAssignableFrom(type)) {
					continue;
				}

				BiFunction<String, Bean, Object> ction = inits.get(type);

				if (ction == null) {
					LOG.severe("Property handler not found for: " + type);
					continue;
				}

				String name = field.getName();
				LOG.finest("Setting field: " + name);
				Object fieldValue = ction.apply(name, bean);

				if (!field.isAccessible()) {
					field.setAccessible(true);
				}

				field.set(bean, fieldValue);
			}

			Class<?> superclass = beanType.getSuperclass();

			if (!Bean.class.equals(beanType) && Bean.class.isAssignableFrom(superclass)) {
				initPropertyFields(bean, (Class<? extends Bean>) superclass);
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			LOG.severe(e1.getMessage());
		}
	}
}
