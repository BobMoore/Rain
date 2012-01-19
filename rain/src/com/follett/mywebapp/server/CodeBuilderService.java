package com.follett.mywebapp.server;


import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("codeBuilder")
public interface CodeBuilderService extends RemoteService {

  ArrayList<String> getCodePieces();
  Boolean saveTest(int testNumber, String testSteps);
  Boolean doesTestExist(int TestNumber);
  String getTest(int TestNumber);
}