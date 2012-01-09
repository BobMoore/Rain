package com.follett.mywebapp.server;

import com.follett.mywebapp.util.SetupDataItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SetupBuilderServiceAsync {

  void getSetupData(AsyncCallback<SetupDataItem> callback);

}