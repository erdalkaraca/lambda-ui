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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.metadocks.lambdaui.company.Person;
import de.metadocks.lambdaui.swt.SwtUI;

public class BindingExample {
	public void createUI(SwtUI<Composite> root) {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		root.dataContext(person);

		root.layout(GridLayoutFactory.swtDefaults().numColumns(2).create())//
				.childControl(() -> SwtUI.create(Label::new)//
						.text("First Name"))//
				.childControl(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text(SWT.Modify, "{Binding path=firstName, delay=200}"))//
				.childControl(() -> SwtUI.create(Label::new)//
						.text("Last Name"))//
				.childControl(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text(SWT.Modify, "{path=lastName, delay=200}"))//
				.childControl(() -> SwtUI.create(Label::new)//
						.text("Complete Name"))//
				.childControl(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text("Name: {path=firstName} {path=lastName}")//
		);
	}

	public static void main(String[] args) {
		SwtUI.openInShell(shell -> new BindingExample().createUI(SwtUI.wrap(shell.control())));
	}
}
