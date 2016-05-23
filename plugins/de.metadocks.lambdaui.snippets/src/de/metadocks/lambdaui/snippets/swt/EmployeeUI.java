package de.metadocks.lambdaui.snippets.swt;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.metadocks.lambdaui.company.Company;
import de.metadocks.lambdaui.company.Employee;
import de.metadocks.lambdaui.swt.SwtUI;

public class EmployeeUI {
	private IObservableValue input = new WritableValue(null, Company.class);

	public void setInput(Employee empl) {
		input.setValue(empl);
	}

	public void createUI(SwtUI<? extends Composite> root) {
		root.dataContext(input);

		root.layout(GridLayoutFactory.swtDefaults().numColumns(2).create())//
				.child(() -> SwtUI.create(Label::new)
						.text("Employee: {path=firstName} {path=lastName} (born {path=birthdate})").layoutData(
								GridDataFactory.createFrom(new GridData(GridData.FILL_HORIZONTAL)).span(2, 1).create()))//
				.child(() -> SwtUI.create(Label::new).text("First Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER).text(SWT.Modify, "{path=firstName}")
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
				.child(() -> SwtUI.create(Label::new).text("Last Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER).text(SWT.Modify, "{path=lastName}")
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
				.child(() -> SwtUI.create(Label::new).text("ID"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER).text(SWT.Modify, "{path=id}")
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
				.child(() -> SwtUI.create(Label::new).text("Birth Date"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER).text(SWT.Modify, "{path=birthdate}")
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
		;
	}
}
