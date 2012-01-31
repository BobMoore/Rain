package com.follett.mywebapp.client;

import com.follett.mywebapp.util.SetupDataItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SetupBuilderServiceAsync {

  void getSetupData(AsyncCallback<SetupDataItem> callback);
  void saveSetupData(SetupDataItem allData,
			AsyncCallback<Boolean> callback);

}