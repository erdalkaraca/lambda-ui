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
package de.metadocks.lambdaui.beans;

public class Property {
	private String name;
	private Bean bean;

	public Property(String name, Bean bean) {
		this.name = name;
		this.bean = bean;
	}

	public String getName() {
		return name;
	}

	public Bean getBean() {
		return bean;
	}
}
