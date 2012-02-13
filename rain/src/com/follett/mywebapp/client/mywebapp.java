package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.CodeStep;
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
import com.google.gwt.user.client.ui.TextBox;
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
	TextBox testNumber;

	private SetupInput setupDialogBox;
	private SetupValidation setupValidationBox;


	private CodeBuilderServiceAsync codeBuildingService = GWT.create(CodeBuilderService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

	    final Button saveButton = new Button("Save Test");
	    this.testNumber = new TextBox();
	    final Button loadTest = new Button("Load Test");
	    final Button generateCode = new Button("Generate Code");
	    final Button editSetup = new Button("Edit Setup");
	    final Button clearTable = new Button("Clear Test");
	    final LayoutPanel mainPanel = new LayoutPanel();
	    this.treePanel = new LayoutPanel();
	    this.setupValidationBox = new SetupValidation(this.treePanel, this);
	    final ScrollPanel flexPanel = new ScrollPanel();
	    this.setupPanel = new LayoutPanel();
	    this.setupDialogBox = new SetupInput(this.setupPanel, this);
	    final SplitLayoutPanel testDevelopementPanel = new SplitLayoutPanel();
	    setStepFlexTable(new FlexTable());

	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(testDevelopementPanel);



		builtStepPanel(saveButton, this.testNumber, generateCode, editSetup, mainPanel, flexPanel, loadTest, clearTable);

	    buildMainPanel(mainPanel, this.setupPanel, testDevelopementPanel, this.treePanel);

	    buildStepTable();

	    //Listeners


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

			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

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
			mywebapp.this.codeBuildingService.saveTest(Integer.valueOf(mywebapp.this.testNumber.getText()).intValue(), testCode.toString(), callback);
		}
	}

    // Add a handler to send the name to the server
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

public DialogBox createCodeDialogBox() {

    final DialogBox dialogBox = new DialogBox(false);

    Button closeButton = new Button(
            "Close", new ClickHandler() {
              public void onClick(ClickEvent event) {
                dialogBox.hide();
              }
            });

    //evaluate the size of their window and make this the bulk of it.
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

	AsyncCallback<String> callback = new AsyncCallback<String>() {

		@Override
		public void onFailure(Throwable caught) {
			System.out.print("Failure! " + caught.toString());
		}

		@Override
		public void onSuccess(String result) {
			label.setText(result);
		}
	};
	mywebapp.this.codeBuildingService.generateTemplatedCode(testCode, callback);

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

		private int columnIndex;
		private ArrayList<SingleTag> currentTag;

		@Override
		public void onClick(ClickEvent event) {
			if (mywebapp.this.codeBuildingService == null) {
				mywebapp.this.codeBuildingService = GWT.create(CodeBuilderService.class);
		    }

		    // Set up the callback object.
		    AsyncCallback<Boolean> callbackCheck = new AsyncCallback<Boolean>() {
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
		    try {
		    	mywebapp.this.codeBuildingService.doesTestExist(Integer.valueOf(testNumberLocal.getText()).intValue(), callbackCheck);
		    } catch (NumberFormatException e) {
		    	errorLabel.setText("Bad test number");
		    }
		    AsyncCallback<String> callbackTest = new AsyncCallback<String>() {

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
									getStepFlexTable().setWidget(currentRow, TestLoader.this.columnIndex, removeStepButton);
									getStepFlexTable().setWidget(currentRow, TestLoader.this.columnIndex + 1, moveUp);
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
									TestLoader.this.columnIndex = 0;
									this.firstStep = false;
									this.lastStepSetup = data.isSetup();
								}
								String stepLabel = data.getLabel();
								getStepFlexTable().setText(currentRow, TestLoader.this.columnIndex, stepLabel);
								TestLoader.this.columnIndex++;
								if(data.getTextfields() != null) {
									ArrayList<String> descriptions = data.getDescriptions();
									ArrayList<String> params = new ArrayList<String>();
									if(TestLoader.this.currentTag.size() > 0 && data.getTagID().equals(TestLoader.this.currentTag.get(0).getTag())) {
										 params = TestLoader.this.currentTag.get(0).getParams();
										 TestLoader.this.currentTag.remove(0);
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
										getStepFlexTable().setWidget(currentRow, TestLoader.this.columnIndex, box);
										TestLoader.this.columnIndex++;
									}
								}
								removeStepButton.addTagID(data.getTagID());
							}
							if(removeStepButton != null) {
								getStepFlexTable().setWidget(currentRow, TestLoader.this.columnIndex, removeStepButton);
								getStepFlexTable().setWidget(currentRow, TestLoader.this.columnIndex + 1, moveUp);
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
					TestLoader.this.currentTag = new ArrayList<SingleTag>();
					for (CodeStep step : steps) {
						TestLoader.this.columnIndex = 0;
						final ArrayList<SingleTag> multipleTags = step.getMultiTag();
						if(step.validation()) {
							TestLoader.this.currentTag.add(new SingleTag(step.getTagID(), step.getVariables(), step.getTitles()));
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
							TestLoader.this.currentTag.add(new SingleTag(step.getTagID(), step.getVariables(), step.getTitles()));
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
								TestLoader.this.currentTag.add(tag);
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
		    };
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

private void builtStepPanel(final Button saveButton, TextBox testNumber, final Button generateCode,
		final Button editSetup, final LayoutPanel mainPanel, ScrollPanel flexPanel, Button loadTest, Button clearTable) {
	mainPanel.add(saveButton);
	mainPanel.setWidgetLeftWidth(saveButton, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(saveButton, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(testNumber);
	mainPanel.setWidgetLeftWidth(testNumber, 12, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(testNumber, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(loadTest);
	mainPanel.setWidgetLeftWidth(loadTest, 23, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(loadTest, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(generateCode);
	mainPanel.setWidgetLeftWidth(generateCode, 34, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(generateCode, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(editSetup);
	mainPanel.setWidgetLeftWidth(editSetup, 45, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(editSetup, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(clearTable);
	mainPanel.setWidgetLeftWidth(clearTable, 56, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(clearTable, 1, Unit.EM, 3, Unit.EM);
	flexPanel.add(this.stepFlexTable);
	flexPanel.ensureVisible(this.stepFlexTable);
	mainPanel.add(flexPanel);
	mainPanel.setWidgetLeftWidth(flexPanel, 1, Unit.EM, 100, Unit.EM);
	mainPanel.setWidgetTopHeight(flexPanel, 5, Unit.EM, 100, Unit.EM);
}

//TODO refactor to take a group of items so that we don't need a reference to mywebapp
class AddSetupHandler implements ClickHandler {

	  ArrayList<Object> items;
	  HashMap<String, TableData> fullData;

	  public AddSetupHandler(ArrayList<Object> items, HashMap<String, TableData> data) {
		  this.items = items;
		  this.fullData = data;
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
	  }
}
//TODO refactor to take a group of items so that we don't need a reference to mywebapp
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
	}
}

private void buildMainPanel(final LayoutPanel mainPanel, final LayoutPanel localSetupPanel,
		SplitLayoutPanel p, LayoutPanel westPanel) {
	p.addWest(westPanel, 256);
	p.addNorth(localSetupPanel, 256);
	p.add(mainPanel);
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
}

