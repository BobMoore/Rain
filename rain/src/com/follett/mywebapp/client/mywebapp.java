package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.server.CodeBuilderService;
import com.follett.mywebapp.server.CodeBuilderServiceAsync;
import com.follett.mywebapp.server.SetupBuilderService;
import com.follett.mywebapp.server.SetupBuilderServiceAsync;
import com.follett.mywebapp.server.TreeBuilderService;
import com.follett.mywebapp.server.TreeBuilderServiceAsync;
import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.CodeStep;
import com.follett.mywebapp.util.SetupDataItem;
import com.follett.mywebapp.util.StepHolder;
import com.follett.mywebapp.util.TableData;
import com.follett.mywebapp.util.TextboxIDHolder;
import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.follett.mywebapp.util.ValidationTreeNode;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
	private int setupRow = 1;
	private int validationRow = 1;
	private boolean editTree = false;
	private ArrayList<String> validationSteps = new ArrayList<String>();
	private ArrayList<String> identifierKey = new ArrayList<String>();
	private int startKey = 10;
	private Tree t;
	private LayoutPanel treePanel;
	private LayoutPanel setupPanel;

	private TreeBuilderServiceAsync treeBuildingService = GWT.create(TreeBuilderService.class);
	private SetupBuilderServiceAsync setupBuildingService = GWT.create(SetupBuilderService.class);
	private CodeBuilderServiceAsync codeBuildingService = GWT.create(CodeBuilderService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

	    final Button saveButton = new Button("Save Test");
	    final TextBox testNumber = new TextBox();
	    final Button loadTest = new Button("Load Test");
	    final Button generateCode = new Button("Generate Code");
	    final Button editSetup = new Button("Edit Setup");
	    final LayoutPanel mainPanel = new LayoutPanel();
	    this.treePanel = new LayoutPanel();
	    final ScrollPanel flexPanel = new ScrollPanel();
	    this.setupPanel = new LayoutPanel();
	    this.setupPanel.add(buildSetupPanel());
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

		builtStepPanel(saveButton, testNumber, generateCode, editSetup, mainPanel, flexPanel, loadTest);

	    buildMainPanel(mainPanel, this.setupPanel, testDevelopementPanel, this.treePanel);

	    buildStepTable();

	    //Listeners
	    class TreeHandler implements SelectionHandler<TreeItem>{

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				ValidationTreeNode selected = (ValidationTreeNode)event.getSelectedItem();
				getStepFlexTable().setText(getValidationRow(), 0, selected.getText());
				StepHolder removeStepButton = new StepHolder("x", selected.getTagID());
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
				getStepFlexTable().setWidget(getValidationRow(), 1 + buttonOffset, removeStepButton);
				addValidationStep(getValidationRow(),selected.getText());
				addIdentifierKey(getValidationRow(), getStartKey() + "");
				bumpValidationRow();
				bumpStartKey();
			}
	    }

    class GenerateCodeHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			//step one get a list of tags and variables to be sent
			CodeContainer testCode = extractCode();
			System.out.print(testCode.toString() + "\n");

			//step two send them to the service to gather the code snipets
			//step three gather the code that was written from the service and do something with it
		}
    }

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
    		this.dialog = createSetupDialogBox();
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
			mywebapp.this.codeBuildingService.saveTest(Integer.valueOf(testNumber.getText()).intValue(), testCode.toString(), callback);
		}
	}

    // Add a handler to send the name to the server
    TreeHandler tHandler = new TreeHandler();
    this.t.addSelectionHandler(tHandler);
    GenerateCodeHandler cHandler = new GenerateCodeHandler();
    generateCode.addClickHandler(cHandler);
    SaveHandler saveHandler = new SaveHandler();
    saveButton.addClickHandler(saveHandler);
    ValidationDialog dialogHandler = new ValidationDialog();
    openValidationDialog.addClickHandler(dialogHandler);
    SetupDialog setupHandler = new SetupDialog();
    editSetup.addClickHandler(setupHandler);
    LoadDialog loadTestHandler = new LoadDialog();
    loadTest.addClickHandler(loadTestHandler);
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
	final TextBox testNumber = new TextBox();
	panel.add(testNumber);
	panel.setWidgetTopHeight(testNumber, 21, Unit.PX, 30, Unit.PX);
	final Label errorLabel = new Label();
	panel.add(errorLabel);
	panel.setWidgetTopHeight(errorLabel, 51, Unit.PX, 47, Unit.PX);

	class TestLoader implements ClickHandler{

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
		    	mywebapp.this.codeBuildingService.doesTestExist(Integer.valueOf(testNumber.getText()).intValue(), callbackCheck);
		    } catch (NumberFormatException e) {
		    	errorLabel.setText("Bad test number");
		    }
		    AsyncCallback<String> callbackTest = new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				//TODO placeholder
				@Override
				public void onSuccess(String result) {
					ArrayList<CodeStep> steps = parseSteps(result);
					for (CodeStep step : steps) {
						System.out.print("\n" + step + " ");
//						char firstChar = step.substring(0,1).toCharArray()[0];
//						if(firstChar >= 'a' && firstChar <= 'Z') {
//							System.out.print("Validation");
//							//validation
//						} else {
//							System.out.print("Setup");
//							//setup
//						}
					}
				}

				//format is [tag [param,param,param], tag [], tag [param]]
				//can also have [[tag [], tag []], tag []]
				private ArrayList<CodeStep> parseSteps(String result){
					ArrayList<CodeStep> returnList = new ArrayList<CodeStep>();
					//Strip off the top layer of []
					result = result.substring(1, result.length()-1);
					CodeStep step;
					String params;
					while(result.contains("[")) {
						//contains a '[' meaning there are more steps
						step = new CodeStep();
						if(result.charAt(0) == '[') {
							//multi tag step
						} else {
							//single tag step
						}
//						step.addTagID(result.substring(0, result.indexOf('[')).trim());
//						System.out.print("\n"+tagID);
//						result = result.substring(result.indexOf('[') + 1).trim();
//						System.out.print("\n"+result);
//						while(result.indexOf(']') > 0) {
//							params = result.substring(0, result.indexOf(']'));
//							result = result.substring(result.indexOf(']') + 1);
//							while(params.contains(",")) {
//								currentList.add(params.substring(0, params.indexOf(',')).trim());
//								params = params.substring(params.indexOf(',')).trim();
//								System.out.print("\n"+params);
//							}
//							if(!params.isEmpty()) {
//								currentList.add(params);
//							}
//						}
//						returnable.put(tagID, currentList);
//						currentList = new ArrayList<String>();
//						if(result.contains(",")) {
//							result = result.substring(result.indexOf(',') + 1).trim();
//						}
					}
					return returnList;
				}
		    };
		    mywebapp.this.codeBuildingService.getTest(Integer.valueOf(testNumber.getText()).intValue(), callbackTest);
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
	testNumber.addKeyPressHandler(testNumberEnter);
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

