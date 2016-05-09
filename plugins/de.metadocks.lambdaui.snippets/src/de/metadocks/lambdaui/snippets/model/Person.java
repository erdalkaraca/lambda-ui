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
package de.metadocks.lambdaui.snippets.model;

public class Person extends Bean {
	private String firstName;
	private String lastName;
	private Address address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		firePropertyChange("firstName", this.firstName, this.firstName = firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		firePropertyChange("lastName", this.lastName, this.lastName = lastName);
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
