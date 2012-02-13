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
import com.follett.mywebapp.util.ValidationTreeDataItem;
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
import com.google.gwt.user.client.ui.TextArea;
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
	private Tree t;
	private LayoutPanel treePanel;
	private LayoutPanel setupPanel;
	TextBox testNumber;

	private SetupDialogBox setupDialogBox;

	private TreeBuilderServiceAsync treeBuildingService = GWT.create(TreeBuilderService.class);
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
	    final ScrollPanel flexPanel = new ScrollPanel();
	    this.setupPanel = new LayoutPanel();
	    this.setupDialogBox = new SetupDialogBox(this.setupPanel, this);
	    final SplitLayoutPanel testDevelopementPanel = new SplitLayoutPanel();
	    final Button openValidationDialog = new Button("Edit Validation Steps");
	    setStepFlexTable(new FlexTable());

	    this.t = buildTree();

	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(testDevelopementPanel);

		this.treePanel.add(this.t);
		this.treePanel.add(openValidationDialog);
		this.treePanel.setWidgetLeftWidth(openValidationDialog, 1, Unit.EM, 15, Unit.EM);
		this.treePanel.setWidgetBottomHeight(openValidationDialog, 1, Unit.EM, 3, Unit.EM);

		builtStepPanel(saveButton, this.testNumber, generateCode, editSetup, mainPanel, flexPanel, loadTest, clearTable);

	    buildMainPanel(mainPanel, this.setupPanel, testDevelopementPanel, this.treePanel);

	    buildStepTable();

	    //Listeners

    class ValidationDialog implements ClickHandler {

    	DialogBox dialog;

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = createValidationDialogBox();
			this.dialog.show();
			this.dialog.center();
		}
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
    ValidationDialog dialogHandler = new ValidationDialog();
    openValidationDialog.addClickHandler(dialogHandler);
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

private DialogBox createValidationDialogBox() {
	    // Create a dialog box and set the caption text
	    final DialogBox dialogBox = new DialogBox(false);

	    Button closeButton = new Button(
	            "Close", new ClickHandler() {
	              public void onClick(ClickEvent event) {
	            	resetTree();
	                dialogBox.hide();
	              }
	            });

	    //evaluate the size of their window and make this the bulk of it.
	    dialogBox.setWidget(buildStepSetup(closeButton));
	    dialogBox.setGlassEnabled(true);
	    return dialogBox;
	  }

