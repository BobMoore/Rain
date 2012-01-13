package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.server.SetupBuilderService;
import com.follett.mywebapp.server.SetupBuilderServiceAsync;
import com.follett.mywebapp.server.TreeBuilderService;
import com.follett.mywebapp.server.TreeBuilderServiceAsync;
import com.follett.mywebapp.util.CodeContainer;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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

	private TreeBuilderServiceAsync treeBuildingService = GWT.create(TreeBuilderService.class);
	private SetupBuilderServiceAsync setupBuildingService = GWT.create(SetupBuilderService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

	  	final TabLayoutPanel toolPanel = new TabLayoutPanel(.7, Unit.CM);
	    final Button saveButton = new Button("Save Test");
	    final Button generateCode = new Button("Generate Code");
	    final LayoutPanel mainPanel = new LayoutPanel();
	    final LayoutPanel westPanel = new LayoutPanel();
	    final ScrollPanel flexPanel = new ScrollPanel();
	    final TabLayoutPanel setupPanel = buildSetupPanel();
//	    final SplitLayoutPanel stepBuildingPanel =
	    final SplitLayoutPanel setupBuildingPanel = buildSetupSetup();
	    final SplitLayoutPanel testDevelopementPanel = new SplitLayoutPanel();
//	    final DialogBox validationBox = createValidationDialogBox();
	    final Button openValidationDialog = new Button("Edit Validation Steps");
	    Tree t = new Tree();
	    setStepFlexTable(new FlexTable());

	    t = buildTree();

//	    toolPanel.add(stepBuildingPanel, "Modify Validation Steps");
	    toolPanel.add(setupBuildingPanel, "Modify Setup Steps");

	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(testDevelopementPanel);

		buildValidationTreePanel(westPanel, t, openValidationDialog);

		builtStepPanel(saveButton, generateCode, mainPanel, flexPanel);

	    buildMainPanel(mainPanel, setupPanel, testDevelopementPanel, westPanel);

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
			// TODO Generate the code!
			//step one get a list of tags and variables to be sent
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
			System.out.print(testCode.toString() + "\n");

			//step two send them to the service to gather the code snipets
			//step three gather the code that was written from the service and do something with it
		}
    }

    class ValidationDialog implements ClickHandler {

    	DialogBox dialog;
    	Tree treeInstance;

    	public ValidationDialog(Tree t) {
    		this.treeInstance = t;
    	}

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = createValidationDialogBox();
			this.dialog.show();
			this.dialog.center();
			this.treeInstance = buildTree();
		}
    }

    // Add a handler to send the name to the server
    TreeHandler tHandler = new TreeHandler();
    t.addSelectionHandler(tHandler);
    GenerateCodeHandler cHandler = new GenerateCodeHandler();
    generateCode.addClickHandler(cHandler);
    ValidationDialog dialogHandler = new ValidationDialog(t);
    openValidationDialog.addClickHandler(dialogHandler);
  }

  private DialogBox createValidationDialogBox() {
	    // Create a dialog box and set the caption text
	    final DialogBox dialogBox = new DialogBox(false);
//	    dialogBox.ensureDebugId("cwDialogBox");
	    dialogBox.setStyleName("dialogs");

	    Button closeButton = new Button(
	            "Close", new ClickHandler() {
	              public void onClick(ClickEvent event) {
	                dialogBox.hide();
	              }
	            });

	    dialogBox.setSize("500px", "500px");
	    SimplePanel holder = new SimplePanel();
	    holder.add(buildStepSetup(closeButton));
	    dialogBox.setWidget(holder);
	    dialogBox.setGlassEnabled(true);
	    return dialogBox;
	  }

