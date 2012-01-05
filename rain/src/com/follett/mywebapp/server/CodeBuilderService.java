package com.follett.mywebapp.server;


import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeBuilder")
public interface CodeBuilderService extends RemoteService {

  ArrayList<String> getCodePieces();
}