# lambda-ui
A lambda driven UI creation collection with support for Eclipse SWT, JFace, Databinding to help to reduce/eliminate boilerplate UI code.

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

![Output](https://raw.githubusercontent.com/erdalkaraca/lambda-ui/master/screenshots/hellow-world.png "Output")

## Databinding

A sub class (a bit enhanced) of the Eclipse XWT binding expressions are supported:

	public void createUI(SwtUI<Composite> root) {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		root.bindingMaster(person);

		root.layout(GridLayoutFactory.swtDefaults().numColumns(2).create())//
				.child(() -> SwtUI.create(Label::new)//
						.text("First Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text(SWT.Modify, "{Binding path=firstName, delay=200}"))//
				.child(() -> SwtUI.create(Label::new)//
						.text("Last Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text(SWT.Modify, "{path=lastName, delay=200}"))//
				.child(() -> SwtUI.create(Label::new)//
						.text("Complete Name"))//
				.child(() -> SwtUI.create(Text::new, SWT.BORDER)//
						.layoutData(new GridData(GridData.FILL_HORIZONTAL))//
						.text("Name: {path=firstName} {path=lastName}")//
		);
	}

![Multi](https://raw.githubusercontent.com/erdalkaraca/lambda-ui/master/screenshots/multi-binding.png "Computed/Multi Binding")

### Databinding Features (supported at the moment)
- Simple property value bindings
- multi bindings
- auto-conversion of values (from/to)
- delayed widget properties bindings

### Binding expressions
Examples:

- **"{Binding path=beanPropertyName, delay=200}"**:
  binds the property of the widget to the bean property called 'beanPropertyName', when a two-way-binding is set (default),
  changes from the widget to the bean are transferred delayed (200 ms here)
  
- **"{path=beanPropertyName, delay=200}"**: same as above, but shortened as the 'Binding' qualifier of the expression is optional

- **"Name: {path=firstName} {path=lastName}"**: a computed/multi binding which will be automatically constructed using
  both props called firstName and lastName