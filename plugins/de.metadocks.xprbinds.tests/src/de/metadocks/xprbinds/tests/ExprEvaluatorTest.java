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
package de.metadocks.xprbinds.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.metadocks.xprbinds.binding.internal.Binding;
import de.metadocks.xprbinds.internal.expr.ExprEvaluator;
import de.metadocks.xprbinds.internal.expr.ExprParser;
import de.metadocks.xprbinds.internal.expr.ExprParser.Node;

public class ExprEvaluatorTest {
	@Test
	public void testEvaluate() {
		List<Node> parseTree = new ExprParser().parseTree("{path=name, delay=200}");
		assertEquals(parseTree.size(), 1);
		ExprEvaluator evaluator = ExprEvaluator.getInstance();
		Binding binding = evaluator.evaluate(parseTree.get(0), Binding.class);
		assertNotNull(binding);
		assertEquals(binding.getPath(), "name");
		assertEquals(binding.getDelay(), 200);
	}
}
