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

import com.example.querybuilder.client.QueryBuilderServiceBus.AddDescribeTabItem;
import com.example.querybuilder.client.QueryBuilderServiceBus.AddDisplayTabItem;
import com.example.querybuilder.client.QueryBuilderServiceBus.ClearStatus;
import com.example.querybuilder.client.QueryBuilderServiceBus.SetStatusBusy;
import com.example.querybuilder.client.ServiceBus.ServiceProvider;
import com.example.querybuilder.client.ServiceBus.ServiceRequest;
import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.QueryResult;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class MainPanel extends ContentPanel implements CommandProcessor
{
  private static final String COMMAND_CREATE_CONNECTION = "Create Connection";
  private static final String COMMAND_CREATE_QUERY = "Create Query";
  private static final String COMMAND_UPDATE_SETTINGS = "Change Settings";

  private CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
  private ResultsTabPanel resultsTabPanel;
  private SourceTabPanel sourceTabPanel;
  private Status status;

  public class MainPanelServiceProvider implements ServiceProvider
  {

    @Override
    public void onServiceRequest(ServiceRequest serviceRequest)
    {
      if (serviceRequest instanceof ClearStatus)
      {
        setStatusClear();
      }
      else if (serviceRequest instanceof SetStatusBusy)
      {
        SetStatusBusy setStatusBusy = (SetStatusBusy)serviceRequest;
        String statusMessage = setStatusBusy.getStatusMessage();
        setStatusBusy(statusMessage);
      }
      else if (serviceRequest instanceof AddDescribeTabItem)
      {
        AddDescribeTabItem addDescribeTabItem = (AddDescribeTabItem)serviceRequest;
        ModelData modelData = addDescribeTabItem.getModelData();
        List<ColumnDefinition> columnDefinitions = addDescribeTabItem.getColumnDefinitions();
        addDescribeTabItem(modelData, columnDefinitions);
      }
      else if (serviceRequest instanceof AddDisplayTabItem)
      {
        AddDisplayTabItem addDisplayTabItem = (AddDisplayTabItem)serviceRequest;
        ModelData modelData = addDisplayTabItem.getModelData();
        QueryResult queryResult = addDisplayTabItem.getQueryResult();
        addDisplayTabItem(modelData, queryResult);
      }
    }

  }

  public MainPanel()
  {
    setHeaderVisible(false);
    //setStyleAttribute("margin", "20px");
    setLayout(new BorderLayout());
    addToolBar();
    addSourceTabPanel();
    addResultsTabPanel();
    addStatusBar();
    resultsTabPanel.addQueryBuilder();
    new ConnectionWindow();
    QueryBuilderServiceBus.addServiceProvider(new MainPanelServiceProvider());
  }

  public void addDescribeTabItem(ModelData modelData, List<ColumnDefinition> columnDefinitions)
  {
    resultsTabPanel.addDescribeTabItem(modelData, columnDefinitions);
  }

  public void addDisplayTabItem(ModelData modelData, QueryResult queryResult)
  {
    resultsTabPanel.addDisplayTabItem(modelData, queryResult);
  }

  public void addQueryBuilder()
  {
    resultsTabPanel.addQueryBuilder();
  }

  private void addResultsTabPanel()
  {
    resultsTabPanel = new ResultsTabPanel();
    BorderLayoutData layoutData = new BorderLayoutData(LayoutRegion.CENTER);
    layoutData.setMargins(new Margins(5, 5, 5, 0));
    add(resultsTabPanel, layoutData);
  }

  private void addSourceTabPanel()
  {
    sourceTabPanel = new SourceTabPanel();
    BorderLayoutData layoutData = new BorderLayoutData(LayoutRegion.WEST, .33f);
    layoutData.setSplit(true);
    layoutData.setMargins(new Margins(5));
    add(sourceTabPanel, layoutData);
  }

  private void addStatusBar()
  {
    ToolBar statusToolBar = new ToolBar();
    status = new Status();
    statusToolBar.add(status);
    statusToolBar.setHeight(27);
    setBottomComponent(statusToolBar);
  }

  private void addToolBar()
  {
    ToolBar toolBar = new ToolBar();

    Button createButton = new Button(COMMAND_CREATE_CONNECTION, commandListener);
    createButton.setToolTip("Create a new database connection");
    toolBar.add(createButton);

    Button queryButton = new Button(COMMAND_CREATE_QUERY, commandListener);
    queryButton.setToolTip("Open a new query builder tab");
    toolBar.add(queryButton);

    Button settingsButton = new Button(COMMAND_UPDATE_SETTINGS, commandListener);
    settingsButton.setToolTip("Modify settings and properties");
    toolBar.add(settingsButton);

    setTopComponent(toolBar);
  }

  @Override
  public void onCommand(String command, AbstractImagePrototype icon)
  {
    if (command.equals(COMMAND_CREATE_CONNECTION))
    {
      QueryBuilderServiceBus.displayConnectionWindow();
    }
    else if (command.equals(COMMAND_CREATE_QUERY))
    {
      addQueryBuilder(); // TODO: Consider whether to post this and let ResultsTabPanel handle callback
    }
  }

  public void setStatusBusy(String message)
  {
    status.setBusy(message);
  }

  public void setStatusClear()
  {
    status.clearStatus(null);
    setStatusInfo("Welcome to QueryBuilder - Copyright 2011 Anthony F. Stuart - All Rights Reserved.");
  }

  public void setStatusInfo(String message)
  {
    status.setStatus(message, null);
  }

}
