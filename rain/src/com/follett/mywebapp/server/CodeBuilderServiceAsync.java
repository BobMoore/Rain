package com.follett.mywebapp.server;

import com.follett.mywebapp.util.TableData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CodeBuilderServiceAsync {

  void getSetupPiece(String tagID, AsyncCallback<TableData> callback);
  void saveTest(int testNumber, String testSteps, AsyncCallback<Boolean> callback);
  void doesTestExist(int TestNumber, AsyncCallback<Boolean> callback);
  void getTest(int TestNumber, AsyncCallback<String> callback);
}