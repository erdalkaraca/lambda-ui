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
package de.metadocks.lambdaui.internal.binding;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.osgi.service.component.annotations.Component;

@Component(service = ObservableFactory.class)
public class BeansObservableFactory implements ObservableFactory {

	@Override
	public IObservable createObservable(Object target, String path, String... nestedPath) {
		// TODO check for nested properties
		IBeanValueProperty value = BeanProperties.value(target.getClass(), path);
		IObservableValue observableValue = value.observe(target);
		return observableValue;
	}

	@Override
	public boolean canHandle(Object object, String path) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
			beanInfo.toString();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		return object != null;
	}
}
