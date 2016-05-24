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

import java.util.Date;

import de.metadocks.beans.Bean;
import de.metadocks.beans.ValueProperty;

public class Person extends Bean {
	private ValueProperty<String> firstName;
	private ValueProperty<String> lastName;
	private ValueProperty<Date> birthdate;
	private ValueProperty<Address> address;

	public String getFirstName() {
		return firstName.getValue();
	}

	public void setFirstName(String firstName) {
		this.firstName.setValue(firstName);
	}

	public String getLastName() {
		return lastName.getValue();
	}

	public void setLastName(String lastName) {
		this.lastName.setValue(lastName);
	}

	public Date getBirthdate() {
		return birthdate.getValue();
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate.setValue(birthdate);
	}

	public Address getAddress() {
		return address.getValue();
	}

	public void setAddress(Address address) {
		this.address.setValue(address);
	}
}
