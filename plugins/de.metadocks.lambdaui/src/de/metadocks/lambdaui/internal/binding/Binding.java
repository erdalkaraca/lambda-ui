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
package de.metadocks.lambdaui.internal.binding;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;

public class Binding {
	public static enum Mode {
		OneWay, TwoWay, OneTime
	}

	public static enum UpdateEvent {
		FocusLost, Modify, Default
	}

	private String path;
	private IConverter converter;
	private Mode mode;
	private int delay = 0;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public IConverter getConverter() {
		return converter;
	}

	public void setConverter(IConverter converter) {
		this.converter = converter;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public IObservableValue observe(Object dataContext) {
		if (dataContext instanceof IObservableValue) {
			IObservableValue detailValue = MasterDetailObservables.detailValue((IObservableValue) dataContext,
					target -> observe(target), null);
			return detailValue;
		}

		IBeanValueProperty value = BeanProperties.value(dataContext.getClass(), path);
		IObservableValue observableValue = value.observe(dataContext);
		return observableValue;
	}

	public void setExpression(String expr) {

	}
}
