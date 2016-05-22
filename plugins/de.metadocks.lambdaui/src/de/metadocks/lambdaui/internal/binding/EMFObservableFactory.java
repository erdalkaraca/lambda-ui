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

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class EMFObservableFactory implements ObservableFactory {

	@Override
	public boolean canHandle(Object target, String path) {
		return target instanceof EObject;
	}

	@Override
	public IObservable createObservable(Object target, String path, String... nestedPath) {
		EObject eObject = (EObject) target;
		// TODO consider nested features
		EClass eClass = eObject.eClass();
		EList<EStructuralFeature> features = eClass.getEAllStructuralFeatures();

		for (EStructuralFeature feature : features) {
			if (feature.getName().equals(path)) {
				return EMFObservables.observeValue(eObject, feature);
			}
		}

		return null;
	}
}
