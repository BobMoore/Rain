package com.follett.mywebapp.server;


import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeBuilder")
public interface SetupBuilderService extends RemoteService {

  HashMap<String, ArrayList<String>> getTreeItems();
}