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
package de.metadocks.lambdaui.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.metadocks.lambdaui.internal.expr.ExprParser;
import de.metadocks.lambdaui.internal.expr.ExprParser.Element;
import de.metadocks.lambdaui.internal.expr.ExprParser.Node;
import de.metadocks.lambdaui.internal.expr.ExprParser.TextNode;

public class ExprParserTest {
	@Test
	public void testParseTree() {
		ExprParser parser = new ExprParser();
		List<Node> nodes = parser
				.parseTree("{Binding path=address.zipCode, converter={StringToZip from=String, to=zip}, mode=OneWay}");
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		assertTrue(nodes.get(0) instanceof Element);

		Element node = (Element) nodes.get(0);
		assertNotNull(node);
		assertEquals("Binding", node.name);
		assertEquals(3, node.children.size());

		assertTrue(node.children.get("path") instanceof TextNode);
		assertTrue(node.children.get("converter") instanceof Element);
		assertTrue(node.children.get("mode") instanceof TextNode);

		{
			TextNode child = (TextNode) node.children.get("path");
			assertEquals("address.zipCode", child.value);
		}

		{
			Element converterNode = (Element) node.children.get("converter");
			assertEquals("StringToZip", converterNode.name);

			assertEquals(2, converterNode.children.size());
			assertTrue(converterNode.children.get("from") instanceof TextNode);
			assertTrue(converterNode.children.get("to") instanceof TextNode);

			assertEquals("String", ((TextNode) converterNode.children.get("from")).value);
			assertEquals("zip", ((TextNode) converterNode.children.get("to")).value);
		}

		{
			TextNode child = (TextNode) node.children.get("mode");
			assertEquals("OneWay", child.value);
		}
	}

	@Test
	public void testParseNoInitialElementName() throws Exception {
		ExprParser parser = new ExprParser();
		List<Node> nodes = parser.parseTree("{a=one, b=two}");
		assertNotNull(nodes);
		assertEquals(1, nodes.size());
		assertTrue(nodes.get(0) instanceof Element);

		Element node = (Element) nodes.get(0);
		assertNotNull(node);
		assertEquals(null, node.name);
		assertEquals(2, node.children.size());
	}

	@Test
	public void testMultiNodesExpr() throws Exception {
		ExprParser parser = new ExprParser();
		List<Node> nodes = parser.parseTree("Hello {Binding path=firstName} {path=lastName}!");
		assertNotNull(nodes);
		assertEquals(5, nodes.size());

		{
			assertTrue(nodes.get(0) instanceof TextNode);
			TextNode node = (TextNode) nodes.get(0);
			assertEquals("Hello ", node.value);
		}

		{
			assertTrue(nodes.get(1) instanceof Element);
			Element node = (Element) nodes.get(1);
			assertEquals("Binding", node.name);
			assertEquals("firstName", ((TextNode) node.children.get("path")).value);
		}

		{
			assertTrue(nodes.get(2) instanceof TextNode);
			TextNode node = (TextNode) nodes.get(2);
			assertEquals(" ", node.value);
		}

		{
			assertTrue(nodes.get(3) instanceof Element);
			Element node = (Element) nodes.get(3);
			assertEquals(null, node.name);
			assertEquals("lastName", ((TextNode) node.children.get("path")).value);
		}

		{
			assertTrue(nodes.get(4) instanceof TextNode);
			TextNode node = (TextNode) nodes.get(4);
			assertEquals("!", node.value);
		}
	}
}
