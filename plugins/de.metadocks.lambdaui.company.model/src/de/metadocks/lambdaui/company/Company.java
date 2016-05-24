package de.metadocks.lambdaui.company;

import java.util.List;

import de.metadocks.beans.Bean;
import de.metadocks.beans.ListProperty;
import de.metadocks.beans.ValueProperty;

public class Company extends Bean {
	private ValueProperty<String> name;
	private ListProperty<Employee> employees;

	public List<Employee> getEmployees() {
		return employees.getValue();
	}

	public String getName() {
		return name.getValue();
	}

	public void setName(String name) {
		this.name.setValue(name);
	}
}
