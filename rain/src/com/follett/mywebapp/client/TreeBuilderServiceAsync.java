package com.follett.mywebapp.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeBuilderServiceAsync {

  void getTreeItems(AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>> callback);
  void saveTreeItems(ArrayList<ValidationTreeDataItem> nodes,
		AsyncCallback<Boolean> callback);

}