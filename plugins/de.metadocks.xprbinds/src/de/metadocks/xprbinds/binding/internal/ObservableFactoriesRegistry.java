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
package de.metadocks.xprbinds.binding.internal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.metadocks.xprbinds.binding.ObservableFactory;

@Component(service = ObservableFactoriesRegistry.class)
public class ObservableFactoriesRegistry {
	private static ObservableFactoriesRegistry INSTANCE;

	private List<ObservableFactory> factories = new ArrayList<>();

	public ObservableFactoriesRegistry() {
		INSTANCE = this;
	}

	@Reference
	public void addFactory(ObservableFactory factory) {
		factories.add(factory);
	}

	public void removeFactory(ObservableFactory factory) {
		factories.remove(factory);
	}

	public <T> ObservableFactory getFactory(T object, String path) {
		for (ObservableFactory observableFactory : factories) {
			if (observableFactory.canHandle(object, path)) {
				return observableFactory;
			}
		}

		return null;
	}

	/**
	 * INTERNAL API: this method only exists for environments where OSGi is not
	 * available. Consumers should be aware of not all factories being
	 * available.
	 */
	public static ObservableFactoriesRegistry getInstance() {
		if (INSTANCE == null) {
			synchronized (ObservableFactoriesRegistry.class) {
				if (INSTANCE == null) {
					INSTANCE = new ObservableFactoriesRegistry();
					// add the beans observable factory as default provider
					// any other factories should be consumed from the OSGi
					// service registry or the conusmer of this API has to
					// register them manually
					INSTANCE.addFactory(new BeansObservableFactory());
				}
			}
		}

		return INSTANCE;
	}
}
