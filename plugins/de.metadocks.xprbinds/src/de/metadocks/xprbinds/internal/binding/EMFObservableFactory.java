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
package de.metadocks.xprbinds.internal.binding;

import org.eclipse.core.databinding.observable.IObservable;
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
	public IObservable createObservable(Object target, String path) {
		EObject eObject = (EObject) target;
		EClass eClass = eObject.eClass();
		EStructuralFeature feature = eClass.getEStructuralFeature(path);

		if (feature == null) {
			throw new IllegalArgumentException("Feature not found: eClass=" + eClass.getName() + ", name=" + path);
		}
		
		return EMFObservables.observeValue(eObject, feature);
	}
}
