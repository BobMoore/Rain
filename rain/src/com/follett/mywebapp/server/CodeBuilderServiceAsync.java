package com.follett.mywebapp.server;

import java.util.ArrayList;

import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.StepTableData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CodeBuilderServiceAsync {

  void getSetupPiece(String tagID, AsyncCallback<ArrayList<StepTableData>> callback);
  void saveTest(int testNumber, String testSteps, AsyncCallback<Boolean> callback);
  void doesTestExist(int TestNumber, AsyncCallback<Boolean> callback);
  void getTest(int TestNumber, AsyncCallback<String> callback);
  void generateTemplatedCode(CodeContainer testCode, AsyncCallback<String> callback);
}