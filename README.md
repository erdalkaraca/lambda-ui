# lambda-ui
A lambda driven UI creation collection with support for Eclipse SWT, JFace, Draw2d to help to reduce/eliminate boilerplate UI code.

## SWT sample

Conventional UI code:

	private Text text;

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
	
With lambda support:

	private SwtUI<Composite> root;

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
	
Both samples produce the following same output:

![Outpu](https://raw.githubusercontent.com/erdalkaraca/lambda-ui/master/screenshots/hellow-world.png "Output") 