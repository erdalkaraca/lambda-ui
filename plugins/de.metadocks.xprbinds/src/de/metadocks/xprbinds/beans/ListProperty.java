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
package de.metadocks.xprbinds.beans;

import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

public class ListProperty<T> extends Property {

	private IObservableList list = new WritableList();

	public ListProperty(String name, Bean bean) {
		super(name, bean);
	}

	@SuppressWarnings("unchecked")
	public List<T> getValue() {
		return list;
	}
}