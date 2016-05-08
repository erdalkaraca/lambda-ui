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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.metadocks.lambdaui.internal.expr.ExprParser;
import de.metadocks.lambdaui.internal.expr.ExprParser.Element;
import de.metadocks.lambdaui.internal.expr.ExprParser.TextNode;

public class ExprParserTest {
	@Test
	public void testParseTree() {
		ExprParser parser = new ExprParser();
		Element node = parser
				.parseTree("{Binding path=address.zipCode, converter={StringToZip from=String, to=zip}, mode=OneWay}");
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
}
