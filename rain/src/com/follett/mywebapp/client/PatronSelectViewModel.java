/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.List;

import com.follett.mywebapp.client.PatronDatabase.PatronAccessLevel;
import com.follett.mywebapp.client.PatronDatabase.PatronLoginTypes;
import com.follett.mywebapp.client.PatronDatabase.PatronType;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;


/**
 * The {@link TreeViewModel} used to organize contacts into a hierarchy.
 */
public class PatronSelectViewModel implements TreeViewModel {



  /**
   * The cell used to render categories.
   */
  private static class CategoryCell extends AbstractCell<PatronType> {

    /**
     * The html of the image used for contacts.
     */

    public CategoryCell() {
    }

    @Override
    public void render(Context context, PatronType value, SafeHtmlBuilder sb) {
      if (value != null) {
        sb.appendEscaped(value.getDisplayName());
      }
    }
  }

  /**
   * A Cell used to render the LetterCount.
   */
  private static class LetterCountCell extends AbstractCell<PatronLoginTypes> {

    @Override
    public void render(Context context, PatronLoginTypes value, SafeHtmlBuilder sb) {
      if (value != null) {
        sb.appendEscaped(value.loginType);
      }
    }
  }

  private final ListDataProvider<PatronType> categoryDataProvider;
  private final Cell<PatronAccessLevel> contactCell;
  private final DefaultSelectionEventManager<PatronAccessLevel> selectionManager =
      DefaultSelectionEventManager.createCheckboxManager();
  private final SelectionModel<PatronAccessLevel> selectionModel;

  public PatronSelectViewModel(final SelectionModel<PatronAccessLevel> selectionModel) {
    this.selectionModel = selectionModel;

    // Create a data provider that provides categories.
    categoryDataProvider = new ListDataProvider<PatronType>();
    List<PatronType> categoryList = categoryDataProvider.getList();
    for (PatronType category : PatronDatabase.get().getTypes()) {
      categoryList.add(category);
    }

    // Construct a composite cell for contacts that includes a checkbox.
    List<HasCell<PatronAccessLevel, ?>> hasCells = new ArrayList<HasCell<PatronAccessLevel, ?>>();
    hasCells.add(new HasCell<PatronAccessLevel, Boolean>() {

      private CheckboxCell cell = new CheckboxCell(true, false);

      public Cell<Boolean> getCell() {
        return cell;
      }

      public FieldUpdater<PatronAccessLevel, Boolean> getFieldUpdater() {
        return null;
      }

      public Boolean getValue(PatronAccessLevel object) {
        return Boolean.valueOf(selectionModel.isSelected(object));
      }
    });
    contactCell = new CompositeCell<PatronAccessLevel>(hasCells) {
      @Override
      public void render(Context context, PatronAccessLevel value, SafeHtmlBuilder sb) {
        sb.appendHtmlConstant("<table><tbody><tr>");
        sb.appendEscaped(value.getaccessLevelID());
        super.render(context, value, sb);
        sb.appendHtmlConstant("</tr></tbody></table>");
      }

      @Override
      protected Element getContainerElement(Element parent) {
        // Return the first TR element in the table.
        return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
      }

      @Override
      protected <X> void render(Context context, PatronAccessLevel value,
          SafeHtmlBuilder sb, HasCell<PatronAccessLevel, X> hasCell) {
        Cell<X> cell = hasCell.getCell();
        sb.appendHtmlConstant("<td>");
        cell.render(context, hasCell.getValue(value), sb);
        sb.appendHtmlConstant("</td>");
      }
    };
  }

  public <T> NodeInfo<?> getNodeInfo(T value) {
    if (value == null) {
      // Return top level categories.
      return new DefaultNodeInfo<PatronType>(categoryDataProvider,
          new CategoryCell());
    } else if (value instanceof PatronType) {
    	//implement adding the sublevel of patrontype
    	PatronType myType = ((PatronType)value);
    	ListDataProvider<PatronLoginTypes> patronTypeDataProvider = new ListDataProvider<PatronLoginTypes>();
        List<PatronLoginTypes> patronLoginTypeList = patronTypeDataProvider.getList();
        for (PatronLoginTypes login : myType.getLogins()) {
        	patronLoginTypeList.add(login);
        }
      return new DefaultNodeInfo<PatronLoginTypes>(
    		  patronTypeDataProvider,
          new LetterCountCell());
    } else if (value instanceof PatronLoginTypes) {
    	//implement adding the sublevel of patronlogintypes
    	PatronLoginTypes myAccess = ((PatronLoginTypes)value);
    	ListDataProvider<PatronAccessLevel> patronAccessDataProvider = new ListDataProvider<PatronAccessLevel>();
        List<PatronAccessLevel> patronAccessTypeList = patronAccessDataProvider.getList();
        for (PatronAccessLevel access : myAccess.getMyAccessLevels()) {
        	patronAccessTypeList.add(access);
        }
    	ListDataProvider<PatronAccessLevel> dataProvider = new ListDataProvider<PatronAccessLevel>();
      return new DefaultNodeInfo<PatronAccessLevel>(
    		  patronAccessDataProvider, contactCell, selectionModel, selectionManager, null);
    }

    // Unhandled type.
    String type = value.getClass().getName();
    throw new IllegalArgumentException("Unsupported object type: " + type);
  }

  public boolean isLeaf(Object value) {
    return value instanceof PatronAccessLevel;
  }
}