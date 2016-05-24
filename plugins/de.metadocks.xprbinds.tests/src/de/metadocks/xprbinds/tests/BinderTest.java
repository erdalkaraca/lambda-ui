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
package de.metadocks.xprbinds.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.junit.Rule;
import org.junit.Test;

import de.metadocks.lambdaui.company.Company;
import de.metadocks.testutils.RealmProvider;
import de.metadocks.xprbinds.binding.Binder;

public class BinderTest {
	@Rule
	public RealmProvider realmProvider = new RealmProvider();

	@Test
	public void testBindByExpression() {
		Binder binder = Binder.create(null);
		assertNotNull(binder);

		IObservableValue targetObservableValue = new WritableValue();
		Company company = new Company();
		company.setName("A0");
		binder.bind(targetObservableValue, company, "{path=name}");
		assertEquals("A0", targetObservableValue.getValue());
		
		company.setName("A1");
		assertEquals("A1", targetObservableValue.getValue());
		
		targetObservableValue.setValue("B0");
		assertEquals("B0", company.getName());
	}
}