private DialogBox createSetupDialogBox() {
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
	  dialogBox.setWidget(buildSetupSetup(closeButton));
	  dialogBox.setGlassEnabled(true);
	  return dialogBox;
  }

private void resetSetup() {
	this.setupPanel.remove(0);
	this.setupPanel.add(buildSetupPanel());
}

private LayoutPanel buildSetupSetup(Button closeButton) {
	final LayoutPanel panel = new LayoutPanel();
	panel.setSize("1400px", "700px");
	final Tree setupTree = new Tree();
	final SetupDataItem allData = new SetupDataItem();
	final Button addTab = new Button("Add Tab");
	final FlexTable mainTable = new FlexTable();
	final Button saveButton = new Button("Save All");

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
    AsyncCallback<SetupDataItem> callback = new AsyncCallback<SetupDataItem>() {
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
							//Add a delete element button!
						}
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
				this.lastColumn = null;
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
						addData();
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
						addData();
					}
				}
			}

			private void addData() {
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
					allData.overWriteTab(this.tabName, newTabName);
					allData.updateTabWithColumns(this.tabName, newTabName, columns);
					this.tabName = newTabName;
				}
				resetSetupTree(panel, setupTree, allData, setupTree.getSelectedItem());
			}
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
//				source.setFocus(true);
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
		    if (mywebapp.this.setupBuildingService == null) {
		    	mywebapp.this.setupBuildingService = GWT.create(SetupBuilderService.class);
		    }

			AsyncCallback<Boolean> callbackSave = new AsyncCallback<Boolean>() {

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
			mywebapp.this.setupBuildingService.saveSetupData(allData, callbackSave);
			mywebapp.this.setupBuildingService = null;
		}
	}

	SaveAll saveHandler = new SaveAll();
	saveButton.addClickHandler(saveHandler);

    this.setupBuildingService.getSetupData(callback);

	return panel;
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
		final Button editSetup, final LayoutPanel mainPanel, ScrollPanel flexPanel, Button loadTest) {
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
    	  newTree.addItem("Failure!");
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
    return newTree;
}

