package com.follett.mywebapp.server;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CodeBuilderServiceAsync {

  void getCodePieces(AsyncCallback<ArrayList<String>> callback);

}