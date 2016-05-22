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
package de.metadocks.lambdaui.conversion;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;

public class ConvertersRegistry {
	private static ConvertersRegistry INSTANCE = new ConvertersRegistry();
	private HijackedUpdateValueStrategy strategy = new HijackedUpdateValueStrategy();

	public static ConvertersRegistry getInstance() {
		return INSTANCE;
	}

	public IConverter getConverter(Class<?> from, Class<?> to) {
		IConverter converter = strategy.createConverter(from, to);

		if (converter != null) {
			return converter;
		}

		// TODO allow for user registered converters lookup
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Object propValue, Class<T> valueType) {
		if (propValue == null) {
			return null;
		}

		if (valueType.equals(propValue.getClass())) {
			return (T) propValue;
		}

		IConverter converter = getConverter(propValue.getClass(), valueType);

		if (converter != null) {
			T convertedValue = (T) converter.convert(propValue);
			return convertedValue;
		}

		throw new UnsupportedOperationException(
				"Could not convert from '" + propValue.getClass() + "' to '" + valueType + "'");
	}

	private static class HijackedUpdateValueStrategy extends UpdateValueStrategy {
		public IConverter createConverter(Object fromType, Object toType) {
			return super.createConverter(fromType, toType);
		}
	}
}