private TabLayoutPanel buildSetupPanel() {
	final TabLayoutPanel localSetupPanel = new TabLayoutPanel(.7, Unit.CM);

	// Initialize the service proxy.
    if (this.setupBuildingService == null) {
    	this.setupBuildingService = GWT.create(SetupBuilderService.class);
    }

    // Set up the callback object.
    AsyncCallback<SetupDataItem> callback = new AsyncCallback<SetupDataItem>() {
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
    				  getStepFlexTable().setWidget(getSetupRow(), column, removeStepButton);
    				  addValidationStep(getSetupRow(),label);
    				  addIdentifierKey(getSetupRow(), getStartKey() + "");
    				  bumpSetupRow();
    				  bumpStartKey();
    			  }
    		  }
    	  }
    	  Button addSetupButton = new Button("Add this!");
    	  addSetupButton.addClickHandler(new AddSetupHandler(boxesAndButtons, allData));
    	  table.setWidget(0, a, addSetupButton);
    	  panel.add(table);
      }
    };
    this.setupBuildingService.getSetupData(callback);
	return localSetupPanel;
}

private void buildMainPanel(final LayoutPanel mainPanel, final LayoutPanel localSetupPanel,
		SplitLayoutPanel p, LayoutPanel westPanel) {
	p.addWest(westPanel, 256);
	p.addNorth(localSetupPanel, 256);
	p.add(mainPanel);
}

private void buildStepTable() {
	getStepFlexTable().setText(0, 0, "Setup Steps");
	this.validationSteps.add("Setup Steps");
	this.identifierKey.add("Setup Steps");
	getStepFlexTable().setText(1, 0, "Validation Steps");
	this.validationSteps.add("Validation Steps");
	this.identifierKey.add("Validation Steps");
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
		removeIndexStep(removedIndex);
		removeStepFlexTableRow(removedIndex);
		if(this.setupOrValidation == 1) {
			reduceValidationRow();
		}else{
			reduceSetupRow();
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

public void removeIndexStep(int stepIndex) {
	this.validationSteps.remove(stepIndex);
}

public ArrayList<String> getValidationSteps() {
	return this.validationSteps;
}

public void addValidationStep(int index, String step) {
	this.validationSteps.add(index, step);
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

private CodeContainer extractCode() {
	CodeContainer testCode = new CodeContainer();
	String id = "";
	HashMap<String, ArrayList<String>> textToVariables = new HashMap<String, ArrayList<String>>();
	ArrayList<String> variables = new ArrayList<String>();
	FlexTable instructionTable = getStepFlexTable();
	for(int a = 1; a < instructionTable.getRowCount(); a++) {
		if(a != getSetupRow()) {
			int b = 0;
			Widget w = instructionTable.getWidget(a, b);
			while(!(w instanceof StepHolder)) {
				if(instructionTable.getWidget(a, b) == null) {
					b++;
					w = instructionTable.getWidget(a, b);
				} else {
					variables = new ArrayList<String>();
					while(w instanceof TextboxIDHolder) {
						TextboxIDHolder box = (TextboxIDHolder)w;
						variables.add(box.getText());
						id = box.getTagID();
						b++;
						w = instructionTable.getWidget(a, b);
					}
					textToVariables.put(id, variables);
				}
			}
			StepHolder tagName = (StepHolder)w;
			if(tagName.getTagID() != null) {
				testCode.addStep(tagName.getTagID(), variables);
			}else if (tagName.getMultiTags() != null) {
				ArrayList<String> currentList;
				ArrayList<String> tags = tagName.getMultiTags();
				for (String tag : tags) {
					currentList = new ArrayList<String>();
					if(textToVariables.containsKey(tag)) {
						currentList = textToVariables.get(tag);
					}
					testCode.addStep(tag, currentList);
				}
			}
		}
	}
	return testCode;
}
}

