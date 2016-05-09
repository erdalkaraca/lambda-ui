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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.Assert;

public class ExprParser {
	public static class Node {
	}

	public static class Element extends Node {
		public String expr;
		public String name;
		public Map<String, Node> children = new LinkedHashMap<>();
	}

	public static class TextNode extends Node {
		public String value;
	}

	private Stack<Node> stack = new Stack<>();
	private String expr;
	private int currentPos;

	public List<Node> parseTree(String expr) {
		Assert.isNotNull(expr);
		Assert.isTrue(!expr.isEmpty());

		this.expr = expr;
		currentPos = 0;
		List<Node> nodes = new ArrayList<>();

		while (currentPos < expr.length()) {
			boolean isElement = lookAhead("{");
			Node node;

			if (isElement) {
				node = consumeElement();
			} else {
				node = consumeText();
			}

			nodes.add(node);
		}

		return nodes;
	}

	private TextNode consumeText() {
		TextNode node = new TextNode();
		int start = currentPos;

		while (currentPos < expr.length()) {
			if (lookAhead("{")) {
				break;
			} else {
				currentPos++;
			}
		}

		node.value = expr.substring(start, currentPos);
		return node;
	}

	private boolean lookAhead(String pattern) {
		return currentPos + pattern.length() < expr.length()
				&& pattern.equals(expr.substring(currentPos, currentPos + pattern.length()));
	}

	private Element consumeElement() {
		int start = currentPos;
		drop("{");
		consumeWhitespaces();
		Element node = new Element();
		stack.push(node);

		int posBak = currentPos;

		try {
			consumeProps();
		} catch (IllegalArgumentException e) {
			// revert last try, does not start with props
			currentPos = posBak;
			consumeName();
		}

		consumeWhitespaces();

		try {
			consumeProps();
		} catch (IllegalArgumentException e) {
			// ignore, no props given
		}

		consumeWhitespaces();
		drop("}");
		Element element = (Element) stack.pop();
		element.expr = expr.substring(start, currentPos);
		return element;
	}

	private void consumeWhitespaces() {
		for (;;) {
			if (currentPos < expr.length() && Character.isWhitespace(expr.charAt(currentPos))) {
				currentPos++;
			} else {
				break;
			}
		}
	}

	private void consumeProps() {
		for (;;) {
			String name = consumeIdentifier();
			consumeWhitespaces();
			drop("=");
			consumeWhitespaces();

			try {
				Node consumeNode = consumeElement();
				peekElement().children.put(name, consumeNode);
			} catch (IllegalArgumentException e) {
				// ignore, not a node
				TextNode right = new TextNode();
				right.value = consumeIdentifier();
				peekElement().children.put(name, right);
			}

			consumeWhitespaces();

			try {
				drop(",");
				consumeWhitespaces();
			} catch (IllegalArgumentException e) {
				// no properties left
				break;
			}
		}
	}

	private Element peekElement() {
		return (Element) stack.peek();
	}

	private void consumeName() {
		if (!Character.isJavaIdentifierStart(expr.charAt(currentPos))) {
			throw new IllegalArgumentException("invalid type name encountered");
		}

		peekElement().name = consumeIdentifier();
	}

	private String consumeIdentifier() {
		StringBuilder nameBuilder = new StringBuilder();
		char charAt;

		for (;;) {
			charAt = expr.charAt(currentPos);

			if (Character.isJavaIdentifierPart(charAt) || charAt == '.') {
				nameBuilder.append(charAt);
				currentPos++;
			} else {
				break;
			}
		}

		return nameBuilder.toString();
	}

	private void drop(String pattern) {
		String expectedToken = expr.substring(currentPos, currentPos + pattern.length());

		if (!pattern.equals(expectedToken)) {
			throw new IllegalArgumentException("expected " + pattern);
		}

		currentPos += pattern.length();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(expr);
		sb.insert(currentPos, '|');
		return sb.toString();
	}
}