private SplitLayoutPanel buildSetupSetup() {
	SplitLayoutPanel panel = new SplitLayoutPanel();
	final Tree setupTree = new Tree();
	final SetupDataItem allData = new SetupDataItem();
	panel.addWest(setupTree, 256);

	LayoutPanel mainPanel = new LayoutPanel();
	final FlexTable mainTable = new FlexTable();

	mainPanel.add(mainTable);
	panel.add(mainPanel);

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
    	int oldRows;
    	String lastColumn = null;
    	String[] title = {"Internal Field tagID.", "Displaying Label.", "Number of associated text fields.", "Description of associated text fields."};

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			TreeItem selected = event.getSelectedItem();
			mainTable.removeAllRows();
			if(selected.getChildCount() == 0) {
				RadioButton checkBox = new RadioButton("checkType", "Check Box");
				RadioButton radioButton = new RadioButton("checkType", "Radio Button");
				RadioChangeHandler handler = new RadioChangeHandler();
				checkBox.addClickHandler(handler);
				radioButton.addClickHandler(handler);
				mainTable.setText(0,0,selected.getText());
				mainTable.setWidget(1, 0, checkBox);
				mainTable.setWidget(1, 1, radioButton);
				String[] columnHeaders = {"TagID", "Diplay", "Editable Fields", "Description of fields"};
				for(int c = 0; c < 4; c++) {
					mainTable.setText(2,c,columnHeaders[c]);
				}
				int a = 0;
				int rowOffset = 3;
				ArrayList<TableData> columnData = allData.getData().get(selected.getText());
				int size = (columnData.size() + 1 < this.oldRows) ? this.oldRows: columnData.size() + 1;
				TableData data;
				EnterPressHandler enter = new EnterPressHandler();
				for(a = 0; a < size; a++) {
					if(a <= columnData.size()) {
						if (a == columnData.size()) {
							data = new TableData(allData.getNextHighestTag(), "", columnData.get(a-1).isCheckbox(), Integer.valueOf(0));
						} else {
							data = columnData.get(a);
						}
						String[] textData = {data.getTagID(), data.getLabel(), data.getTextfields().toString(), data.getDescriptionsToString()};
						TextBox tableBox;
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
							tableBox.setTitle(title[b]);
							mainTable.setWidget(a + rowOffset, b, tableBox);
							//Add a delete element button!
						}
					}
				}
				this.oldRows = columnData.size() + 1;
				this.lastColumn = selected.getText();
			} else {
				this.lastColumn = null;
				this.oldRows = 0;
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

		class EnterPressHandler implements KeyPressHandler, BlurHandler{

			//this is crap I know... I save the whole table everytime I lose focus or press the enter key

			@Override
			public void onKeyPress(KeyPressEvent event) {
				Object source = event.getSource();
				if(source instanceof TextBox) {
					if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
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
							data = new TableData(tagID, label, checkbox, fields);
							data.addDescriptions(((TextBox)mainTable.getWidget(a, 3)).getText());
							columnData.add(data);
						}
						allData.updateDataInColumn(SetupHandler.this.lastColumn, columnData);
					}
				}
			}

			@Override
			public void onBlur(BlurEvent event) {
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
					data = new TableData(tagID, label, checkbox, fields);
					data.addDescriptions(((TextBox)mainTable.getWidget(a, 3)).getText());
					columnData.add(data);
				}
				allData.updateDataInColumn(SetupHandler.this.lastColumn, columnData);
			}
		}

    }

    SetupHandler treeHandler = new SetupHandler();
    setupTree.addSelectionHandler(treeHandler);

    this.setupBuildingService.getSetupData(callback);


	return panel;
}

