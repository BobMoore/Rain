package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.List;

import com.follett.mywebapp.client.PatronDatabase.PatronAccessLevel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class mywebapp implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
	private FlexTable stepFlexTable;
	private FlexTable setupFlexTable;
	private int setupRow = 1;
	private int validationRow = 1;
	private boolean editTree = false;
	private ArrayList<String> validationSteps = new ArrayList<String>();
	private ArrayList<String> identifierKey = new ArrayList<String>();
	private int startKey = 10;


  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

	    // Create a tree with a few items in it.
	    final TreeItem root = new TreeItem("Log In");
	    final Button sendButton = new Button("Add Step");
	    final Button saveButton = new Button("Save Test");
	    final Button generateCode = new Button("Generate Code");
	    final Button addSetup = new Button("Add setup!");
	    final TextBox addStepField = new TextBox();
	    final TextBox selectedText = new TextBox();
	    final LayoutPanel mainPanel = new LayoutPanel();
	    final LayoutPanel buttonPanel = new LayoutPanel();
	    final TabLayoutPanel setupPanel = new TabLayoutPanel(.7, Unit.CM);
	    final int width = 10;
	    final int height = 3;
	    final int columnOne = 1;
	    final int rowOne = 1;
	    final int columnTwo = 12;
	    final int rowTwo = 4;
	    final int columnThree = 23;
	    final Tree t = new Tree();
	    setStepFlexTable(new FlexTable());
	    setSetupFlexTable(new FlexTable());


	    // Create a three-pane layout with splitters.
	    SplitLayoutPanel p = new SplitLayoutPanel();

	    CellBrowser cellBrowser;

	    final MultiSelectionModel<PatronAccessLevel> selectionModel = new MultiSelectionModel<PatronAccessLevel>(PatronDatabase.PatronAccessLevel.KEY_PROVIDER);
	    selectionModel.addSelectionChangeHandler(
	        new SelectionChangeEvent.Handler() {
	          public void onSelectionChange(SelectionChangeEvent event) {
	            StringBuilder sb = new StringBuilder();
	            boolean first = true;
	            List<PatronAccessLevel> selected = new ArrayList<PatronAccessLevel>(
	                selectionModel.getSelectedSet());
	            for (PatronAccessLevel value : selected) {
	              if (first) {
	                first = false;
	              } else {
	                sb.append(", ");
	              }
	              sb.append(value.getaccessLevelID());
	            }
	            selectedText.setText(sb.toString());
	          }
	        });

	    cellBrowser = new CellBrowser(
	        new PatronSelectViewModel(selectionModel), null);
	    cellBrowser.setAnimationEnabled(true);


	    RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(p);

	    buildButtonPanel(sendButton, saveButton, generateCode, addSetup,
				addStepField, selectedText, buttonPanel, width, height, columnOne,
				rowOne, columnTwo, rowTwo, columnThree);

	    buildSetupPanel(setupPanel, cellBrowser);

	    buildMainPanel(root, mainPanel, buttonPanel, setupPanel, t, p);

	    // Add it to the root tree. TODO get this to load in the actual items from the db for tree steps
	    root.addItem(new TreeItem("Circulation"));
	    root.addItem(new TreeItem("Catalog"));
	    buildStepTable();

	    //Listeners
	    class TreeHandler implements SelectionHandler<TreeItem>{

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selected = event.getSelectedItem();
				if(!isEditTree()) {
					getStepFlexTable().setText(getValidationRow(), 0, selected.getText());
					Button removeStepButton = new Button("x");
					removeStepButton.addClickHandler(new removeStepHandler(getStartKey() + ""));
					getStepFlexTable().setWidget(getValidationRow(), 1, removeStepButton);
					addValidationStep(getValidationRow(),selected.getText());
					addIdentifierKey(getValidationRow(), getStartKey() + "");
					bumpValidationRow();
					bumpStartKey();
				} else {
					selected.addItem(addStepField.getText());
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
    		  sendButton.setText("Add Step");
    	  }else {
    		  setEditTree(true);
    		  sendButton.setText("Editing...");
    	  }
      }
	/**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        }
      }

      public void setTotalItems(int totalItems) {
    	  this.totalItems = totalItems;
      }


      public int getTotalItems() {
    	  return totalItems;
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


    // Add a handler to send the name to the server
    MyHandler handler = new MyHandler();
    sendButton.addClickHandler(handler);
    addStepField.addKeyUpHandler(handler);
    SetupHandler setupHandler = new SetupHandler();
    addSetup.addClickHandler(setupHandler);
    TreeHandler tHandler = new TreeHandler();
    t.addSelectionHandler(tHandler);

  }

private void buildSetupPanel(final TabLayoutPanel setupPanel, CellBrowser tree) {
	//TODO add in the items for the tabs and checkboxes
	setupPanel.add(tree,"Patrons");
	setupPanel.add(new HTML("Sites"),"Sites");
}

private void buildButtonPanel(final Button sendButton, final Button saveButton,
		final Button generateCode, Button addSetup,
		final TextBox addStepField, TextBox selectedLabel, final LayoutPanel buttonPanel,
		final int width, final int height, final int columnOne,
		final int rowOne, final int columnTwo, final int rowTwo, int columnThree) {
	buttonPanel.add(sendButton);
	buttonPanel.setWidgetLeftWidth(sendButton, columnOne, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(sendButton, rowOne, Unit.EM, height, Unit.EM);
	buttonPanel.add(addStepField);
	buttonPanel.setWidgetLeftWidth(addStepField, columnOne, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(addStepField, rowTwo, Unit.EM, height, Unit.EM);
	buttonPanel.add(saveButton);
	buttonPanel.setWidgetLeftWidth(saveButton, columnTwo, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(saveButton, rowOne, Unit.EM, height, Unit.EM);
	buttonPanel.add(generateCode);
	buttonPanel.setWidgetLeftWidth(generateCode, columnTwo, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(generateCode, rowTwo, Unit.EM, height, Unit.EM);
	buttonPanel.add(addSetup);
	buttonPanel.setWidgetLeftWidth(addSetup, columnThree, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(addSetup, rowOne, Unit.EM, height, Unit.EM);
	buttonPanel.add(selectedLabel);
	buttonPanel.setWidgetLeftWidth(selectedLabel, columnThree, Unit.EM, width, Unit.EM);
	buttonPanel.setWidgetTopHeight(selectedLabel, rowTwo, Unit.EM, height, Unit.EM);

}

private void buildMainPanel(final TreeItem root, final LayoutPanel mainPanel,
		final LayoutPanel buttonPanel, final TabLayoutPanel setupPanel,
		final Tree t, SplitLayoutPanel p) {
	mainPanel.add(getStepFlexTable());
	t.addItem(root);
	p.addWest(t, 256);
	p.addNorth(setupPanel, 256);
	p.addNorth(buttonPanel, 100);
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
	return editTree;
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
	return identifierKey;
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
	return startKey;
}

public void bumpStartKey() {
	this.startKey++;
}

public FlexTable getSetupFlexTable() {
	return setupFlexTable;
}

public void setSetupFlexTable(FlexTable setupFlexTable) {
	this.setupFlexTable = setupFlexTable;
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

