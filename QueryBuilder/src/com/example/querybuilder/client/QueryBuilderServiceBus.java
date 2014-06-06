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
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO: Eliminate callback as part of message. Replace with eventSource if
// necessary.

public class QueryBuilderServiceBus extends ServiceBus
{
  public static void addDescribeTabItem(ModelData modelData, List<ColumnDefinition> columnDefinition)
  {
    post(new AddDescribeTabItem(modelData, columnDefinition));
  }

  public static void addDisplayTabItem(ModelData modelData, QueryResult queryResult)
  {
    post(new AddDisplayTabItem(modelData, queryResult));
  }

  public static void addTablesToQueryBuilder(List<ModelData> selectedTables)
  {
    post(new AddTablesToQueryBuilder(selectedTables));
  }

  public static void clearStatus()
  {
    post(new ClearStatus());
  }

  public static void deleteConnections(List<String> connectionNames)
  {
    post(new DeleteConnections(connectionNames));
  }

  public static void deleteTables(List<Integer> tableIds)
  {
    post(new DeleteTables(tableIds));
  }

  public static void describeTable(ModelData selectedItem, TableReference tableReference)
  {
    post(new DescribeTable(selectedItem, tableReference));
  }

  public static void displayConnectionWindow()
  {
    post(new DisplayConnectionWindow());
  }

  public static void displayTable(ModelData selectedItem, TableReference tableReference)
  {
    post(new DisplayTable(selectedItem, tableReference));
  }

  public static void getMasterTree()
  {
    post(new GetMasterTree());
  }

  public static void getMasterTreeResults(TreeModel masterTree)
  {
    post(new GetMasterTreeResults(masterTree));
  }

  public static void getPage(QueryDescriptor queryDescriptor, PagingLoadConfig loadConfig, AsyncCallback<PageResult> callback)
  {
    post(new GetPage(queryDescriptor, loadConfig, callback));
  }

  public static void importTable(List<TableReference> tableReferences, boolean isGenerateVersion)
  {
    post(new ImportTable(tableReferences, isGenerateVersion));
  }

  public static void selectConnection(String connectionName)
  {
    post(new SelectConnection(connectionName));
  }

  public static void selectTable(ModelData modelData)
  {
    post(new SelectTable(modelData));
  }

  public static void setStatusBusy(String statusMessage)
  {
    post(new SetStatusBusy(statusMessage));
  }

  public static class AddDescribeTabItem extends ServiceRequest
  {
    private List<ColumnDefinition> columnDefinitions;
    private ModelData modelData;

    public AddDescribeTabItem(ModelData modelData, List<ColumnDefinition> columnDefinitions)
    {
      this.modelData = modelData;
      this.columnDefinitions = columnDefinitions;
    }

    public List<ColumnDefinition> getColumnDefinitions()
    {
      return columnDefinitions;
    }

    public ModelData getModelData()
    {
      return modelData;
    }
  }

  public static class AddDisplayTabItem extends ServiceRequest
  {
    private ModelData modelData;
    private QueryResult queryResult;

    public AddDisplayTabItem(ModelData modelData, QueryResult queryResult)
    {
      this.modelData = modelData;
      this.queryResult = queryResult;
    }

    public ModelData getModelData()
    {
      return modelData;
    }

    public QueryResult getQueryResult()
    {
      return queryResult;
    }
  }

  public static class AddTablesToQueryBuilder extends ServiceRequest
  {
    private List<ModelData> tables;

    public AddTablesToQueryBuilder(List<ModelData> tables)
    {
      this.tables = tables;
    }

    public List<ModelData> getTables()
    {
      return tables;
    }
  }

  public static class ClearStatus extends ServiceRequest
  {
  }

  public static class DeleteConnections extends ServiceRequest
  {
    private List<String> connectionNames;

    public DeleteConnections(List<String> connectionNames)
    {
      this.connectionNames = connectionNames;
    }

    public List<String> getConnectionNames()
    {
      return connectionNames;
    }
  }

  public static class DeleteTables extends ServiceRequest
  {
    private List<Integer> tableIds;

    public DeleteTables(List<Integer> tableIds)
    {
      this.tableIds = tableIds;
    }

