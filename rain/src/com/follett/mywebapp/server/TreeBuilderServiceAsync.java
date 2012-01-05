package com.follett.mywebapp.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeBuilderServiceAsync {

  void getTreeItems(AsyncCallback<HashMap<String, ArrayList<ValidationTreeDataItem>>> callback);

}