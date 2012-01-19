package com.follett.mywebapp.server;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CodeBuilderServiceAsync {

  void getCodePieces(AsyncCallback<ArrayList<String>> callback);
  void saveTest(int testNumber, String testSteps, AsyncCallback<Boolean> callback);
  void doesTestExist(int TestNumber, AsyncCallback<Boolean> callback);
  void getTest(int TestNumber, AsyncCallback<String> callback);
}