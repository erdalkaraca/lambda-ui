package de.metadocks.lambdaui.snippets.swt;

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.metadocks.lambdaui.swt.SwtUI;
import de.metadocks.lambdaui.swt.ViewerUI;

public class HelloWorld {

	public void createUIConventional(Composite parent) {
		parent.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		Label label = new Label(parent, SWT.NONE);
		label.setText("Selection");

		ComboViewer viewer = new ComboViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		customizeComboViewer(viewer);
		viewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button button = new Button(parent, SWT.NONE);
		button.setText("Apply");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText("Selection:  " + viewer.getCombo().getText());
			}
		});

		text = new Text(parent, SWT.READ_ONLY | SWT.BORDER);
		text.setLayoutData(
				GridDataFactory.swtDefaults().span(3, 1).grab(true, true).align(SWT.FILL, SWT.FILL).create());
	}

	private void customizeComboViewer(ComboViewer viewer) {
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(element);
			}
		});
		viewer.setInput(Arrays.asList("One", "Two", "Three"));
	}

	private SwtUI<Composite> root;
	private Text text;

	public void createUI(Composite parent) {
		root = SwtUI.wrap(parent);
		root.layout(GridLayoutFactory.swtDefaults().numColumns(3).create())//
				.child(() -> SwtUI.create(Label::new)//
						.text("Selection"))//
				.child(() -> ViewerUI.createViewer(ComboViewer::new, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY)//
						.id("selectionCombo")//
						.customizeViewer(this::customizeComboViewer)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL)))//
				.child(() -> SwtUI.create(Button::new)//
						.text("Apply")//
						.on(SWT.Selection, this::onButtonClick))//
				.child(() -> SwtUI.create(Text::new, SWT.READ_ONLY | SWT.BORDER)//
						.id("textField")//
						.layoutData(GridDataFactory.swtDefaults().span(3, 1).grab(true, true).align(SWT.FILL, SWT.FILL)
								.create()));
	}

	private void onButtonClick(Event evt) {
		Text tf = root.find("textField");
		ComboViewer viewer = root.findViewer("selectionCombo");
		tf.setText("Selection: " + viewer.getCombo().getText());
	}

	public static void main(String[] args) {
		SwtUI.openInShell(shell -> new HelloWorld().createUIConventional(shell.text("Hello world").control()));
	}
}
