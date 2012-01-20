package com.follett.mywebapp.server;



import com.follett.mywebapp.util.TableData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("codeBuilder")
public interface CodeBuilderService extends RemoteService {

  TableData getSetupPiece(String tagID);
  Boolean saveTest(int testNumber, String testSteps);
  Boolean doesTestExist(int TestNumber);
  String getTest(int TestNumber);
}