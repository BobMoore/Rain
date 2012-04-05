package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.CodeStep;
import com.follett.mywebapp.util.SetupDataItem;
import com.follett.mywebapp.util.SingleTag;
import com.follett.mywebapp.util.StepHolder;
import com.follett.mywebapp.util.StepTableData;
import com.follett.mywebapp.util.TableData;
import com.follett.mywebapp.util.TextboxIDHolder;
import com.follett.mywebapp.util.ValidationTreeNode;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class mywebapp implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private FlexTable stepFlexTable;
	private int setupRow = 0;
	private int validationRow = 0;
	private boolean editTree = false;
	private ArrayList<String> validationSteps = new ArrayList<String>();
	private ArrayList<String> identifierKey = new ArrayList<String>();
	private int startKey = 10;
	private LayoutPanel treePanel;
	private LayoutPanel setupPanel;
	private LayoutPanel mainPanel = new LayoutPanel();
	final ScrollPanel flexPanel = new ScrollPanel();
	TextBox testNumber;
	private Boolean databasePresent = Boolean.FALSE;

	private SetupInput setupDialogBox;
	@SuppressWarnings("unused")
	private SetupValidation setupValidationBox;


	private CodeBuilderServiceAsync codeBuildingService = GWT.create(CodeBuilderService.class);
	private DatabaseBuilderServiceAsync databaseBuildingService = GWT.create(DatabaseBuilderService.class);

	public void onModuleLoad() {

		AsyncCallback<Boolean> callback = checkDatabaseCallback();
		this.databaseBuildingService.checkDatabase(callback);
	}

	public void paintMainPage() {

		final Button saveButton = new Button("Save Test");
		this.testNumber = new TextBox();
		final Button loadTest = new Button("Load Test");
		final Button generateCode = new Button("Generate Code");
		final Button editSetup = new Button("Edit Setup");
		final Button clearTable = new Button("Clear Test");
		this.treePanel = new LayoutPanel();
		this.setupValidationBox = new SetupValidation(this.treePanel, new TreeHandler());
		this.setupPanel = new LayoutPanel();
		this.setupDialogBox = new SetupInput(this.setupPanel);
		final SplitLayoutPanel testDevelopementPanel = new SplitLayoutPanel();
		setStepFlexTable(new FlexTable());

		RootLayoutPanel rp = RootLayoutPanel.get();
		rp.add(testDevelopementPanel);

		testDevelopementPanel.setWidth(""+ rp.getOffsetWidth() + "px");
		int offsetWidth85 = (int) (rp.getOffsetWidth() * .85);
		this.mainPanel.setWidth("" + offsetWidth85 + "px");
		this.setupPanel.setWidth("" + offsetWidth85 + "px");
		int offsetWidth15 = (int) (rp.getOffsetWidth()*.15);
		this.treePanel.setWidth("" + offsetWidth15 + "px");
		int offsetHeight75 = (int) (rp.getOffsetHeight() * .75);
		int offsetHeight25 = (int) (rp.getOffsetHeight() * .25);


		buildStepPanel(saveButton, this.testNumber, generateCode, editSetup, this.mainPanel, this.flexPanel, loadTest, clearTable, offsetWidth85, offsetHeight75);
		buildMainPanel(this.mainPanel, this.setupPanel, testDevelopementPanel, this.treePanel, offsetWidth15, offsetHeight25);

		buildStepTable();

		//Listeners
		CodeDialog cHandler = new CodeDialog();
		generateCode.addClickHandler(cHandler);
		SaveHandler saveHandler = new SaveHandler();
		saveButton.addClickHandler(saveHandler);
		SetupDialog setupHandler = new SetupDialog();
		editSetup.addClickHandler(setupHandler);
		LoadDialog loadTestHandler = new LoadDialog();
		loadTest.addClickHandler(loadTestHandler);
		TestClear clear = new TestClear();
		clearTable.addClickHandler(clear);
	}

	class SetupDialog implements ClickHandler {

		DialogBox dialog;

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = mywebapp.this.setupDialogBox.createSetupDialogBox();
			this.dialog.show();
			this.dialog.center();
		}
	}

	class CodeDialog implements ClickHandler {

		DialogBox dialog;

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = createCodeDialogBox();
			this.dialog.show();
			this.dialog.center();
		}
	}

	class LoadDialog implements ClickHandler {

		DialogBox dialog;

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = createLoadDialog();
			this.dialog.show();
			this.dialog.center();
			LayoutPanel panel = (LayoutPanel) this.dialog.getWidget();
			int index = -1;
			for(int a = 0; a < panel.getWidgetCount(); a++) {
				if(panel.getWidget(a) instanceof TextBox) {
					index = a;
				}
			}
			if(index > 0) {
				final TextBox myBox = (TextBox)panel.getWidget(index);
				if(myBox != null) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute () {
							myBox.setFocus(true);
						}
					});
				}
			}
		}
	}

	class TestClear implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			mywebapp.this.buildStepTable();
		}
	}

	class SaveHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			if (mywebapp.this.codeBuildingService == null) {
				mywebapp.this.codeBuildingService = GWT.create(SetupBuilderService.class);
			}

			CodeContainer testCode = extractCode();

			AsyncCallback<Boolean> callback = extractCodeCallback();
			mywebapp.this.codeBuildingService.saveTest(Integer.valueOf(mywebapp.this.testNumber.getText()).intValue(), testCode.toString(), callback);
		}
	}

	public DialogBox createCodeDialogBox() {

		final DialogBox dialogBox = new DialogBox(false);

		Button closeButton = new Button(
				"Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				});

		dialogBox.setWidget(buildCodeDialog(closeButton));
		dialogBox.setGlassEnabled(true);
		return dialogBox;
	}

	private LayoutPanel buildCodeDialog(Button closeButton) {
		LayoutPanel panel = new LayoutPanel();
		if (mywebapp.this.codeBuildingService == null) {
			mywebapp.this.codeBuildingService = GWT.create(SetupBuilderService.class);
		}

		CodeContainer testCode = extractCode();
		panel.setSize("500px", "250px");
		final Label label = new Label();
		panel.add(label);
		panel.add(closeButton);
		panel.setWidgetBottomHeight(closeButton, 1, Unit.PX, 30, Unit.PX);

		AsyncCallback<String> callback = generateCodeCallback(label);
		mywebapp.this.codeBuildingService.generateTemplatedCode(testCode, callback);

		return panel;
	}

	public DialogBox createDatabaseDialogBox() {

		final DialogBox dialogBox = new DialogBox(false);

		Button closeButton = new Button(
				"Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						if(getDatabasePresent().booleanValue()) {
							dialogBox.hide();
							paintMainPage();
						}
					}
				});

		dialogBox.setWidget(buildDatabaseDialog(closeButton));
		dialogBox.setGlassEnabled(true);
		return dialogBox;
	}

	private LayoutPanel buildDatabaseDialog(final Button closeButton) {
		final LayoutPanel panel = new LayoutPanel();
		if (mywebapp.this.codeBuildingService == null) {
			mywebapp.this.codeBuildingService = GWT.create(SetupBuilderService.class);
		}

		panel.setSize("500px", "250px");
		final Label label = new Label();
		label.setText("Rain was unable to detect a database. Would you like to create one now?");
		panel.add(label);
		Button generateDB = new Button("Generate!");
		panel.add(generateDB);
		panel.setWidgetBottomHeight(generateDB, 32, Unit.PX, 30, Unit.PX);
		panel.add(closeButton);
		panel.setWidgetBottomHeight(closeButton, 1, Unit.PX, 30, Unit.PX);

		generateDB.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<Boolean> callback = buildDatabaseCallback(closeButton);
				mywebapp.this.databaseBuildingService.buildDatabase(callback);
			}
		});

		return panel;
	}

	public DialogBox createLoadDialog() {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox(false);

		Button closeButton = new Button(
				"Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				});

		//evaluate the size of their window and make this the bulk of it.
		dialogBox.setWidget(buildLoadTestDialog(closeButton));
		dialogBox.setGlassEnabled(true);

		return dialogBox;
	}

	private LayoutPanel buildLoadTestDialog(final Button closeButton) {
		LayoutPanel panel = new LayoutPanel();
		panel.setSize("90px", "160px");
		Label label = new Label();
		label.setText("Test Number:");
		panel.add(label);
		panel.setWidgetTopHeight(label, 1, Unit.PX, 20, Unit.PX);
		final TextBox testNumberLocal = new TextBox();
		panel.add(testNumberLocal);
		if(!mywebapp.this.testNumber.getText().isEmpty()) {
			testNumberLocal.setText(mywebapp.this.testNumber.getText());
		}
		testNumberLocal.setFocus(true);
		panel.setWidgetTopHeight(testNumberLocal, 21, Unit.PX, 30, Unit.PX);
		final Label errorLabel = new Label();
		panel.add(errorLabel);
		panel.setWidgetTopHeight(errorLabel, 51, Unit.PX, 47, Unit.PX);

		class TestLoader implements ClickHandler{

			@Override
			public void onClick(ClickEvent event) {
				if (mywebapp.this.codeBuildingService == null) {
					mywebapp.this.codeBuildingService = GWT.create(CodeBuilderService.class);
				}

				AsyncCallback<Boolean> callbackCheck = doesTestExistCallback(closeButton, errorLabel);
				try {
					mywebapp.this.codeBuildingService.doesTestExist(Integer.valueOf(testNumberLocal.getText()).intValue(), callbackCheck);
				} catch (NumberFormatException e) {
					errorLabel.setText("Bad test number");
				}
				AsyncCallback<String> callbackTest = getTestCallback();
				try {
					mywebapp.this.codeBuildingService.getTest(Integer.valueOf(testNumberLocal.getText()).intValue(), callbackTest);
				}catch(NumberFormatException e) {
					errorLabel.setText("Bad test number");
				}
			}
		}
		final Button loadTest = new Button("Load");
		TestLoader loader = new TestLoader();
		loadTest.addClickHandler(loader);
		panel.add(loadTest);
		panel.setWidgetBottomHeight(loadTest, 32, Unit.PX, 30, Unit.PX);

		class ButtonClicker implements KeyPressHandler{

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					loadTest.click();
				}
			}
		}
		ButtonClicker testNumberEnter = new ButtonClicker();
		testNumberLocal.addKeyPressHandler(testNumberEnter);
		panel.add(closeButton);
		panel.setWidgetBottomHeight(closeButton, 1, Unit.PX, 30, Unit.PX);
		return panel;
	}

	private void buildStepPanel(final Button saveButton, TextBox localTestNumber, final Button generateCode,
			final Button editSetup, final LayoutPanel mp, ScrollPanel localFlex, Button loadTest, Button clearTable, int offsetWidth, int offsetHeight75) {
		int buttonHeightOffset = (int) (offsetHeight75 * .05);
		int buttonTopOffset = (int) (offsetHeight75 * .01);
		int buttonWidthOffset = (int) (offsetWidth * .15);
		double sixButtons = 100/6;
		ArrayList<Object> buttons = new ArrayList<Object>();
		buttons.add(saveButton);
		buttons.add(localTestNumber);
		buttons.add(loadTest);
		buttons.add(generateCode);
		buttons.add(editSetup);
		buttons.add(clearTable);
		for(int a = 0; a < 6; a++) {
			mp.add((Widget) buttons.get(a));
			mp.setWidgetLeftWidth((Widget) buttons.get(a), (offsetWidth * ((.01 * (sixButtons * a)) + .02)), Unit.PX, buttonWidthOffset, Unit.PX);
			mp.setWidgetTopHeight((Widget) buttons.get(a), buttonTopOffset, Unit.PX, buttonHeightOffset, Unit.PX);
		}
		localFlex.add(this.stepFlexTable);
		mp.add(localFlex);
		mp.setWidgetLeftWidth(localFlex, offsetWidth * .01, Unit.PX, offsetWidth * .95, Unit.PX);
		mp.setWidgetTopHeight(localFlex, offsetHeight75 * .08, Unit.PX, offsetHeight75 * .92, Unit.PX);
	}

	private final class LoadTestAsync implements AsyncCallback<String> {
		private int columnIndex;
		private ArrayList<SingleTag> currentTag;

		@Override
		public void onFailure(Throwable caught) {
		}

		@Override
		public void onSuccess(String result) {
			buildStepTable();
			ArrayList<CodeStep> steps = parseSteps(result);

			AsyncCallback<ArrayList<StepTableData>> callbackStep = new AsyncCallback<ArrayList<StepTableData>>() {

				boolean firstStep = true;
				boolean lastStepSetup = true;

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(ArrayList<StepTableData> dataResult) {
					int currentRow = 0;
					StepHolder removeStepButton = null;
					Button moveUp = null;
					for (StepTableData data : dataResult) {
						if(!this.firstStep && data.isNewStep()) {
							getStepFlexTable().setWidget(currentRow, LoadTestAsync.this.columnIndex, removeStepButton);
							getStepFlexTable().setWidget(currentRow, LoadTestAsync.this.columnIndex + 1, moveUp);
							if(this.lastStepSetup) {
								bumpSetupRow();
							} else {
								bumpValidationRow();
							}
							bumpStartKey();
						}
						currentRow = (data.isSetup()) ? getSetupRow(): getValidationRow();
						if(data.isNewStep()) {
							removeStepButton = new StepHolder("x");
							moveUp = new Button("Move Up");
							bumpStartKey();
							addIdentifierKey(currentRow, getStartKey() + "");
							removeStepButton.addClickHandler(new RemoveStepHandler(getStartKey() + "", (data.isSetup())?0:1));
							moveUp.addClickHandler(new MoveUpStepHandler(getStartKey() + "", (data.isSetup())?0:1));
							getStepFlexTable().insertRow(currentRow);
							LoadTestAsync.this.columnIndex = 0;
							this.firstStep = false;
							this.lastStepSetup = data.isSetup();
						}
						String stepLabel = data.getLabel();
						getStepFlexTable().setText(currentRow, LoadTestAsync.this.columnIndex, stepLabel);
						LoadTestAsync.this.columnIndex++;
						if(data.getTextfields() != null) {
							ArrayList<String> descriptions = data.getDescriptions();
							ArrayList<String> params = new ArrayList<String>();
							if(LoadTestAsync.this.currentTag.size() > 0 && data.getTagID().equals(LoadTestAsync.this.currentTag.get(0).getTag())) {
								params = LoadTestAsync.this.currentTag.get(0).getParams();
								LoadTestAsync.this.currentTag.remove(0);
							} else {
								params = new ArrayList<String>();
							}
							for(int a = 0; a < data.getTextfields().intValue(); a++) {
								TextboxIDHolder box = new TextboxIDHolder(data.getTagID());
								if(a < descriptions.size()) {
									box.setTitle(descriptions.get(a));
								}
								if(a < params.size()) {
									box.setText(params.get(a));
								}
								getStepFlexTable().setWidget(currentRow, LoadTestAsync.this.columnIndex, box);
								LoadTestAsync.this.columnIndex++;
							}
						}
						removeStepButton.addTagID(data.getTagID());
					}
					if(removeStepButton != null) {
						getStepFlexTable().setWidget(currentRow, LoadTestAsync.this.columnIndex, removeStepButton);
						getStepFlexTable().setWidget(currentRow, LoadTestAsync.this.columnIndex + 1, moveUp);
						if(this.lastStepSetup) {
							bumpSetupRow();
						} else {
							bumpValidationRow();
						}
						bumpStartKey();
					}
				}
			};

			String tags = "";
			boolean first = true;
			boolean vFirst = true;
			this.currentTag = new ArrayList<SingleTag>();
			for (CodeStep step : steps) {
				this.columnIndex = 0;
				final ArrayList<SingleTag> multipleTags = step.getMultiTag();
				if(step.validation()) {
					this.currentTag.add(new SingleTag(step.getTagID(), step.getVariables(), step.getTitles()));
					if(first) {
						tags += "New Step, ";
						if(vFirst) {
							tags += "Validation, ";
							vFirst = false;
						}
						tags += step.getTagID();
						first = false;
					} else {
						tags += ", New Step, ";
						if(vFirst) {
							tags += "Validation, ";
							vFirst = false;
						}
						tags += step.getTagID();
					}
				} else if (multipleTags == null) {
					this.currentTag.add(new SingleTag(step.getTagID(), step.getVariables(), step.getTitles()));
					if(!first) {
						tags += ", ";
					} else {
						first = false;
					}
					tags += step.getTagID();
				} else if (multipleTags != null) {
					if(tags.isEmpty()) {
						tags += "New Step";
						first = false;
					}else {
						tags += ", New Step";
					}

					for (SingleTag tag : multipleTags) {
						this.currentTag.add(tag);
						if(first) {
							tags += tag.getTag();
							first = false;
						} else {
							tags += ", " + tag.getTag();
						}
					}
				}
			}
			mywebapp.this.codeBuildingService.getSetupPiece(tags, callbackStep);
		}

		private ArrayList<CodeStep> parseSteps(String result){
			ArrayList<CodeStep> returnList = new ArrayList<CodeStep>();
			result = result.substring(1, result.length()-1);
			String params;
			String tagID;
			ArrayList <String> currentList = new ArrayList<String>();
			CodeStep multiStep;
			while(result.contains("[")) {
				multiStep = new CodeStep();
				if(result.charAt(0) == '[') {
					result = result.substring(1);
					while(result.indexOf(']') > 0) {
						tagID = result.substring(0, result.indexOf('[')).trim();
						result = result.substring(result.indexOf('[') + 1).trim();
						currentList = new ArrayList<String>();
						if(result.indexOf(']') > 0) {
							params = result.substring(0, result.indexOf(']'));
							result = result.substring(result.indexOf(']') + 1);
							currentList = parseParams(params);
						}
						multiStep.addTag(tagID, currentList, null);
					}
					returnList.add(multiStep);
				} else {
					tagID = result.substring(0, result.indexOf('[')).trim();
					result = result.substring(result.indexOf('[') + 1).trim();
					currentList = new ArrayList<String>();
					if(result.indexOf(']') > 0) {
						params = result.substring(0, result.indexOf(']'));
						result = result.substring(result.indexOf(']') + 1);
						currentList = parseParams(params);
					}
					returnList.add(new CodeStep(tagID, currentList, null));
				}
				if(result.contains(",")) {
					result = result.substring(result.indexOf(',') + 1).trim();
				}
			}
			return returnList;
		}

		private ArrayList <String> parseParams(String params) {
			ArrayList <String> currentList = new ArrayList<String>();
			while(params.contains(",")) {
				currentList.add(params.substring(0, params.indexOf(',')).trim());
				params = params.substring(params.indexOf(',')+1).trim();
			}
			if(!params.isEmpty()) {
				currentList.add(params);
			}
			return currentList;
		}
	}

	class AddSetupHandler implements ClickHandler {

		ArrayList<Object> items;
		HashMap<String, TableData> fullData;

		public AddSetupHandler(HashMap<String, TableData> data, ArrayList<Object> items) {
			this.fullData = data;
			this.items = items;
		}

		@Override
		public void onClick(ClickEvent event) {
			//scroll through the objects and grab which ones I need
			int column = 0;
			String label = "";
			Boolean checked = Boolean.FALSE;
			boolean rowBump = false;
			StepHolder removeStepButton = new StepHolder("x");
			Button moveUp = new Button("Move Up");
			for (Object obj : this.items) {
				if(obj instanceof CheckBox) {
					CheckBox box = (CheckBox)obj;
					label = box.getText();
					checked = box.getValue();
				}
				if(obj instanceof RadioButton) {
					RadioButton button = (RadioButton)obj;
					label = button.getText();
					checked = button.getValue();
				}
				if(checked.booleanValue()) {
					if(!rowBump) {
						getStepFlexTable().insertRow(getSetupRow());
						rowBump = true;
					}
					getStepFlexTable().setText(getSetupRow(), column, label);
					column++;
					if(this.fullData.containsKey(label)) {
						TableData data = this.fullData.get(label);
						if(data.getTextfields() != null) {
							ArrayList<String> descriptions = data.getDescriptions();
							for(int c = 0; c < data.getTextfields().intValue(); c++) {
								TextboxIDHolder box = new TextboxIDHolder(data.getTagID());
								if(c<descriptions.size()) {
									box.setTitle(descriptions.get(c));
								}
								getStepFlexTable().setWidget(getSetupRow(), column, box);
								column++;
							}
						}
						removeStepButton.addTagID(data.getTagID());
					}
				}
				if(rowBump) {
					removeStepButton.addClickHandler(new RemoveStepHandler(getStartKey() + "", 0));
					moveUp.addClickHandler(new MoveUpStepHandler(getStartKey() + "", 0));
					getStepFlexTable().setWidget(getSetupRow(), column, removeStepButton);
					getStepFlexTable().setWidget(getSetupRow(), column + 1, moveUp);
					addIdentifierKey(getSetupRow(), getStartKey() + "");
					bumpSetupRow();
					bumpStartKey();
				}
				mywebapp.this.flexPanel.setHeight((getStepFlexTable().getOffsetHeight() + 100) + "px");
			}
		}
	}

	class TreeHandler implements SelectionHandler<TreeItem>{

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			ValidationTreeNode selected = (ValidationTreeNode)event.getSelectedItem();
			getStepFlexTable().setText(getValidationRow(), 0, selected.getText());
			StepHolder removeStepButton = new StepHolder("x", selected.getTagID());
			Button moveUp = new Button("Move Up");
			int buttonOffset = 0;
			ArrayList<String> descriptions = selected.getDescriptions();
			if(selected.getFields() != null) {
				for(int a = 0; a < selected.getFields().intValue(); a++) {
					TextboxIDHolder box = new TextboxIDHolder(selected.getTagID());
					if(a < descriptions.size()) {
						box.setTitle(descriptions.get(a));
					}
					getStepFlexTable().setWidget(getValidationRow(), 1 + buttonOffset, box);
					buttonOffset++;
				}
			}
			removeStepButton.addClickHandler(new RemoveStepHandler(getStartKey() + ""));
			moveUp.addClickHandler(new MoveUpStepHandler(getStartKey() + ""));
			getStepFlexTable().setWidget(getValidationRow(), 1 + buttonOffset, removeStepButton);
			getStepFlexTable().setWidget(getValidationRow(), 2 + buttonOffset, moveUp);
			addIdentifierKey(getValidationRow(), getStartKey() + "");
			bumpValidationRow();
			bumpStartKey();
			mywebapp.this.mainPanel.setHeight((getStepFlexTable().getOffsetHeight() + 100) + "px");
		}
	}

	private void buildMainPanel(final LayoutPanel mainPanel2, final LayoutPanel localSetupPanel,
			SplitLayoutPanel p, LayoutPanel westPanel, int offsetWidth15, int offsetHeight25) {
		p.addWest(westPanel, offsetWidth15);
		p.addNorth(localSetupPanel, offsetHeight25);
		p.add(mainPanel2);
	}

	private void buildStepTable() {
		this.validationSteps = new ArrayList<String>();
		int rowCount = getStepFlexTable().getRowCount();
		for(int a = 0; a < rowCount; a++) {
			getStepFlexTable().removeRow(0);
		}
		getStepFlexTable().setText(0, 0, "Setup Steps");
		this.identifierKey = new ArrayList<String>();
		this.validationSteps.add("Setup Steps");
		this.identifierKey.add("Setup Steps");
		getStepFlexTable().setText(1, 0, "Validation Steps");
		this.validationSteps.add("Validation Steps");
		this.identifierKey.add("Validation Steps");
		this.setupRow = 1;
		this.validationRow = 1;
	}

	class RemoveStepHandler implements ClickHandler {
		String myKey;
		int setupOrValidation;

		public RemoveStepHandler(String newKey) {
			this.myKey = newKey;
			this.setupOrValidation = 1;
		}

		public RemoveStepHandler(String newKey, int zeroForSetup) {
			this.myKey = newKey;
			this.setupOrValidation = zeroForSetup;
		}

		public void onClick(ClickEvent event) {
			int removedIndex = getIdentifierKey().indexOf(this.myKey);
			removeIndexKey(removedIndex);
			removeStepFlexTableRow(removedIndex);
			if(this.setupOrValidation == 1) {
				reduceValidationRow();
			}else{
				reduceSetupRow();
			}
		}
	}

	class MoveUpStepHandler implements ClickHandler {
		String myKey;
		int setupOrValidation;

		public MoveUpStepHandler(String newKey) {
			this.myKey = newKey;
			this.setupOrValidation = 1;
		}

		public MoveUpStepHandler(String newKey, int zeroForSetup) {
			this.myKey = newKey;
			this.setupOrValidation = zeroForSetup;
		}

		public void onClick(ClickEvent event) {
			int currentIndex = getIdentifierKey().indexOf(this.myKey);
			boolean dontSkip = true;
			if(currentIndex == 1 || currentIndex == getSetupRow() + 1) {
				dontSkip = false;
			}
			if(dontSkip) {
				String dummy = getIdentifierKey().get(currentIndex-1);
				getIdentifierKey().set(currentIndex-1, this.myKey);
				getIdentifierKey().set(currentIndex, dummy);
				getStepFlexTable().insertRow(currentIndex-1);
				int count = getStepFlexTable().getCellCount(currentIndex+1);
				for(int a = 0; a < count; a++) {
					if(getStepFlexTable().getWidget(currentIndex+1, a) != null) {
						getStepFlexTable().setWidget(currentIndex - 1, a, getStepFlexTable().getWidget(currentIndex+1, a));
					} else {
						getStepFlexTable().setText(currentIndex - 1, a, getStepFlexTable().getText(currentIndex+1, a));
					}
				}
				getStepFlexTable().removeRow(currentIndex + 1);
			}
		}
	}

	public void setEditTree(boolean editTree) {
		this.editTree = editTree;
	}

	public boolean isEditTree() {
		return this.editTree;
	}

	public void setStepFlexTable(FlexTable stepFlexTable) {
		this.stepFlexTable = stepFlexTable;
	}

	public FlexTable getStepFlexTable() {
		return this.stepFlexTable;
	}

	public void removeStepFlexTableRow(int rowIndex) {
		this.stepFlexTable.removeRow(rowIndex);
	}

	public void bumpValidationRow() {
		this.validationRow++;
	}

	public void reduceValidationRow() {
		this.validationRow--;
	}

	public int getValidationRow() {
		return this.validationRow + getSetupRow();
	}

	public ArrayList<String> getIdentifierKey() {
		return this.identifierKey;
	}

	public void addIdentifierKey(int index, String key) {
		this.identifierKey.add(index, key);
	}

	public void setIdentifierKey(ArrayList<String> identifierKey) {
		this.identifierKey = identifierKey;
	}

	public void removeIndexKey(int keyIndex) {
		this.identifierKey.remove(keyIndex);
	}

	public int getStartKey() {
		return this.startKey;
	}

	public void bumpStartKey() {
		this.startKey++;
	}

	public int getSetupRow() {
		return this.setupRow;
	}

	public void bumpSetupRow() {
		this.setupRow++;
	}

	public void reduceSetupRow() {
		this.setupRow--;
	}

	private CodeContainer extractCode() {
		CodeContainer testCode = new CodeContainer();
		String id = "";
		ArrayList<String> variables = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		FlexTable instructionTable = getStepFlexTable();
		for(int a = 1; a < instructionTable.getRowCount(); a++) {
			if(a != getSetupRow()) {
				CodeStep step = new CodeStep();
				int b = 0;
				Widget w = instructionTable.getWidget(a, b);
				while(!(w instanceof StepHolder)) {
					variables = new ArrayList<String>();
					titles = new ArrayList<String>();
					if(instructionTable.getWidget(a, b) == null) {
						b++;
						w = instructionTable.getWidget(a, b);
					} else {
						while(w instanceof TextboxIDHolder) {
							TextboxIDHolder box = (TextboxIDHolder)w;
							variables.add(box.getText());
							titles.add(box.getTitle());
							id = box.getTagID();
							b++;
							w = instructionTable.getWidget(a, b);
						}
						if(!(w instanceof StepHolder)) {
							step.addTag(id, variables, titles);
						}
					}
				}
				StepHolder tagName = (StepHolder)w;
				if(tagName.getTagID() != null) {
					testCode.addStep(tagName.getTagID(), variables, titles);
					variables = new ArrayList<String>();
					titles = new ArrayList<String>();
				}else if (tagName.getMultiTags() != null && tagName.getMultiTags().size() == 1) {
					testCode.addStep(id, variables, titles);
					variables = new ArrayList<String>();
					titles = new ArrayList<String>();
				}
				if (tagName.getMultiTags() != null && tagName.getMultiTags().size() != 1) {
					CodeStep newStep = new CodeStep();
					SingleTag single;
					if(step.getMultiTag() != null && step.getMultiTag().size() > 0) {
						single = step.getMultiTag().get(0);
						step.getMultiTag().remove(0);
					} else {
						single = new SingleTag(null, null, null);
					}
					for (String tag : tagName.getMultiTags()) {
						if(tag.equals(single.getTag())) {
							newStep.addTag(single.getTag(), single.getParams(), single.getTitles());
							if(step.getMultiTag().size() > 0) {
								single = step.getMultiTag().get(0);
								step.getMultiTag().remove(0);
							} else {
								single = new SingleTag(null, null, null);
							}
						} else {
							newStep.addTag(tag, null, null);
						}
					}
					testCode.addStep(newStep);
				}
			}
		}
		return testCode;
	}

	public Boolean getDatabasePresent() {
		return this.databasePresent;
	}

	public void setDatabasePresent(Boolean databasePresent) {
		this.databasePresent = databasePresent;
	}

	public class SetupInput {

		private SetupBuilderServiceAsync setupBuildingService = GWT.create(SetupBuilderService.class);
		private LayoutPanel inputSetupPanel;

		public SetupInput (LayoutPanel setupPanel) {
			this.inputSetupPanel = setupPanel;
			this.inputSetupPanel.add(buildSetupPanel());
		}

		public DialogBox createSetupDialogBox() {
			// Create a dialog box and set the caption text
			final DialogBox dialogBox = new DialogBox(false);

			Button closeButton = new Button(
					"Close", new ClickHandler() {
						public void onClick(ClickEvent event) {
							resetSetup();
							dialogBox.hide();
						}
					});

			//evaluate the size of their window and make this the bulk of it.
			dialogBox.setWidget(buildSetupDialogPanel(closeButton));
			dialogBox.setGlassEnabled(true);
			return dialogBox;
		}

		private LayoutPanel buildSetupDialogPanel(Button closeButton) {
			final LayoutPanel panel = new LayoutPanel();
			panel.setSize("1400px", "700px");
			final Tree setupTree = new Tree();
			final SetupDataItem allData = new SetupDataItem();
			final Button addTab = new Button("Add Tab");
			final FlexTable mainTable = new FlexTable();
			final Button saveButton = new Button("Save All");
			final ArrayList<String> tagIndex = new ArrayList<String>();

			panel.add(setupTree);
			panel.setWidgetLeftWidth(setupTree, 1, Unit.EM, 20, Unit.EM);
			panel.setWidgetTopHeight(setupTree, 1, Unit.EM, 40, Unit.EM);
			panel.add(mainTable);
			panel.setWidgetLeftWidth(mainTable, 21, Unit.EM, 100, Unit.EM);
			panel.setWidgetTopHeight(mainTable, 1, Unit.EM, 40, Unit.EM);
			panel.add(saveButton);
			panel.setWidgetLeftWidth(saveButton, 1, Unit.EM, 10, Unit.EM);
			panel.setWidgetBottomHeight(saveButton, 1, Unit.EM, 3, Unit.EM);
			panel.add(addTab);
			panel.setWidgetLeftWidth(addTab, 12, Unit.EM, 10, Unit.EM);
			panel.setWidgetBottomHeight(addTab, 1, Unit.EM, 3, Unit.EM);
			panel.add(closeButton);
			panel.setWidgetLeftWidth(closeButton, 23, Unit.EM, 10, Unit.EM);
			panel.setWidgetBottomHeight(closeButton, 1, Unit.EM, 3, Unit.EM);

			if (this.setupBuildingService == null) {
				this.setupBuildingService = GWT.create(SetupBuilderService.class);
			}

			// Set up the callback object.
			AsyncCallback<SetupDataItem> callback = buildTreeCallback(setupTree, allData);

			class SetupHandler implements SelectionHandler<TreeItem>{
				String lastColumn = null;
				String[] title = {"Internal Field tagID.", "Displaying Label.", "Number of associated text fields.", "Description of associated text fields."};

				@Override
				public void onSelection(SelectionEvent<TreeItem> event) {
					TreeItem selected = event.getSelectedItem();
					mainTable.removeAllRows();
					if(selected.getParentItem() != null) {
						//I could set the lock here! lock down the column while its being edited... but then I would need to save once the column is left.
						EnterPressHandler enter = new EnterPressHandler();
						RadioButton checkBox = new RadioButton("checkType", "Check Box");
						RadioButton radioButton = new RadioButton("checkType", "Radio Button");
						RadioChangeHandler handler = new RadioChangeHandler();
						checkBox.addClickHandler(handler);
						radioButton.addClickHandler(handler);
						TextBox tableBox;
						tableBox = new TextBox();
						tableBox.setText(selected.getText());
						tableBox.addKeyPressHandler(enter);
						tableBox.addBlurHandler(enter);
						mainTable.setWidget(0, 0, tableBox);
						mainTable.setWidget(1, 0, checkBox);
						mainTable.setWidget(1, 1, radioButton);
						String[] columnHeaders = {"TagID", "Diplay", "Editable Fields", "Description of fields"};
						for(int c = 0; c < 4; c++) {
							mainTable.setText(2,c,columnHeaders[c]);
						}
						int a = 0;
						int rowOffset = 3;
						ArrayList<TableData> columnData = allData.getData().get(selected.getText());
						if(columnData == null) {
							columnData = new ArrayList<TableData>();
							checkBox.setValue(Boolean.TRUE);
						}
						int size = columnData.size() + 1;
						TableData data;
						Button removeStep;
						for(a = 0; a < size; a++) {
							if(a <= columnData.size()) {
								if (a == columnData.size()) {
									boolean value = (a > 0) ? columnData.get(a-1).isCheckbox() : checkBox.getValue().booleanValue();
									data = new TableData(allData.getNextHighestTag(), "", value, Integer.valueOf(0));
								} else {
									data = columnData.get(a);
								}
								String[] textData = {data.getTagID(), data.getLabel(), data.getTextfields().toString(), data.getDescriptionsToString()};
								if(data.isCheckbox() && !checkBox.getValue().booleanValue()) {
									checkBox.setValue(Boolean.TRUE);
								}
								if(!data.isCheckbox() && !radioButton.getValue().booleanValue()) {
									radioButton.setValue(Boolean.TRUE);
								}
								for(int b = 0; b < 4; b++) {
									tableBox = new TextBox();
									tableBox.addKeyPressHandler(enter);
									tableBox.addBlurHandler(enter);
									if(b == 0) {
										tableBox.setEnabled(false);
									}
									if(b == 3) {
										tableBox.setWidth("500px");
									}
									tableBox.setText(textData[b]);
									tableBox.setTitle(this.title[b]);
									mainTable.setWidget(a + rowOffset, b, tableBox);
								}
								removeStep = new Button("x");
								removeStep.addClickHandler(new StepRemover(data.getTagID()));
								mainTable.setWidget(a + rowOffset, 4, removeStep);

							}
						}
						this.lastColumn = selected.getText();
					} else {
						String tabName = selected.getText();
						TextBox tab = new TextBox();
						tab.setText(tabName);
						tab.setTitle("The displayed text on the tab");
						mainTable.setText(0, 0, "The displayed text on the tab");
						mainTable.setWidget(0, 1, tab);
						mainTable.setText(1, 0, "The columns to appear within the tab");
						ArrayList<String> columns = allData.getColumnsOnTab(tabName);
						TabEnterPressHandler enter = new TabEnterPressHandler(tabName);
						int a = 0;
						TextBox box;
						int size = (columns == null) ? 0: columns.size();
						for(a = 0; a < size + 1; a++) {
							box = new TextBox();
							if(a < size) {
								box.setText(columns.get(a));
								box.setEnabled(false);
							}
							box.addKeyPressHandler(enter);
							box.addBlurHandler(enter);
							mainTable.setWidget(a+2, 0, box);
						}
						TabChangeHandler change = new TabChangeHandler(selected);
						tab.addBlurHandler(change);
						tab.addKeyPressHandler(change);
						this.lastColumn = null;
					}
				}

				class StepRemover implements ClickHandler{
					private String tagID;

					public StepRemover(String tagID) {
						this.tagID = tagID;
					}

					@Override
					public void onClick(ClickEvent event) {
						mainTable.removeRow(tagIndex.indexOf(this.tagID)+4);
						tagIndex.remove(this.tagID);
						allData.removeTag(this.tagID);
					}
				}

				class RadioChangeHandler implements ClickHandler{

					@Override
					public void onClick(ClickEvent event) {
						RadioButton selected = (RadioButton)event.getSource();
						ArrayList<TableData> columnData = allData.getDataforColumn(SetupHandler.this.lastColumn);
						for (int a = 0; a < columnData.size(); a++) {
							columnData.get(a).setCheckbox((selected.getText().equals("Check Box")) ? true : false);
						}
						allData.updateDataInColumn(SetupHandler.this.lastColumn, columnData);
					}
				}

				class TabChangeHandler implements KeyPressHandler, BlurHandler{
					TreeItem item;

					public TabChangeHandler(TreeItem selected) {
						this.item = selected;
					}

					@Override
					public void onKeyPress(KeyPressEvent event) {
						if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
							removeData(this.item.getText());
							addData(this.item.getText());
						}
					}

					@Override
					public void onBlur(BlurEvent event) {
						removeData(this.item.getText());
						addData(this.item.getText());
					}
				}

				class TabEnterPressHandler implements KeyPressHandler, BlurHandler{

					String tabName;

					//This needs to have duplicate column name checking

					public TabEnterPressHandler(String tabName) {
						this.tabName = tabName;
					}

					@Override
					public void onKeyPress(KeyPressEvent event) {
						Object source = event.getSource();
						if(source instanceof TextBox) {
							if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
								addData(this.tabName);
							}
							if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB) {
								//Tab is firing a blurring event... the jerk
							}
						}
					}

					@Override
					public void onBlur(BlurEvent event) {
						Object source = event.getSource();
						if(source instanceof TextBox) {
							if(event.getNativeEvent().getKeyCode() != KeyCodes.KEY_ENTER &&
									event.getNativeEvent().getKeyCode() != KeyCodes.KEY_TAB) {
								addData(this.tabName);
							}
						}
					}

				}

				private void addData(String tabName) {
					ArrayList<String> columns = new ArrayList<String>();
					String columnToAdd;
					String newTabName;
					newTabName = ((TextBox)mainTable.getWidget(0, 1)).getText();
					for(int row = 0; row < mainTable.getRowCount(); row++) {
						if(mainTable.getWidget(row, 0) != null) {
							columnToAdd = ((TextBox)mainTable.getWidget(row, 0)).getText();
							if(!columnToAdd.isEmpty()) {
								columns.add(columnToAdd);
							}
						}
					}
					if(allData.doesTabExist(newTabName)) {
						allData.overWriteColumnsOnTab(newTabName, columns);
					} else {
						allData.overWriteTab(tabName, newTabName);
						allData.updateTabWithColumns(tabName, newTabName, columns);
						tabName = newTabName;
					}
					resetSetupTree(panel, setupTree, allData, setupTree.getSelectedItem());
				}

				private void removeData(String tabName) {
					if(allData.doesTabExist(tabName)) {
						allData.removeTab(tabName);
					}
					resetSetupTree(panel, setupTree, allData, setupTree.getSelectedItem());
				}

				class EnterPressHandler implements KeyPressHandler, BlurHandler{

					//this is crap I know... I save the whole table everytime I lose focus or press the enter key

					@Override
					public void onKeyPress(KeyPressEvent event) {
						Object source = event.getSource();
						if(source instanceof TextBox) {
							if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
								addData();
							}
						}
					}

					@Override
					public void onBlur(BlurEvent event) {
						Object source = event.getSource();
						if(source instanceof TextBox) {
							addData();
						}
					}

					private void addData() {
						ArrayList<TableData> columnData = new ArrayList<TableData>();
						String tagID;
						String label;
						Integer fields;
						boolean checkbox;
						TableData data;
						for(int a = 3; a < mainTable.getRowCount(); a++) {
							tagID = ((TextBox)mainTable.getWidget(a, 0)).getText();
							label = ((TextBox)mainTable.getWidget(a, 1)).getText();
							fields = Integer.valueOf(((TextBox)mainTable.getWidget(a, 2)).getText());
							checkbox = ((RadioButton)mainTable.getWidget(1, 0)).getValue().booleanValue();
							if(!label.isEmpty()) {
								data = new TableData(tagID, label, checkbox, fields);
								data.addDescriptions(((TextBox)mainTable.getWidget(a, 3)).getText());
								columnData.add(data);
							}
						}
						allData.updateDataInColumn(SetupHandler.this.lastColumn, columnData);
						resetSetupTree(panel, setupTree, allData, setupTree.getSelectedItem());
						//Still loses focus!! Get the focus back to the element they were in. Also tab button will cause a loss in focus(blur) but we don't want that to go away!
//					source.setFocus(true);
					}
				}
			}

			SetupHandler treeHandler = new SetupHandler();
			setupTree.addSelectionHandler(treeHandler);

			class TabAdder implements ClickHandler{

				//bad garbage collection as I'm only ripping out the top level of the tree, all of the associated children are probably still there.
				@Override
				public void onClick(ClickEvent event) {
					allData.addTab("New Tab");
					resetSetupTree(panel, setupTree, allData);
				}
			}

			TabAdder tabHandler = new TabAdder();
			addTab.addClickHandler(tabHandler);

			class SaveAll implements ClickHandler{

				@Override
				public void onClick(ClickEvent event) {
					if (SetupInput.this.setupBuildingService == null) {
						SetupInput.this.setupBuildingService = GWT.create(SetupBuilderService.class);
					}

					AsyncCallback<Boolean> callbackSave = saveSetupDataCallback();
					SetupInput.this.setupBuildingService.saveSetupData(allData, callbackSave);
					SetupInput.this.setupBuildingService = null;
				}
			}

			SaveAll saveHandler = new SaveAll();
			saveButton.addClickHandler(saveHandler);

			this.setupBuildingService.getSetupData(callback);

			return panel;
		}

		private void resetSetupTree(final LayoutPanel panel, final Tree setupTree,
				final SetupDataItem allData) {
			resetSetupTree(panel, setupTree, allData, null);
		}

		private void resetSetupTree(final LayoutPanel panel, final Tree setupTree,
				final SetupDataItem allData, TreeItem selected) {
			TreeItem selectedItem = null;
			String selectedText = null;
			if(selected != null) {
				selectedText = selected.getText();
			}
			panel.remove(setupTree);
			setupTree.removeItems();
			ArrayList<String> tabs = allData.getTabs();
			for (String tab : tabs) {
				TreeItem tabItem = new TreeItem(tab);
				if(selected != null && tab.equals(selectedText)) {
					selectedItem = tabItem;
				}
				ArrayList<String> columns = allData.getColumnsOnTab(tab);
				if(!(columns == null || columns.isEmpty())) {
					for (String column : columns) {
						TreeItem columnItem = new TreeItem(column);
						tabItem.addItem(columnItem);
						if(selected != null && column.equals(selectedText)) {
							selectedItem = columnItem;
						}
					}
				}
				setupTree.addItem(tabItem);
			}
			panel.add(setupTree);
			panel.setWidgetLeftWidth(setupTree, 1, Unit.EM, 20, Unit.EM);
			panel.setWidgetTopHeight(setupTree, 1, Unit.EM, 40, Unit.EM);
			if(selectedItem != null) {
				if(selectedItem.getParentItem() != null) {
					selectedItem.getParentItem().setState(true);
				}
				setupTree.setSelectedItem(selectedItem);
				setupTree.getSelectedItem().setState(selected.getState());
			}
		}

		public TabLayoutPanel buildSetupPanel() {
			final TabLayoutPanel localSetupPanel = new TabLayoutPanel(.7, Unit.CM);

			// Initialize the service proxy.
			if (this.setupBuildingService == null) {
				this.setupBuildingService = GWT.create(SetupBuilderService.class);
			}

			// Set up the callback object.
			AsyncCallback<SetupDataItem> callback = setupDataCallback(localSetupPanel);
			this.setupBuildingService.getSetupData(callback);
			return localSetupPanel;
		}

		public void resetSetup() {
			this.inputSetupPanel.remove(0);
			this.inputSetupPanel.add(buildSetupPanel());
		}
	}

	private AsyncCallback<Boolean> checkDatabaseCallback() {
		return new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.print("Failure!");
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.booleanValue()) {
					paintMainPage();
				} else {
					DialogBox dialog;
					dialog = createDatabaseDialogBox();
					dialog.show();
					dialog.center();
				}
			}
		};
	}

	private AsyncCallback<SetupDataItem> setupDataCallback(
			final TabLayoutPanel localSetupPanel) {
		return new AsyncCallback<SetupDataItem>() {
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SetupDataItem result) {
				ArrayList<String> tabList = result.getTabs();
				for (String tab : tabList) {
					LayoutPanel panel = new LayoutPanel();
					buildPanel(panel, result.getColumnsOnTab(tab), result.getData());
					localSetupPanel.add(panel, tab);
				}
			}

			private void buildPanel(LayoutPanel panel, ArrayList<String> columns, HashMap<String,ArrayList<TableData>> tableData) {
				final ArrayList<Object> boxesAndButtons = new ArrayList<Object>();
				final HashMap<String, TableData> allData = new HashMap<String, TableData>();
				final FlexTable table = new FlexTable();
				ArrayList<TableData> columnData;
				int a = 0;
				for (String columnHeader : columns) {
					table.setText(0, a, columnHeader);
					columnData = tableData.get(columnHeader);
					int b = 1;
					for (TableData data : columnData) {
						if(data.isCheckbox()) {
							CheckBox box = new CheckBox(data.getLabel());
							boxesAndButtons.add(box);
							table.setWidget(b, a, box);
						}else {
							RadioButton button = new RadioButton(columnHeader, data.getLabel());
							boxesAndButtons.add(button);
							table.setWidget(b, a, button);
						}
						allData.put(data.getLabel(), data);
						b++;
					}
					a++;
				}

				Button addSetupButton = new Button("Add this!");
				addSetupButton.addClickHandler(new AddSetupHandler(allData, boxesAndButtons));
				table.setWidget(0, a, addSetupButton);
				panel.add(table);
			}
		};
	}

	private AsyncCallback<Boolean> extractCodeCallback() {
		return new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.print("Failure!");
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.booleanValue()) {
					System.out.print("Exception!");
				}
			}
		};
	}


	private AsyncCallback<String> generateCodeCallback(final Label label) {
		return new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.print("Failure! " + caught.toString());
			}

			@Override
			public void onSuccess(String result) {
				label.setText(result);
			}
		};
	}

	private AsyncCallback<Boolean> buildDatabaseCallback(
			final Button closeButton) {
		return new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.print("Failure! " + caught.toString());
			}

			@Override
			public void onSuccess(Boolean result) {
				setDatabasePresent(Boolean.TRUE);
				closeButton.click();
			}
		};
	}

	private AsyncCallback<String> getTestCallback() {
		return new LoadTestAsync();
	}

	private AsyncCallback<Boolean> doesTestExistCallback(
			final Button closeButton, final Label errorLabel) {
		return new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.booleanValue()) {
					closeButton.click();
				} else {
					errorLabel.setText("Test is not there!");
				}
			}
		};
	}

	private AsyncCallback<SetupDataItem> buildTreeCallback(
			final Tree setupTree, final SetupDataItem allData) {
		return new AsyncCallback<SetupDataItem>() {
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SetupDataItem result) {
				allData.setData(result);
				ArrayList<String> tabs = result.getTabs();
				for (String tab : tabs) {
					TreeItem tabItem = new TreeItem(tab);
					ArrayList<String> columns = result.getColumnsOnTab(tab);
					for (String column : columns) {
						TreeItem columnItem = new TreeItem(column);
						tabItem.addItem(columnItem);
					}
					setupTree.addItem(tabItem);
				}
			}
		};
	}

	private AsyncCallback<Boolean> saveSetupDataCallback() {
		return new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				System.out.print("Failure!");
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.booleanValue()) {
					System.out.print("Exception!");
				}
			}
		};
	}

	public class DialogBoxCreator {

		final DialogBox dialogBox = new DialogBox(false);

		public DialogBoxCreator() {

		Button closeButton = new Button(
				"Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						dialogBox.hide();
					}
				});

		switch(method) {
		case 1:
			this.dialogBox.setWidget(buildCodeDialog(closeButton));
		case 2:
			this.dialogBox.setWidget(buildDatabaseDialog(closeButton));
		case 3:
			this.dialogBox.setWidget(buildLoadTestDialog(closeButton));
		case 4:
			this.dialogBox.setWidget(buildSetupDialogPanel(closeButton));

		}
		this.dialogBox.setGlassEnabled(true);
		}

		public DialogBox getMyDialogBox(){
			return this.dialogBox;
		}
	}

}

