package com.follett.mywebapp.client;


import com.follett.mywebapp.util.SetupDataItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("setupBuilder")
public interface SetupBuilderService extends RemoteService {

  SetupDataItem getSetupData();
  Boolean saveSetupData(SetupDataItem allData);
}