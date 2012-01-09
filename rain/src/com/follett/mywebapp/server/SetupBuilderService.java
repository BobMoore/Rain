package com.follett.mywebapp.server;


import com.follett.mywebapp.util.SetupDataItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("setupBuilder")
public interface SetupBuilderService extends RemoteService {

  SetupDataItem getSetupData();
}