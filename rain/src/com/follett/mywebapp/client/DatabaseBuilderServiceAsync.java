package com.follett.mywebapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DatabaseBuilderServiceAsync {

  void checkDatabase(AsyncCallback<Boolean> callback);
  void buildDatabase(AsyncCallback<Boolean> callback);

}