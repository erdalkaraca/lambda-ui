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

import org.junit.Test;

import de.metadocks.beans.Bean;
import de.metadocks.beans.ValueProperty;

public class BeanTest {
	static class Car extends Bean {

		private ValueProperty<Integer> numberOfWheels;

		public void setNumberOfWheels(Integer i) {
			numberOfWheels.setValue(i);
		}

		public Integer getNumberOfWheels() {
			return numberOfWheels.getValue();
		}
	}

	@Test
	public void testProperties() {
		Car car = new Car();
		assertEquals(null, car.getNumberOfWheels());

		car.setNumberOfWheels(4);
		int numberOfWheels = car.getNumberOfWheels();
		System.out.println(numberOfWheels);
	}
}
