package com.follett.mywebapp.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("databaseBuilder")
public interface DatabaseBuilderService extends RemoteService {

  Boolean checkDatabase();
  Boolean buildDatabase();
}