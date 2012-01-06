package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.server.TreeBuilderService;
import com.follett.mywebapp.server.TreeBuilderServiceAsync;
import com.follett.mywebapp.util.CodeContainer;
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

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

	    final Button addStepButton = new Button("Add Step");
	    final Button saveButton = new Button("Save Test");
	    final Button generateCode = new Button("Generate Code");
	    final Button addSetup = new Button("Add setup!");
	    final TextBox addStepField = new TextBox();
	    final TextBox selectedText = new TextBox();
	    final LayoutPanel mainPanel = new LayoutPanel();
	    final LayoutPanel bookSetupPanel = new LayoutPanel();
	    final LayoutPanel patronSetupPanel = new LayoutPanel();
	    final LayoutPanel siteSetupPanel = new LayoutPanel();
	    final LayoutPanel buttonPanel = new LayoutPanel();
	    final LayoutPanel westPanel = new LayoutPanel();
	    final TabLayoutPanel setupPanel = new TabLayoutPanel(.7, Unit.CM);
	    final int width = 10;
	    final int height = 3;
	    final int columnOne = 1;
	    final int rowOne = 1;
	    final int columnTwo = 12;
	    final int rowTwo = 4;
	    final int columnThree = 23;
	    Tree t = new Tree();
	    setStepFlexTable(new FlexTable());

	    t = buildTree();

	    SplitLayoutPanel p = new SplitLayoutPanel();

	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(p);

	    buildSetupPanel(setupPanel, patronSetupPanel, bookSetupPanel, siteSetupPanel);

		buildWestPanel(addStepButton, addStepField, westPanel, t);

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
					Button removeStepButton = new Button("x");
					int buttonOffset = 0;
					if(selected.getFields() != null) {
						for(int a = 0; a < selected.getFields().intValue(); a++) {
							TextBox box = new TextBox();
							box.setName(selected.getTagID());
							getStepFlexTable().setWidget(getValidationRow(), 1 + buttonOffset, box);
							buttonOffset++;
						}
					}
					removeStepButton.addClickHandler(new removeStepHandler(getStartKey() + ""));
					getStepFlexTable().setWidget(getValidationRow(), 1 + buttonOffset, removeStepButton);
					addValidationStep(getValidationRow(),selected.getText());
					addIdentifierKey(getValidationRow(), getStartKey() + "");
					bumpValidationRow();
					bumpStartKey();
				} else {
					ValidationTreeNode node = new ValidationTreeNode("New tagID", selected.getTagID(), addStepField.getText(), Integer.valueOf(0));
					selected.addItem(node);
				}
			}

			class removeStepHandler implements ClickHandler {
				String myKey;

				public removeStepHandler(String newKey) {
					this.myKey = newKey;
				}

				public void onClick(ClickEvent event) {
					int removedIndex = getIdentifierKey().indexOf(this.myKey);
					removeIndexKey(removedIndex);
					removeIndexStep(removedIndex);
					removeStepFlexTableRow(removedIndex);
					reduceValidationRow();
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

    class SetupHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			getStepFlexTable().insertRow(getSetupRow());
			getStepFlexTable().setText(getSetupRow(), 0, selectedText.getText());
			Button removeStepButton = new Button("x");
			removeStepButton.addClickHandler(new removeStepHandler(getStartKey() + ""));
			getStepFlexTable().setWidget(getSetupRow(), 1, removeStepButton);
			addValidationStep(getSetupRow(), selectedText.getText());
			addIdentifierKey(getSetupRow(), getStartKey() + "");
			bumpSetupRow();
			bumpStartKey();
		}

		class removeStepHandler implements ClickHandler {
			String myKey;

			public removeStepHandler(String newKey) {
				this.myKey = newKey;
			}

			public void onClick(ClickEvent event) {
				int removedIndex = getIdentifierKey().indexOf(this.myKey);
				removeIndexKey(removedIndex);
				removeIndexStep(removedIndex);
				removeStepFlexTableRow(removedIndex);
				reduceSetupRow();
			}
		}
    }

    class GenerateCodeHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			// TODO Generate the code!
			//step one get a list of tags and variables to be sent
			CodeContainer testCode = new CodeContainer();
			String tagID;
			ArrayList<String> variables;
			FlexTable instructionTable = getStepFlexTable();
			//THIS NEEDS TO IGNORE THE STEP FIELDS OR IT WILL BREAK!
			for(int a = 1; a < instructionTable.getRowCount(); a++) {
				if(a != getSetupRow()) {
					//scroll through the boxes to see if they are text fields or buttons
					int b = 1;
					Widget w = instructionTable.getWidget(a, b);
					variables = new ArrayList<String>();
					while(!(w instanceof Button)) {
						TextBox box = (TextBox)w;
						variables.add(box.getText());
						b++;
						w = instructionTable.getWidget(a, b);
					}
					Button tagName = (Button)w;
					testCode.addStep(tagName.getText(), variables);
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
    SetupHandler setupHandler = new SetupHandler();
    addSetup.addClickHandler(setupHandler);
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

private void buildWestPanel(final Button addStepButton,
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

private void buildSetupPanel(final TabLayoutPanel setupPanel, LayoutPanel patronSetupPanel, LayoutPanel bookSetupPanel, LayoutPanel siteSetupPanel) {
	//TODO add in the items for the tabs and check boxes
	//possibly allow for added patrons to flag into the bib box to allow for selecting them to checkout to?
	createBookSetupPanel(bookSetupPanel);
	setupPanel.add(bookSetupPanel, "Bibs");
	createPatronSetupPanel(patronSetupPanel);
	setupPanel.add(patronSetupPanel,"Patrons");
	createSiteSetupPanel(siteSetupPanel);
	setupPanel.add(siteSetupPanel,"Sites");
}

private void createBookSetupPanel(LayoutPanel bookSetupPanel) {
	FlexTable bibTable = new FlexTable();
	Button addSetupButton = new Button("Add this!");
	//TODO read this data in from sql
	String[] columnHeadings = {"Material Type", "Checked out?"};
	String materialGroup = "materialTypeGroup";
	String[] buttonNames = {"Library book", "Textbook", "Digital Resource", "eBook - Shelf", "eBook - Local"};
	String checkOutGroup = "checkOutGroup";
	String[] checkedOut = {"Available", "Checked out - current", "Checked out - past"};
	for(int a = 0; a < columnHeadings.length; a++) {
		bibTable.setText(0, a, columnHeadings[a]);
	}
	for(int a = 0; a < buttonNames.length; a++) {
		bibTable.setWidget(a + 1, 0, new RadioButton(materialGroup, buttonNames[a]));
	}
	for(int a = 0; a < checkedOut.length; a++) {
		bibTable.setWidget(a + 1, 1, new RadioButton(checkOutGroup, checkedOut[a]));
	}
	bibTable.setWidget(0, 6, addSetupButton);
	bookSetupPanel.add(bibTable);
}

private void createPatronSetupPanel(LayoutPanel patronSetupTable) {
	FlexTable patronTable = new FlexTable();
	Button addSetupButton = new Button("Add this!");
	//TODO read this data in from sql
	String[] columnHeadings = {"Patron Type", "Login Type", "Permissions"};
	String patronGroup = "patronTypeGroup";
	String[] patronTypes = {"Guest", "Patron", "Teacher", "Admin", "Cataloger"};
	String loginGroup = "loginGroup";
	String[] loginTypes = {"No Login", "Login"};
	String[] permissions = {"Checkout Library Materials", "Checkout Textbooks", "Search Using Destiny Quest", "View Fines"};
	for(int a = 0; a < columnHeadings.length; a++) {
		patronTable.setText(0, a, columnHeadings[a]);
	}
	for(int a = 0; a < patronTypes.length; a++) {
		patronTable.setWidget(a + 1, 0, new RadioButton(patronGroup, patronTypes[a]));
	}
	for(int a = 0; a < loginTypes.length; a++) {
		patronTable.setWidget(a + 1, 1, new RadioButton(loginGroup, loginTypes[a]));
	}
	for(int a = 0; a < permissions.length; a++) {
		patronTable.setWidget(a + 1, 2, new CheckBox(permissions[a]));
	}
	patronTable.setWidget(0, 6, addSetupButton);
	patronSetupTable.add(patronTable);
}

private void createSiteSetupPanel(LayoutPanel siteSetupTable) {
	FlexTable siteTable = new FlexTable();
	Button addSetupButton = new Button("Add this!");
	//TODO read this data in from sql
	String[] columnHeadings = {"Products", "Third Party", "Permissions"};
	String[] siteTypes = {"Library", "Textbook", "Asset", "Media"};
	String[] thirdPartyTypes = {"Digital Resources", "One Search", "Fountas and Pinnell", "Reading Program Service", "Standards", "TitlePeek", "WebPath Express"};
	for(int a = 0; a < columnHeadings.length; a++) {
		siteTable.setText(0, a, columnHeadings[a]);
	}
	for(int a = 0; a < siteTypes.length; a++) {
		siteTable.setWidget(a + 1, 0, new CheckBox(siteTypes[a]));
	}
	for(int a = 0; a < thirdPartyTypes.length; a++) {
		siteTable.setWidget(a + 1, 1, new CheckBox(thirdPartyTypes[a]));
	}
	siteTable.setWidget(0, 6, addSetupButton);
	siteSetupTable.add(siteTable);
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

