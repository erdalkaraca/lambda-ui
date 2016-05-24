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
package de.metadocks.lambdaui.company;

import de.metadocks.beans.Bean;
import de.metadocks.beans.ValueProperty;

public class Address extends Bean {
	private ValueProperty<String> street;
	private ValueProperty<Integer> number;
	private ValueProperty<String> zipCode;

	public String getStreet() {
		return street.getValue();
	}

	public void setStreet(String street) {
		this.street.setValue(street);
	}

	public int getNumber() {
		return number.getValue(0);
	}

	public void setNumber(int number) {
		this.number.setValue(number);
	}

	public String getZipCode() {
		return zipCode.getValue();
	}

	public void setZipCode(String zipCode) {
		this.zipCode.setValue(zipCode);
	}
}
