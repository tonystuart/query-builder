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

import com.example.querybuilder.client.QueryBuilderServiceBus.DeleteConnections;
import com.example.querybuilder.client.QueryBuilderServiceBus.DeleteTables;
import com.example.querybuilder.client.QueryBuilderServiceBus.DescribeTable;
import com.example.querybuilder.client.QueryBuilderServiceBus.DisplayTable;
import com.example.querybuilder.client.QueryBuilderServiceBus.GetMasterTree;
import com.example.querybuilder.client.QueryBuilderServiceBus.GetPage;
import com.example.querybuilder.client.QueryBuilderServiceBus.ImportTable;
import com.example.querybuilder.client.ServiceBus.ServiceProvider;
import com.example.querybuilder.client.ServiceBus.ServiceRequest;
import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class QueryBuilder implements EntryPoint
{
  public static QueryBuilderServiceAsync queryBuilderService = GWT.create(QueryBuilderService.class);

  public void onModuleLoad()
  {
    GWT.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());

    Viewport viewport = new Viewport();
    viewport.setLayout(new FitLayout());
    viewport.add(new MainPanel());

    RootPanel.get().add(viewport);

    QueryBuilderServiceBus.addServiceProvider(new QueryBuilderServiceProvider());
    queryBuilderService.getMasterTree(new MasterTreeCallback());
  }

  public void setStatusBusy(String message)
  {
    QueryBuilderServiceBus.setStatusBusy(message);
  }

  public class QueryBuilderServiceProvider implements ServiceProvider
  {
    @Override
    public void onServiceRequest(ServiceRequest serviceRequest)
    {
      if (serviceRequest instanceof GetPage)
      {
        GetPage getPage = (GetPage)serviceRequest;
        QueryDescriptor queryDescriptor = getPage.getQueryDescriptor();
        PagingLoadConfig loadConfig = getPage.getLoadConfig();
        AsyncCallback<PageResult> callback = getPage.getCallback();
        setStatusBusy("Requesting Page...");
        queryBuilderService.getPage(queryDescriptor, loadConfig, new PageCallback(callback));
      }
      else if (serviceRequest instanceof ImportTable)
      {
        ImportTable importTable = (ImportTable)serviceRequest;
        List<TableReference> tableReferences = importTable.getTableReferences();
        boolean isGenerateVersion = importTable.isGenerateVersion();
        setStatusBusy("Importing...");
        queryBuilderService.importTables(tableReferences, isGenerateVersion, new MasterTreeCallback());
      }
      else if (serviceRequest instanceof DescribeTable)
      {
        DescribeTable describeTable = (DescribeTable)serviceRequest;
        ModelData selectedItem = describeTable.getSelectedItem();
        TableReference tableReference = describeTable.getTableReference();
        setStatusBusy("Describing..");
        queryBuilderService.getColumnDefinitions(tableReference, new DescribeTableCallback(selectedItem));
      }
      else if (serviceRequest instanceof DisplayTable)
      {
        DisplayTable displayTable = (DisplayTable)serviceRequest;
        ModelData selectedItem = displayTable.getSelectedItem();
        TableReference tableReference = displayTable.getTableReference();
        QueryDescriptor queryDescriptor = new QueryDescriptor(tableReference);
        BasePagingLoadConfig pagingLoadConfig = new BasePagingLoadConfig(0, 50);
        setStatusBusy("Displaying..");
        queryBuilderService.runQuery(queryDescriptor, pagingLoadConfig, new DisplayTableCallback(selectedItem));
      }
      else if (serviceRequest instanceof DeleteConnections)
      {
        DeleteConnections deleteConnections = (DeleteConnections)serviceRequest;
        List<String> connectionNames = deleteConnections.getConnectionNames();
        setStatusBusy("Deleting...");
        queryBuilderService.deleteConnections(connectionNames, new MasterTreeCallback());
      }
      else if (serviceRequest instanceof DeleteTables)
      {
        DeleteTables deleteTables = (DeleteTables)serviceRequest;
        List<Integer> tableIds = deleteTables.getTableIds();
        setStatusBusy("Deleting...");
        queryBuilderService.deleteTables(tableIds, new MasterTreeCallback());
      }
      else if (serviceRequest instanceof GetMasterTree)
      {
        queryBuilderService.getMasterTree(new MasterTreeCallback());
      }
    }

  }

  public class DescribeTableCallback extends ClearStatusCallback<List<ColumnDefinition>>
  {
    private ModelData modelData;

    public DescribeTableCallback(ModelData modelData)
    {
      this.modelData = modelData;
    }

    @Override
    protected void process(List<ColumnDefinition> columnDefinitions)
    {
      QueryBuilderServiceBus.addDescribeTabItem(modelData, columnDefinitions);
    }
  }

  public class DisplayTableCallback extends ClearStatusCallback<QueryResult>
  {
    private ModelData modelData;

    public DisplayTableCallback(ModelData modelData)
    {
      this.modelData = modelData;
    }

    @Override
    protected void process(QueryResult tableDescriptor)
    {
      QueryBuilderServiceBus.addDisplayTabItem(modelData, tableDescriptor);
    }
  }

  public class MasterTreeCallback extends ClearStatusCallback<TreeModel>
  {
    @Override
    public void process(TreeModel masterTree)
    {
      QueryBuilderServiceBus.getMasterTreeResults(masterTree);
    }
  }

  public class PageCallback extends ClearStatusCallback<PageResult>
  {
    private AsyncCallback<PageResult> callback;

    public PageCallback(AsyncCallback<PageResult> callback)
    {
      this.callback = callback;
    }

    @Override
    public void onFailure(Throwable caught)
    {
      super.onFailure(caught);
      callback.onFailure(caught);
    }

    @Override
    protected void process(PageResult pageResult)
    {
      callback.onSuccess(pageResult);
    }
  }

}
