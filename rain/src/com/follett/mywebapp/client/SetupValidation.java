package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.client.mywebapp.TreeHandler;
import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.follett.mywebapp.util.ValidationTreeNode;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class SetupValidation {

	private TreeBuilderServiceAsync treeBuildingService = GWT.create(TreeBuilderService.class);
	private LayoutPanel treePanel;
	private Tree t;
	private TreeHandler tHandler;
	final Button openValidationDialog = new Button("Edit Validation Steps");

	public SetupValidation (LayoutPanel treePanel, TreeHandler tHandler) {
		this.treePanel = treePanel;
		this.t = buildTree();
		this.treePanel.add(this.t);
		this.treePanel.add(this.openValidationDialog);
		this.treePanel.setWidgetLeftWidth(this.openValidationDialog, 1, Unit.EM, 15, Unit.EM);
		this.treePanel.setWidgetBottomHeight(this.openValidationDialog, 1, Unit.EM, 3, Unit.EM);
		ValidationDialog dialogHandler = new ValidationDialog();
	    this.openValidationDialog.addClickHandler(dialogHandler);
	    this.tHandler = tHandler;
		this.t.addSelectionHandler(tHandler);
	}

	public DialogBox createValidationDialogBox() {
		final DialogBox dialogBox = new DialogBox(false);

		Button closeButton = new Button(
				"Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						resetTree();
						dialogBox.hide();
					}
				});

		dialogBox.setWidget(buildSetupValidation(closeButton));
		dialogBox.setGlassEnabled(true);
		return dialogBox;
	}

	private LayoutPanel buildSetupValidation(Button closeButton) {
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
		final Button makeParentRoot = new Button("Make Root Item");
		panel.add(makeParentRoot);
		panel.setWidgetLeftWidth(makeParentRoot, 38, Unit.EM, 10, Unit.EM);
		panel.setWidgetTopHeight(makeParentRoot, 1, Unit.EM, 3, Unit.EM);
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

		class TreeSelectionHandler implements SelectionHandler<TreeItem>{

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

		final TreeSelectionHandler treeHandler = new TreeSelectionHandler();
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

		class MakeParentRootHandler implements ClickHandler {

			@Override
			public void onClick(ClickEvent event) {
				updateParent.setText("Update Parent");
				updateParent.setEnabled(true);
				if(newNode.isEnabled()) {
					//this is a non-new item. We are just going to update the selected parent to the root and redraw the tree.
				}else {
					//this is a new item that is added to the root. add it and redraw the tree
					String highestTag = getHighestTag(innerTree);
					newNode.setEnabled(true);
					tagID.setText(ValidationTreeNode.incrementTagID(highestTag));
					description.setText("");
					fields.setText("");
					fieldDescriptions.setText("");
				}
			}
		}

		NewStepHandler newNodeHandler = new NewStepHandler();
		newNode.addClickHandler(newNodeHandler);

		class SaveStepsHandler implements ClickHandler {

			@Override
			public void onClick(ClickEvent event) {
				ArrayList<TreeItem> convertable = getAllItemsFromTree(innerTree);
				ArrayList<ValidationTreeDataItem> sendable = new ArrayList<ValidationTreeDataItem>();
				for (TreeItem item : convertable) {
					ValidationTreeDataItem data = new ValidationTreeDataItem((ValidationTreeNode)item);
					sendable.add(data);
				}

				if (SetupValidation.this.treeBuildingService == null) {
					SetupValidation.this.treeBuildingService = GWT.create(TreeBuilderService.class);
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
				SetupValidation.this.treeBuildingService.saveTreeItems(sendable, callback);
			}

		}
		SaveStepsHandler save = new SaveStepsHandler();
		saveSteps.addClickHandler(save);

		return panel;
	}
	private Tree buildTree() {
		final Tree newTree = new Tree();
		if (this.treeBuildingService == null) {
			this.treeBuildingService = GWT.create(TreeBuilderService.class);
		}

		AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>> callback = new AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>>() {
			public void onFailure(Throwable caught) {
				newTree.addItem(caught.getMessage());
			}

			@Override
			public void onSuccess(HashMap<String, ArrayList<ValidationTreeDataItem>> result) {
				ArrayList<ValidationTreeDataItem> roots = result.get("root");
				if(roots != null) {
					for (ValidationTreeDataItem items : roots) {
						ValidationTreeNode node = new ValidationTreeNode(items);
						if(result.containsKey(node.getTagID())) {
							addChildrenToTree(node, result);
						}
						newTree.addItem(node);
					}
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

	public void resetTree() {
		this.treePanel.remove(this.t);
		this.t = buildTree();
		this.treePanel.add(this.t);
		this.treePanel.setWidgetLeftWidth(this.t, 0, Unit.PX, 256, Unit.PX);
		this.treePanel.setWidgetTopHeight(this.t, 0, Unit.PX, 768, Unit.PX);
		this.t.addSelectionHandler(this.tHandler);
	}

	private String getHighestTag(Tree tree) {
		String returnable = "";
		for(int a = 0; a < tree.getItemCount(); a++) {
			String compare = compareChildren((ValidationTreeNode)tree.getItem(a));
			if(returnable.compareTo(compare) < 0) {
				returnable = compare;
			}
		}
		if(returnable == "") {
			returnable = "AAAAAA";
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

    class ValidationDialog implements ClickHandler {

    	DialogBox dialog;

		@Override
		public void onClick(ClickEvent event) {
			this.dialog = createValidationDialogBox();
			this.dialog.show();
			this.dialog.center();
		}
    }
}
