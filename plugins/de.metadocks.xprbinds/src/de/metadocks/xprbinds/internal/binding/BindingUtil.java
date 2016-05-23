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
package de.metadocks.xprbinds.internal.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;

import de.metadocks.xprbinds.conversion.ConvertersRegistry;
import de.metadocks.xprbinds.internal.expr.ExprEvaluator;
import de.metadocks.xprbinds.internal.expr.ExprParser;
import de.metadocks.xprbinds.internal.expr.ExprParser.Element;
import de.metadocks.xprbinds.internal.expr.ExprParser.Node;
import de.metadocks.xprbinds.internal.expr.ExprParser.TextNode;

public class BindingUtil {
	
}
