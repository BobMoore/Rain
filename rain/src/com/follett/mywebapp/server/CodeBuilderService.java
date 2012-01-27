package com.follett.mywebapp.server;

import java.util.ArrayList;

import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.StepTableData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("codeBuilder")
public interface CodeBuilderService extends RemoteService {

	ArrayList<StepTableData> getSetupPiece(String tagID);
	Boolean saveTest(int testNumber, String testSteps);
	Boolean doesTestExist(int TestNumber);
	String getTest(int TestNumber);
	public String generateTemplatedCode(CodeContainer testCode);
}