    public List<Integer> getTableIds()
    {
      return tableIds;
    }
  }

  public static class DescribeTable extends ServiceRequest
  {
    private ModelData selectedItem;
    private TableReference tableReference;

    public DescribeTable(ModelData selectedItem, TableReference tableReference)
    {
      this.selectedItem = selectedItem;
      this.tableReference = tableReference;
    }

    public ModelData getSelectedItem()
    {
      return selectedItem;
    }

    public TableReference getTableReference()
    {
      return tableReference;
    }
  }

  public static class DisplayConnectionWindow extends ServiceRequest
  {
  }

  public static class DisplayTable extends ServiceRequest
  {
    private ModelData selectedItem;
    private TableReference tableReference;

    public DisplayTable(ModelData selectedItem, TableReference tableReference)
    {
      this.selectedItem = selectedItem;
      this.tableReference = tableReference;
    }

    public ModelData getSelectedItem()
    {
      return selectedItem;
    }

    public TableReference getTableReference()
    {
      return tableReference;
    }
  }

  public static class GetMasterTree extends ServiceRequest
  {
  }

  public static class GetMasterTreeResults extends ServiceRequest
  {
    private TreeModel masterTree;

    public GetMasterTreeResults(TreeModel masterTree)
    {
      this.masterTree = masterTree;
    }

    public TreeModel find(TreeModel parentTreeModel, String name)
    {
      int childCount = parentTreeModel.getChildCount();
      for (int childOffset = 0; childOffset < childCount; childOffset++)
      {
        TreeModel childTreeModel = (TreeModel)parentTreeModel.getChild(childOffset);
        if (childTreeModel.get(Keys.NAME).equals(name))
        {
          return childTreeModel;
        }
      }
      return null;
    }

    public TreeModel getConnectionTree()
    {
      return find(masterTree, Keys.CONNECTIONS);
    }

    public TreeModel getMasterTree()
    {
      return masterTree;
    }

    public Integer getTableId()
    {
      return getTransientProperty(masterTree, Keys.NEW_TABLE_ID);
    }

    public <X> X getTransientProperty(TreeModel masterTree, String name)
    {
      X value = masterTree.get(name);
      if (value != null)
      {
        masterTree.remove(name);
      }
      return value;
    }
  }

  public static class GetPage extends ServiceRequest
  {
    private AsyncCallback<PageResult> callback;
    private PagingLoadConfig loadConfig;
    private QueryDescriptor queryDescriptor;

    public GetPage(QueryDescriptor queryDescriptor, PagingLoadConfig loadConfig, AsyncCallback<PageResult> callback)
    {
      this.queryDescriptor = queryDescriptor;
      this.loadConfig = loadConfig;
      this.callback = callback;
    }

    public AsyncCallback<PageResult> getCallback()
    {
      return callback;
    }

    public PagingLoadConfig getLoadConfig()
    {
      return loadConfig;
    }

    public QueryDescriptor getQueryDescriptor()
    {
      return queryDescriptor;
    }
  }

  public static class ImportTable extends ServiceRequest
  {
    private boolean isGenerateVersion;
    private List<TableReference> tableReferences;

    public ImportTable(List<TableReference> tableReferences, boolean isGenerateVersion)
    {
      this.tableReferences = tableReferences;
      this.isGenerateVersion = isGenerateVersion;
    }

    public List<TableReference> getTableReferences()
    {
      return tableReferences;
    }

    public boolean isGenerateVersion()
    {
      return isGenerateVersion;
    }
  }

  public static class SelectConnection extends ServiceRequest
  {
    private String connectionName;

    public SelectConnection(String connectionName)
    {
      this.connectionName = connectionName;
    }

    public String getConnectionName()
    {
      return connectionName;
    }
  }

  public static class SelectTable extends ServiceRequest
  {
    private ModelData modelData;

    public SelectTable(ModelData modelData)
    {
      this.modelData = modelData;
    }

    public ModelData getModelData()
    {
      return modelData;
    }
  }

  public static class SetStatusBusy extends ServiceRequest
  {
    private String statusMessage;

    public SetStatusBusy(String statusMessage)
    {
      this.statusMessage = statusMessage;
    }

    public String getStatusMessage()
    {
      return statusMessage;
    }
  }
}
