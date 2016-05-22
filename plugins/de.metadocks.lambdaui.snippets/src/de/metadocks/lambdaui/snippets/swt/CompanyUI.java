package de.metadocks.lambdaui.snippets.swt;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.metadocks.lambdaui.company.Company;
import de.metadocks.lambdaui.company.Employee;
import de.metadocks.lambdaui.swt.SwtUI;
import de.metadocks.lambdaui.swt.ViewerUI;

public class CompanyUI {
	private IObservableValue companyMaster = new WritableValue(null, Company.class);

	public void createUI(SwtUI<? extends Composite> root) {
		root.dataContext(companyMaster);
		root.layout(GridLayoutFactory.swtDefaults().numColumns(2).create())//
				.child(() -> SwtUI.create(Label::new).text("Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER).text("{path=name}")
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
				.child(() -> SwtUI.create(Label::new).text("Employees")
						.layoutData(GridDataFactory.swtDefaults().span(2, 1).grab(true, false)
								.align(SWT.FILL, SWT.CENTER).create()))//
				.child(() -> ViewerUI.createViewer(TableViewer::new, SWT.BORDER)//
						.customizeViewer(this::customizeEmployees)//
						.input("{path=employees}")//
						.layoutData(GridDataFactory.swtDefaults().span(2, 1).grab(true, true).align(SWT.FILL, SWT.FILL)
								.create()))//
		;
	}

	public void customizeEmployees(TableViewer viewer) {
		viewer.setContentProvider(new ObservableListContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Employee emp = (Employee) element;
				return String.format("%s %s (id=%d)", emp.getFirstName(), emp.getLastName(), emp.getId());
			}
		});
	}

	public void setCompany(Company company) {
		companyMaster.setValue(company);
	}

	public static void main(String[] args) {
		SwtUI.openInShell(shell -> {
			CompanyUI companyUI = new CompanyUI();
			companyUI.createUI(shell);
			Company company = new Company();
			company.setName("Test Company");

			for (int i = 0; i < 10; i++) {
				Employee e = new Employee();
				e.setFirstName("First " + i);
				e.setLastName("Last");
				e.setId(i);
				company.getEmployees().add(e);
			}

			companyUI.setCompany(company);
		});
	}
}
