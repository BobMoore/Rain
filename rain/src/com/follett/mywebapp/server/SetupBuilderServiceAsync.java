package com.follett.mywebapp.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SetupBuilderServiceAsync {

  void getTreeItems(AsyncCallback<HashMap<String, ArrayList<String>>> callback);

}