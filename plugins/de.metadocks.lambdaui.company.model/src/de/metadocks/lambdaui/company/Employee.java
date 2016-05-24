package de.metadocks.lambdaui.company;

import de.metadocks.beans.ValueProperty;

public class Employee extends Person {
	private ValueProperty<Integer> id;

	public int getId() {
		return id.getValue(0);
	}

	public void setId(int id) {
		this.id.setValue(id);
	}
}
