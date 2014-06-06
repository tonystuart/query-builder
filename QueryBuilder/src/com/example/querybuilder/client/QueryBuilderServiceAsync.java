// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.List;

import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.SerializableClasses;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QueryBuilderServiceAsync
{
  public void addSerializableClassesToWhiteList(SerializableClasses serializableClasses, AsyncCallback<SerializableClasses> callback);

  public void createConnection(String connectionName, String url, String userId, String password, String description, AsyncCallback<TreeModel> callback);

  public void deleteConnections(List<String> connectionNames, AsyncCallback<TreeModel> masterTreeCallback);

  public void deleteTables(List<Integer> tableIds, AsyncCallback<TreeModel> masterTreeCallback);

  public void getMasterTree(AsyncCallback<TreeModel> callback);

  public void getPage(QueryDescriptor queryDescriptor, PagingLoadConfig loadConfig, AsyncCallback<PageResult> pageCallback);

  public void getColumnDefinitions(TableReference tableReference, AsyncCallback<List<ColumnDefinition>> getColumnDefinitionsCallback);

  public void importTables(List<TableReference> tableReferences, boolean isGenerateVersion, AsyncCallback<TreeModel> tableCallback);

  public void runQuery(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig, AsyncCallback<QueryResult> queryResultCallback);

}
