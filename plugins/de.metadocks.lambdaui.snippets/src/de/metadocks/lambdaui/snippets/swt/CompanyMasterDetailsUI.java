package de.metadocks.lambdaui.snippets.swt;

import java.util.Date;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import de.metadocks.lambdaui.company.Company;
import de.metadocks.lambdaui.company.Employee;
import de.metadocks.lambdaui.swt.SwtUI;

public class CompanyMasterDetailsUI {
	private CompanyUI companyUI;
	private EmployeeUI employeeUI;

	public void createUI(SwtUI<? extends Composite> root) {
		root.childControl(() -> SwtUI.create(SashForm::new)//
				.childControl(() -> SwtUI.create(Composite::new).customizeUI(this::createMasterUI))//
				.childControl(() -> SwtUI.create(Composite::new).customizeUI(this::createDetailsUI))//
				.customize(sash -> {
					sash.setWeights(new int[] { 40, 60 });
				}));
	}

	private void createMasterUI(SwtUI<? extends Composite> root) {
		companyUI = new CompanyUI();
		companyUI.createUI(root);
		root.withViewerUI(CompanyUI.EMPLOYEES_VIEWER, viewer -> {
			viewer.viewer().addSelectionChangedListener(evt -> {
				IStructuredSelection selection = (IStructuredSelection) evt.getSelection();
				Object firstElement = selection.getFirstElement();
				employeeUI.setInput((Employee) firstElement);
			});
		});
	}

	private void createDetailsUI(SwtUI<? extends Composite> root) {
		employeeUI = new EmployeeUI();
		employeeUI.createUI(root);
	}

	public static void main(String[] args) {
		SwtUI.openInShell(shell -> {
			CompanyMasterDetailsUI ui = new CompanyMasterDetailsUI();
			ui.createUI(shell);
			Company company = new Company();
			company.setName("Test Company");

			for (int i = 0; i < 10; i++) {
				Employee e = new Employee();
				e.setFirstName("First " + i);
				e.setLastName("Last");
				e.setId(i);
				e.setBirthdate(new Date());
				company.getEmployees().add(e);
			}

			ui.companyUI.setCompany(company);
		});
	}
}
