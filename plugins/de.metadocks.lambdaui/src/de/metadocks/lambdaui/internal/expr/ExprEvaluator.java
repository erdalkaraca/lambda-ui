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
package de.metadocks.lambdaui.internal.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;

import de.metadocks.lambdaui.conversion.ConvertersRegistry;
import de.metadocks.lambdaui.internal.binding.Binding;
import de.metadocks.lambdaui.internal.expr.ExprParser.Element;
import de.metadocks.lambdaui.internal.expr.ExprParser.Node;
import de.metadocks.lambdaui.internal.expr.ExprParser.TextNode;

public class ExprEvaluator {
	private static final ExprEvaluator INSTANCE = new ExprEvaluator();

	private Map<String, Class<?>> typesLookup = new HashMap<>();

	public ExprEvaluator() {
		typesLookup.put(Binding.class.getSimpleName(), Binding.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluate(Node node, Class<T> elementType) {
		if (node instanceof TextNode) {
			return (T) ((TextNode) node).value;
		} else if (node instanceof Element) {
			Element element = (Element) node;
			Class<T> type;

			if (element.name == null) {
				type = elementType;
			} else {
				type = (Class<T>) typesLookup.get(element.name);
			}

			T ret;

			try {
				ret = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Could not instantiate type: " + element.name);
			}

			for (Entry<String, Node> e : element.children.entrySet()) {
				String propName = e.getKey();
				IBeanValueProperty value = BeanProperties.value(ret.getClass(), propName);
				// derive value type from property type of bean if not provided
				// in AST
				Class<?> valueType = (Class<?>) value.getValueType();
				Object propValue = evaluate(e.getValue(), valueType);
				propValue = ConvertersRegistry.getInstance().convert(propValue, valueType);
				value.setValue(ret, propValue);
			}

			return ret;
		}

		return null;

	}

	public static ExprEvaluator getInstance() {
		return INSTANCE;
	}
}
