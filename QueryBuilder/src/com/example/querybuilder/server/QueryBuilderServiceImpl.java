// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.util.List;

import com.example.querybuilder.client.QueryBuilderService;
import com.example.querybuilder.client.Utilities;
import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.SerializableClasses;
import com.example.querybuilder.shared.ServerException;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class QueryBuilderServiceImpl extends RemoteServiceServlet implements QueryBuilderService
{
  private DataWarehouse dataWarehouse = DataWarehouse.getInstance();

  @Override
  public SerializableClasses addSerializableClassesToWhiteList(SerializableClasses serializableClasses) throws ServerException
  {
    try
    {
      return serializableClasses;
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public TreeModel createConnection(String connectionName, String url, String userId, String password, String description) throws ServerException
  {
    try
    {
      return dataWarehouse.createConnection(connectionName, url, userId, password, description);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public TreeModel deleteConnections(List<String> connectionNames) throws ServerException
  {
    try
    {
      return dataWarehouse.deleteConnections(connectionNames);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public TreeModel deleteTables(List<Integer> nickNames) throws ServerException
  {
    try
    {
      return dataWarehouse.deleteTables(nickNames);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  protected void doUnexpectedFailure(Throwable e)
  {
    System.err.println("******************* doUnexpectedFailure *******************");
    e.printStackTrace();
    super.doUnexpectedFailure(e);
  }

  @Override
  public TreeModel getMasterTree() throws ServerException
  {
    try
    {
      return dataWarehouse.getMasterTree();
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public PageResult getPage(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig) throws ServerException
  {
    try
    {
      return dataWarehouse.getPage(queryDescriptor, pagingLoadConfig);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public List<ColumnDefinition> getColumnDefinitions(TableReference tableReference) throws ServerException
  {
    try
    {
      return dataWarehouse.getColumnDefinitions(tableReference);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public TreeModel importTables(List<TableReference> tableReferences, boolean isGenerateVersion) throws ServerException
  {
    try
    {
      return dataWarehouse.importTables(tableReferences, isGenerateVersion);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

  @Override
  public QueryResult runQuery(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig) throws ServerException
  {
    try
    {
      return dataWarehouse.runQuery(queryDescriptor, pagingLoadConfig);
    }
    catch (RuntimeException e)
    {
      throw new ServerException(Utilities.getStackTrace(e));
    }
  }

}
