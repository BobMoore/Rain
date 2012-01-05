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

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * The data source for contact information used in the sample.
 */
public class PatronDatabase {

	public static final String[] typeNames = {"Student", "Teacher", "Admin", "Cataloger"};
	public static final String[] loginTypes = {"No Login", "Random Login", "Specific Login"};
	public static final String[] accessLevels = {"Login", "AED Everything", "Reach your Destiny!"};
  /**
   * A contact category.
   */
  public static class PatronType implements Comparable<PatronType> {

    private final String typeName;
    private PatronLoginTypes[] logins;

    private PatronType(String typeName) {
      this.typeName = typeName;
      this.logins = new PatronLoginTypes[loginTypes.length];
      for (int i = 0; i < loginTypes.length; i++) {
    	  this.logins[i] = new PatronLoginTypes(this, loginTypes[i]);
      }
    }

    public String getDisplayName() {
    	return this.typeName;
    }

	@Override
	public int compareTo(PatronType o) {
		return (o == null || o.typeName == null) ? -1
				: -o.typeName.compareTo(this.typeName);
	}

	public PatronLoginTypes[] getLogins() {
		return this.logins;
	}
  }
  /**
   * Tracks the number of contacts in a category that begin with the same
   * letter.
   */
  public static class PatronLoginTypes implements Comparable<PatronLoginTypes> {
    private PatronType type;
    private PatronAccessLevel[] myAccessLevels;
    String loginType;

    /**
     * Construct a new {@link PatronLoginTypes} for one contact.
     *
     * @param category the category
     */
    public PatronLoginTypes(PatronType type, String loginType) {
    	this.type = type;
    	this.loginType = loginType;
    	this.myAccessLevels = new PatronAccessLevel[accessLevels.length];
    	for (int i = 0; i < accessLevels.length; i++) {
    		this.myAccessLevels[i] = new PatronAccessLevel(this, accessLevels[i]);
    	}
    }

    public int compareTo(PatronLoginTypes o) {
      return (o == null || o.loginType == null) ? -1
				: -o.loginType.compareTo(this.loginType);
    }

    @Override
    public boolean equals(Object o) {
      return compareTo((PatronLoginTypes) o) == 0;
    }

	public PatronAccessLevel[] getMyAccessLevels() {
		return this.myAccessLevels;
	}
  }
  /**
   * Information about a contact.
   */
  public static class PatronAccessLevel implements Comparable<PatronAccessLevel> {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<PatronAccessLevel> KEY_PROVIDER = new ProvidesKey<PatronAccessLevel>() {
      public Object getKey(PatronAccessLevel item) {
        return item == null ? null : Integer.valueOf(item.getId());
      }
    };

    private static int nextId = 0;

    private String accessLevelID;
    private PatronLoginTypes loginType;
    private final int id;

    public PatronAccessLevel(PatronLoginTypes type, String id) {
      this.id = nextId;
      nextId++;
      this.accessLevelID = id;
      this.loginType = type;
    }

    public PatronAccessLevel(String level) {
    	this.id = nextId;
    	nextId++;
    	this.accessLevelID = level;
    }

    public int compareTo(PatronAccessLevel o) {
      return (o == null || o.accessLevelID == null) ? -1
          : -o.accessLevelID.compareTo(this.accessLevelID);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof PatronAccessLevel) {
        return this.id == ((PatronAccessLevel) o).id;
      }
      return false;
    }

    /**
     * @return the contact's firstName
     */
    public String getaccessLevelID() {
      return this.accessLevelID;
    }

    /**
     * @return the unique ID of the contact
     */
    public int getId() {
      return this.id;
    }

  }

  /**
   * The singleton instance of the database.
   */
  private static PatronDatabase instance;

  /**
   * Get the singleton instance of the contact database.
   *
   * @return the singleton instance
   */
  public static PatronDatabase get() {
    if (instance == null) {
      instance = new PatronDatabase();
    }
    return instance;
  }

  /**
   * The provider that holds the list of contacts in the database.
   */
  private ListDataProvider<PatronAccessLevel> dataProvider = new ListDataProvider<PatronAccessLevel>();

  private final PatronType[] types;

  /**
   * Construct a new contact database.
   */
  private PatronDatabase() {
    this.types = new PatronType[typeNames.length];
    for (int i = 0; i < typeNames.length; i++) {
      this.types[i] = new PatronType(typeNames[i]);
    }
  }

  /**
   * Add a new contact.
   *
   * @param contact the contact to add.
   */
  public void addContact(PatronAccessLevel contact) {
    List<PatronAccessLevel> contacts = this.dataProvider.getList();
    // Remove the contact first so we don't add a duplicate.
    contacts.remove(contact);
    contacts.add(contact);
  }

  /**
   * Add a display to the database. The current range of interest of the display
   * will be populated with data.
   *
   * @param display a {@Link HasData}.
   */
  public void addDataDisplay(HasData<PatronAccessLevel> display) {
    this.dataProvider.addDataDisplay(display);
  }

  public ListDataProvider<PatronAccessLevel> getDataProvider() {
    return this.dataProvider;
  }

  /**
   * Refresh all displays.
   */
  public void refreshDisplays() {
    this.dataProvider.refresh();
  }

public PatronType[] getTypes() {
	return this.types;
}

}