private LayoutPanel buildStepSetup(Button closeButton) {
	LayoutPanel panel = new LayoutPanel();
	panel.setSize("700px", "700px");
	final Tree innerTree = buildTree();
	panel.add(innerTree);
	panel.setWidgetLeftWidth(innerTree, 1, Unit.EM, 25, Unit.EM);
	panel.setWidgetTopHeight(innerTree, 1, Unit.EM, 40, Unit.EM);

	final Button saveSteps = new Button("Save All");
	panel.add(saveSteps);
	panel.setWidgetBottomHeight(saveSteps, 1, Unit.EM, 3, Unit.EM);
	panel.setWidgetLeftWidth(saveSteps, 1, Unit.EM, 10, Unit.EM);
	panel.add(closeButton);
	panel.setWidgetBottomHeight(closeButton, 1, Unit.EM, 3, Unit.EM);
	panel.setWidgetLeftWidth(closeButton, 12, Unit.EM, 10, Unit.EM);

	//create the fields in the main panel to fill out the tree items
	final TextBox tagID = new TextBox();
	tagID.setReadOnly(true);
	tagID.setTitle("Internal TagID. This is a non editable field");
	panel.add(tagID);
	panel.setWidgetLeftWidth(tagID, 26, Unit.EM, 10, Unit.EM);
	panel.setWidgetTopHeight(tagID, 1, Unit.EM, 3, Unit.EM);
	final TextBox parentTagID = new TextBox();
	parentTagID.setReadOnly(true);
	parentTagID.setTitle("Internal parentTagID. Please use the button on the right to edit this.");
	panel.add(parentTagID);
	panel.setWidgetLeftWidth(parentTagID, 26, Unit.EM, 10, Unit.EM);
	panel.setWidgetTopHeight(parentTagID, 5, Unit.EM, 3, Unit.EM);
	final Button updateParent = new Button("Update Parent");
	panel.add(updateParent);
	panel.setWidgetLeftWidth(updateParent, 38, Unit.EM, 10, Unit.EM);
	panel.setWidgetTopHeight(updateParent, 5, Unit.EM, 3, Unit.EM);
	final TextBox description = new TextBox();
	description.setTitle("Displayable discription of the step.");
	panel.add(description);
	panel.setWidgetLeftWidth(description, 26, Unit.EM, 10, Unit.EM);
	panel.setWidgetTopHeight(description, 9, Unit.EM, 3, Unit.EM);
	final TextBox fields = new TextBox();
	//rewrite... gah
	fields.setTitle("Number of editable fields to fill out when this step is used.");
	panel.add(fields);
	panel.setWidgetLeftWidth(fields, 26, Unit.EM, 10, Unit.EM);
	panel.setWidgetTopHeight(fields, 13, Unit.EM, 3, Unit.EM);
	final TextArea fieldDescriptions = new TextArea();
	panel.add(fieldDescriptions);
	fieldDescriptions.setTitle("Description of the fields used, seperated by a comma.");
	panel.setWidgetLeftWidth(fieldDescriptions, 26, Unit.EM, 20, Unit.EM);
	panel.setWidgetTopHeight(fieldDescriptions, 17, Unit.EM, 6, Unit.EM);
	final Button newNode = new Button("New Step");
	panel.add(newNode);
	panel.setWidgetLeftWidth(newNode, 26, Unit.EM, 7, Unit.EM);
	panel.setWidgetTopHeight(newNode, 25, Unit.EM, 3, Unit.EM);

	//Create a listener for a the tree to put the selection into the fields in the main panel
	class TreeHandler implements SelectionHandler<TreeItem>{

		ValidationTreeNode lastSelected = null;

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			ValidationTreeNode selected = (ValidationTreeNode)event.getSelectedItem();
			if(this.lastSelected != null) {
				try {
					this.lastSelected.setFields(Integer.valueOf(fields.getText()));
					this.lastSelected.setText(description.getText().trim());
					this.lastSelected.setDescriptions(fieldDescriptions.getText());
				} catch (NumberFormatException e) {
					this.lastSelected.setFields(Integer.valueOf(0));
//					t.setSelectedItem(this.lastSelected,true);
				}
			}
			if(updateParent.isEnabled()) {
				tagID.setText(selected.getTagID());
				parentTagID.setText(selected.getParentTagID());
				description.setText(selected.getText());
				fields.setText(selected.getFields().toString());
				fieldDescriptions.setText(selected.getDescriptionsToString());
				this.lastSelected = selected;
			}else {
				updateParent.setText("Update Parent");
				updateParent.setEnabled(true);
				if(newNode.isEnabled()) {
					innerTree.removeItem(this.lastSelected);
					this.lastSelected.setParentTagID(selected.getTagID());
				}else {
					this.lastSelected = new ValidationTreeNode(tagID.getText(), selected.getTagID(), "New Step", Integer.valueOf(0));
					newNode.setEnabled(true);
				}
				selected.addItem(this.lastSelected);
				innerTree.setSelectedItem(this.lastSelected, true);
			}
		}
    }

	final TreeHandler treeHandler = new TreeHandler();
	innerTree.addSelectionHandler(treeHandler);

	class EnterPressHandler implements KeyPressHandler, BlurHandler{

		@Override
		public void onKeyPress(KeyPressEvent event) {
			Object source = event.getSource();
			if(source instanceof TextBox) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					SelectionEvent.fire(innerTree, innerTree.getSelectedItem());
				}
			}
		}

		@Override
		public void onBlur(BlurEvent event) {
			treeHandler.lastSelected.setDescriptions(fieldDescriptions.getText());
		}
	}

	EnterPressHandler enter = new EnterPressHandler();
	description.addKeyPressHandler(enter);
	description.addBlurHandler(enter);
	fields.addKeyPressHandler(enter);
	fields.addBlurHandler(enter);
	fieldDescriptions.addKeyPressHandler(enter);
	fieldDescriptions.addBlurHandler(enter);

	//create a way to change the parent through clicking on the tree
	//make sure when the parent is changed, the tree is rebuilt and the focus is shifted to the moved location

	class UpdateParentHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			if(!parentTagID.getText().isEmpty()) {
				updateParent.setText("Click new parent");
				updateParent.setEnabled(false);
			}
		}
	}

	UpdateParentHandler parentHandler = new UpdateParentHandler();
	updateParent.addClickHandler(parentHandler);

	class NewStepHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String highestTag = getHighestTag(innerTree);
			newNode.setEnabled(false);
			tagID.setText(ValidationTreeNode.incrementTagID(highestTag));
			updateParent.setText("Click new parent");
			updateParent.setEnabled(false);
			description.setText("");
			fields.setText("");
			fieldDescriptions.setText("");
		}

	}

	NewStepHandler newNodeHandler = new NewStepHandler();
	newNode.addClickHandler(newNodeHandler);

	//Add a save tree button that calls a main method to refresh the tree. Also save to the database and have the method call out to the database.
	class SaveStepsHandler implements ClickHandler {


		@Override
		public void onClick(ClickEvent event) {
			ArrayList<TreeItem> convertable = getAllItemsFromTree(innerTree);
			ArrayList<ValidationTreeDataItem> sendable = new ArrayList<ValidationTreeDataItem>();
			for (TreeItem item : convertable) {
				ValidationTreeDataItem data = new ValidationTreeDataItem((ValidationTreeNode)item);
				sendable.add(data);
			}

		    if (mywebapp.this.treeBuildingService == null) {
		    	mywebapp.this.treeBuildingService = GWT.create(TreeBuilderService.class);
		    }

		    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
		    	public void onFailure(Throwable caught) {
		    		System.out.print(caught);
		    	}

		    	@Override
		    	public void onSuccess(Boolean success) {
		    		if(success.booleanValue()) {
		    			System.out.print("Exception!");
		    		}
		    	}

		    };
		    mywebapp.this.treeBuildingService.saveTreeItems(sendable, callback);
		}

	}
	SaveStepsHandler save = new SaveStepsHandler();
	saveSteps.addClickHandler(save);

	return panel;
}

