package de.metadocks.lambdaui.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.metadocks.lambdaui.beans.Bean;
import de.metadocks.lambdaui.beans.ValueProperty;

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
