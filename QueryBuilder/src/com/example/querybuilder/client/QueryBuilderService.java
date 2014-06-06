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
import com.example.querybuilder.shared.ServerException;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("queryBuilderService")
public interface QueryBuilderService extends RemoteService
{
  public SerializableClasses addSerializableClassesToWhiteList(SerializableClasses serializableClasses) throws ServerException;

  public TreeModel createConnection(String connectionName, String url, String userId, String password, String description) throws ServerException;

  public TreeModel deleteConnections(List<String> connectionNames) throws ServerException;

  public TreeModel deleteTables(List<Integer> nickNames) throws ServerException;

  public TreeModel getMasterTree() throws ServerException;

  public PageResult getPage(QueryDescriptor queryDescriptor, PagingLoadConfig loadConfig) throws ServerException;

  public List<ColumnDefinition> getColumnDefinitions(TableReference tableReference) throws ServerException;

  public TreeModel importTables(List<TableReference> tableReferences, boolean isGenerateVersion) throws ServerException;

  public QueryResult runQuery(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig) throws ServerException;

}