private String getHighestTag(Tree tree) {
	String returnable = "";
	for(int a = 0; a < tree.getItemCount(); a++) {
		String compare = compareChildren((ValidationTreeNode)tree.getItem(a));
		if(returnable.compareTo(compare) < 0) {
			returnable = compare;
		}
	}
	return returnable;
}

private ArrayList<TreeItem> getAllItemsFromTree(Tree tree){
	ArrayList<TreeItem> items = new ArrayList<TreeItem>();
	for(int a = 0; a < tree.getItemCount(); a++) {
		items.add(tree.getItem(a));
		items.addAll(getChildItems(tree.getItem(a)));
	}
	return items;
}

private ArrayList<TreeItem> getChildItems(TreeItem item) {
	ArrayList<TreeItem> returnable = new ArrayList<TreeItem>();
	for(int a = 0; a < item.getChildCount(); a++) {
		returnable.add(item.getChild(a));
		returnable.addAll(getChildItems(item.getChild(a)));
	}
	return returnable;
}

private String compareChildren(ValidationTreeNode item) {
	String returnable = item.getTagID();
	for (int a = 0; a < item.getChildCount(); a++) {
		String compare = compareChildren((ValidationTreeNode)item.getChild(a));
		if(returnable.compareTo(compare) < 0) {
			returnable = compare;
		}
	}
	return returnable;
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

private Tree buildTree() {
	final Tree newTree = new Tree();
	// Initialize the service proxy.
    if (this.treeBuildingService == null) {
    	this.treeBuildingService = GWT.create(TreeBuilderService.class);
    }

    // Set up the callback object.
    AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>> callback = new AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>>() {
      public void onFailure(Throwable caught) {
    	  newTree.addItem(caught.getMessage());
      }

	@Override
	public void onSuccess(HashMap<String, ArrayList<ValidationTreeDataItem>> result) {
		ArrayList<ValidationTreeDataItem> roots = result.get("root");
		for (ValidationTreeDataItem items : roots) {
			ValidationTreeNode node = new ValidationTreeNode(items);
			if(result.containsKey(node.getTagID())) {
				addChildrenToTree(node, result);
			}
			newTree.addItem(node);
		}
	}

	public void addChildrenToTree(ValidationTreeNode node, HashMap<String, ArrayList<ValidationTreeDataItem>> result) {
		ArrayList<ValidationTreeDataItem> branches = result.get(node.getTagID());
		for (ValidationTreeDataItem branch : branches) {
			ValidationTreeNode leaf = new ValidationTreeNode(branch);
			if(result.containsKey(leaf.getTagID())) {
				addChildrenToTree(leaf, result);
			}
			node.addItem(leaf);
		}
	}
    };
    this.treeBuildingService.getTreeItems(callback);

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

    TreeHandler tHandler = new TreeHandler();
    newTree.addSelectionHandler(tHandler);

    return newTree;
}

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

public void resetTree() {
	this.treePanel.remove(this.t);
	this.t = buildTree();
	this.treePanel.add(this.t);
	this.treePanel.setWidgetLeftWidth(this.t, 0, Unit.PX, 256, Unit.PX);
	this.treePanel.setWidgetTopHeight(this.t, 0, Unit.PX, 768, Unit.PX);
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

