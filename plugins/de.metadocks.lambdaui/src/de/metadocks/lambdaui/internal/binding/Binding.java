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

import org.eclipse.core.databinding.conversion.IConverter;

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
	private String expr;

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

	public void setExpression(String expr) {
		this.expr = expr;
	}

	@Override
	public String toString() {
		return expr != null ? expr : super.toString();
	}
}