private DockLayoutPanel buildStepSetup(Button closeButton) {
	DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
	LayoutPanel westPanel = new LayoutPanel();
	final Tree t = buildTree();
	westPanel.add(t);
	westPanel.add(closeButton);
	westPanel.setWidgetLeftWidth(closeButton, 1, Unit.EM, 10, Unit.EM);
	westPanel.setWidgetBottomHeight(closeButton, 1, Unit.EM, 3, Unit.EM);
	panel.addWest(westPanel, 256);
	LayoutPanel mainPanel = new LayoutPanel();
	panel.add(mainPanel);

	final Button saveSteps = new Button("Save All");
	westPanel.add(saveSteps);
	westPanel.setWidgetBottomHeight(saveSteps, 1, Unit.EM, 3, Unit.EM);
	westPanel.setWidgetLeftWidth(saveSteps, 1, Unit.EM, 10, Unit.EM);

	//create the fields in the main panel to fill out the tree items
	final TextBox tagID = new TextBox();
	tagID.setReadOnly(true);
	tagID.setTitle("Internal TagID. This is a non editable field");
	mainPanel.add(tagID);
	mainPanel.setWidgetLeftWidth(tagID, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(tagID, 1, Unit.EM, 3, Unit.EM);
	final TextBox parentTagID = new TextBox();
	parentTagID.setReadOnly(true);
	parentTagID.setTitle("Internal parentTagID. Please use the button on the right to edit this.");
	mainPanel.add(parentTagID);
	mainPanel.setWidgetLeftWidth(parentTagID, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(parentTagID, 5, Unit.EM, 3, Unit.EM);
	final Button updateParent = new Button("Update Parent");
	mainPanel.add(updateParent);
	mainPanel.setWidgetLeftWidth(updateParent, 12, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(updateParent, 5, Unit.EM, 3, Unit.EM);
	final TextBox description = new TextBox();
	description.setTitle("Displayable discription of the step.");
	mainPanel.add(description);
	mainPanel.setWidgetLeftWidth(description, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(description, 9, Unit.EM, 3, Unit.EM);
	final TextBox fields = new TextBox();
	//rewrite... gah
	fields.setTitle("Number of editable fields to fill out when this step is used.");
	mainPanel.add(fields);
	mainPanel.setWidgetLeftWidth(fields, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(fields, 13, Unit.EM, 3, Unit.EM);
	final TextArea fieldDescriptions = new TextArea();
	mainPanel.add(fieldDescriptions);
	fieldDescriptions.setTitle("Description of the fields used, seperated by a comma.");
	mainPanel.setWidgetLeftWidth(fieldDescriptions, 1, Unit.EM, 20, Unit.EM);
	mainPanel.setWidgetTopHeight(fieldDescriptions, 17, Unit.EM, 6, Unit.EM);
	final Button newNode = new Button("New Step");
	mainPanel.add(newNode);
	mainPanel.setWidgetLeftWidth(newNode, 1, Unit.EM, 7, Unit.EM);
	mainPanel.setWidgetTopHeight(newNode, 25, Unit.EM, 3, Unit.EM);

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
					//TODO replace this with better error message handling
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
					t.removeItem(this.lastSelected);
					this.lastSelected.setParentTagID(selected.getTagID());
				}else {
					this.lastSelected = new ValidationTreeNode(tagID.getText(), selected.getTagID(), "New Step", Integer.valueOf(0));
					newNode.setEnabled(true);
				}
				selected.addItem(this.lastSelected);
				t.setSelectedItem(this.lastSelected, true);
			}
		}
    }

	final TreeHandler treeHandler = new TreeHandler();
	t.addSelectionHandler(treeHandler);

	class EnterPressHandler implements KeyPressHandler, BlurHandler{

		@Override
		public void onKeyPress(KeyPressEvent event) {
			Object source = event.getSource();
			if(source instanceof TextBox) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					SelectionEvent.fire(t, t.getSelectedItem());
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
			String highestTag = getHighestTag(t);
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
			ArrayList<TreeItem> convertable = getAllItemsFromTree(t);
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

private String getHighestTag(Tree t) {
	String returnable = "";
	for(int a = 0; a < t.getItemCount(); a++) {
		String compare = compareChildren((ValidationTreeNode)t.getItem(a));
		if(returnable.compareTo(compare) < 0) {
			returnable = compare;
		}
	}
	return returnable;
}

private ArrayList<TreeItem> getAllItemsFromTree(Tree t){
	ArrayList<TreeItem> items = new ArrayList<TreeItem>();
	for(int a = 0; a < t.getItemCount(); a++) {
		items.add(t.getItem(a));
		items.addAll(getChildItems(t.getItem(a)));
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

private void builtStepPanel(final Button saveButton, final Button generateCode,
		final LayoutPanel mainPanel, ScrollPanel flexPanel) {
	mainPanel.add(saveButton);
	mainPanel.setWidgetLeftWidth(saveButton, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(saveButton, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(generateCode);
	mainPanel.setWidgetLeftWidth(generateCode, 12, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(generateCode, 1, Unit.EM, 3, Unit.EM);
	flexPanel.add(this.stepFlexTable);
	flexPanel.ensureVisible(this.stepFlexTable);
	mainPanel.add(flexPanel);
	mainPanel.setWidgetLeftWidth(flexPanel, 1, Unit.EM, 100, Unit.EM);
	mainPanel.setWidgetTopHeight(flexPanel, 5, Unit.EM, 100, Unit.EM);
}

private void buildValidationTreePanel(final LayoutPanel westPanel,
		Tree t, Button openValidationDialog) {
	westPanel.add(t);
	westPanel.add(openValidationDialog);
	westPanel.setWidgetLeftWidth(openValidationDialog, 1, Unit.EM, 15, Unit.EM);
	westPanel.setWidgetBottomHeight(openValidationDialog, 1, Unit.EM, 3, Unit.EM);
}

private Tree buildTree() {
	final Tree t = new Tree();
	// Initialize the service proxy.
    if (this.treeBuildingService == null) {
    	this.treeBuildingService = GWT.create(TreeBuilderService.class);
    }

    // Set up the callback object.
    AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>> callback = new AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>>() {
      public void onFailure(Throwable caught) {
    	  t.addItem("Failure!");
      }

	@Override
	public void onSuccess(HashMap<String, ArrayList<ValidationTreeDataItem>> result) {
		ArrayList<ValidationTreeDataItem> roots = result.get("root");
		for (ValidationTreeDataItem items : roots) {
			ValidationTreeNode node = new ValidationTreeNode(items);
			if(result.containsKey(node.getTagID())) {
				addChildrenToTree(node, result);
			}
			t.addItem(node);
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
    return t;
}

private TabLayoutPanel buildSetupPanel() {
	final TabLayoutPanel setupPanel = new TabLayoutPanel(.7, Unit.CM);

	//TODO add in the items for the tabs and check boxes
	//possibly allow for added patrons to flag into the bib box to allow for selecting them to checkout to?

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
    		  setupPanel.add(panel, tab);
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
	return setupPanel;
}

private void buildMainPanel(final LayoutPanel mainPanel, final TabLayoutPanel setupPanel,
		SplitLayoutPanel p, LayoutPanel westPanel) {
	p.addWest(westPanel, 256);
	p.addNorth(setupPanel, 256);
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
}

