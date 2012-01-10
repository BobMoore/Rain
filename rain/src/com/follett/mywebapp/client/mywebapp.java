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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
	    final Button addStepButton = new Button("Add Step");
	    final Button saveButton = new Button("Save Test");
	    final Button generateCode = new Button("Generate Code");
	    final TextBox addStepField = new TextBox();
	    final LayoutPanel mainPanel = new LayoutPanel();
	    final LayoutPanel buttonPanel = new LayoutPanel();
	    final LayoutPanel westPanel = new LayoutPanel();
	    final TabLayoutPanel setupPanel = buildSetupPanel();
	    Tree t = new Tree();
	    setStepFlexTable(new FlexTable());

	    t = buildTree();

	    SplitLayoutPanel p = new SplitLayoutPanel();

	    toolPanel.add(p, "Test Developement");

	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(toolPanel);



		buildValidationTreePanel(addStepButton, addStepField, westPanel, t);

		builtStepPanel(saveButton, generateCode, mainPanel);

	    buildMainPanel(mainPanel, buttonPanel, setupPanel, t, p, westPanel);

	    buildStepTable();

	    //Listeners
	    class TreeHandler implements SelectionHandler<TreeItem>{

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				ValidationTreeNode selected = (ValidationTreeNode)event.getSelectedItem();
				if(!isEditTree()) {
					getStepFlexTable().setText(getValidationRow(), 0, selected.getText());
					StepHolder removeStepButton = new StepHolder("x", selected.getTagID());
					int buttonOffset = 0;
					if(selected.getFields() != null) {
						for(int a = 0; a < selected.getFields().intValue(); a++) {
							TextboxIDHolder box = new TextboxIDHolder(selected.getTagID());
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
				} else {
					//TODO add in generation of new tagID
					ValidationTreeNode node = new ValidationTreeNode("New tagID", selected.getTagID(), addStepField.getText(), Integer.valueOf(0));
					selected.addItem(node);
				}
			}
	    }

    // Create a handler for the sendButton and nameField
    class MyHandler implements ClickHandler, KeyUpHandler{
    	private int totalItems = 3;

      /**
       * Fired when the user clicks on any step.
       */
      public void onClick(ClickEvent event) {
    	  if(isEditTree()) {
    		  setEditTree(false);
    		  addStepButton.setText("Add Step");
    	  }else {
    		  setEditTree(true);
    		  addStepButton.setText("Editing...");
    	  }
      }
	/**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        }
      }

      @SuppressWarnings("unused")
	public void setTotalItems(int totalItems) {
    	  this.totalItems = totalItems;
      }


      @SuppressWarnings("unused")
	public int getTotalItems() {
    	  return this.totalItems;
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

    // Add a handler to send the name to the server
    MyHandler handler = new MyHandler();
    addStepButton.addClickHandler(handler);
    addStepField.addKeyUpHandler(handler);
    TreeHandler tHandler = new TreeHandler();
    t.addSelectionHandler(tHandler);
    GenerateCodeHandler cHandler = new GenerateCodeHandler();
    generateCode.addClickHandler(cHandler);
  }

private void builtStepPanel(final Button saveButton, final Button generateCode,
		final LayoutPanel mainPanel) {
	mainPanel.add(saveButton);
	mainPanel.setWidgetLeftWidth(saveButton, 1, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(saveButton, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(generateCode);
	mainPanel.setWidgetLeftWidth(generateCode, 12, Unit.EM, 10, Unit.EM);
	mainPanel.setWidgetTopHeight(generateCode, 1, Unit.EM, 3, Unit.EM);
	mainPanel.add(getStepFlexTable());
	mainPanel.setWidgetLeftWidth(getStepFlexTable(), 1, Unit.EM, 100, Unit.EM);
	mainPanel.setWidgetTopHeight(getStepFlexTable(), 5, Unit.EM, 100, Unit.EM);
}

private void buildValidationTreePanel(final Button addStepButton,
		final TextBox addStepField, final LayoutPanel westPanel, Tree t) {
	westPanel.add(t);
	westPanel.add(addStepButton);
	westPanel.setWidgetLeftWidth(addStepButton, 1, Unit.EM, 6, Unit.EM);
	westPanel.setWidgetBottomHeight(addStepButton, 1, Unit.EM, 3, Unit.EM);
	westPanel.add(addStepField);
	westPanel.setWidgetLeftWidth(addStepField, 8, Unit.EM, 10, Unit.EM);
	westPanel.setWidgetBottomHeight(addStepField, 1, Unit.EM, 3, Unit.EM);
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
		ArrayList<ValidationTreeDataItem> roots = result.get(null);
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

private void buildMainPanel(final LayoutPanel mainPanel, final LayoutPanel buttonPanel,
		final TabLayoutPanel setupPanel, final Tree t,
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

