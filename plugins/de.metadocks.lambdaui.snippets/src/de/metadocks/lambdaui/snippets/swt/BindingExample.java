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
package de.metadocks.lambdaui.snippets.swt;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.metadocks.lambdaui.snippets.model.Person;
import de.metadocks.lambdaui.swt.SwtUI;

public class BindingExample {
	public void createUI(SwtUI<Composite> root) {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		root.bindingMaster(person);

		root.layout(GridLayoutFactory.swtDefaults().numColumns(2).create())//
				.child(() -> SwtUI.create(Label::new)//
						.text("First Name"))//
				.child(() -> SwtUI.create(Text::new)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text("{Binding path=firstName}"))//
				.child(() -> SwtUI.create(Label::new)//
						.text("Last Name"))//
				.child(() -> SwtUI.create(Text::new)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text("{Binding path=lastName}")//
		);
	}

	public static void main(String[] args) {
		SwtUI.openInShell(shell -> new BindingExample().createUI(SwtUI.wrap(shell.control())));
	}
}
