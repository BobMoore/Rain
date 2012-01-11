package com.follett.mywebapp.server;


import java.util.ArrayList;
import java.util.HashMap;

import com.follett.mywebapp.util.ValidationTreeDataItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("treeBuilder")
public interface TreeBuilderService extends RemoteService {

  HashMap<String, ArrayList<ValidationTreeDataItem>> getTreeItems();
  Boolean saveTreeItems(ArrayList<ValidationTreeDataItem> nodes);
}