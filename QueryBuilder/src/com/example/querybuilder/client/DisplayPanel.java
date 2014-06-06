// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.LinkedList;
import java.util.List;

import com.example.querybuilder.shared.ArrayModelData;
import com.example.querybuilder.shared.ColumnDescriptor;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.SqlColumnTypes;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DisplayPanel extends ContentPanel
{
  private static final int MAX_CHARACTERS = 20;
  private static final int MAX_COLUMN_WIDTH_CHECK_ROWS = 10;
  private static final int MIN_COLUMN_WIDTH = 25; // Same as grid.getMinColumnWidth(), but we need it before we create the grid
  private static final int PAGE_SIZE = 50;
  private static final int PIXEL_WIDTH_PER_CHARACTER = 10;

  private Grid<ModelData> grid;
  private BasePagingLoadConfig loadConfig;
  private BasePagingLoader<PageResult> loader;
  private QueryDescriptor queryDescriptor;
  private PreloadablePagingToolBar toolBar;
  private List<ColumnDescriptor> columnDescriptors;

  public DisplayPanel()
  {
    setBorders(true);
    setBodyBorder(false);
    setHeaderVisible(false);
    setLayout(new FitLayout());

    RpcProxy<PageResult> proxy = new PageResultRpcProxy();

    loader = new BasePagingLoader<PageResult>(proxy);
    loadConfig = new BasePagingLoadConfig(0, PAGE_SIZE);
    loader.useLoadConfig(loadConfig);
    loader.setRemoteSort(true);

    toolBar = new PreloadablePagingToolBar(PAGE_SIZE);
    toolBar.bind(loader);

    setBottomComponent(toolBar);
  }

  public void display(QueryDescriptor queryDescriptor, QueryResult queryResult)
  {
    this.queryDescriptor = queryDescriptor;
    this.columnDescriptors = queryResult.getColumnDescriptors();

    PageResult pageResult = queryResult.getPageResult();
    List<ModelData> data = pageResult.getData();

    ListStore<ModelData> listStore = new ListStore<ModelData>(loader);
    listStore.add(data);
    toolBar.onLoad(new LoadEvent(loader, loadConfig, pageResult));

    ColumnModel columnModel = getColumnModel(data);

    if (grid == null)
    {
      grid = new Grid<ModelData>(listStore, columnModel);
      grid.setLoadMask(true);
      grid.setBorders(false);
      grid.addListener(Events.CellDoubleClick, new CellDoubleClickGridListener());
      add(grid);
      if (isRendered())
      {
        doLayout();
      }
    }
    else
    {
      grid.reconfigure(listStore, columnModel);
    }
  }

  private class HeaderGroup
  {
    private int rowOffset;
    private int columnOffset;
    private HeaderGroupConfig headerGroupConfig;
    public HeaderGroup(int rowOffset, int columnOffset, HeaderGroupConfig headerGroupConfig)
    {
      this.rowOffset = rowOffset;
      this.columnOffset = columnOffset;
      this.headerGroupConfig = headerGroupConfig;
    }
    public int getRowOffset()
    {
      return rowOffset;
    }
    public int getColumnOffset()
    {
      return columnOffset;
    }
    public HeaderGroupConfig getHeaderGroupConfig()
    {
      return headerGroupConfig;
    }
  }
  
  private ColumnModel getColumnModel(List<ModelData> data)
  {
    int columnOffset = 0;
    HeaderGroupConfig headerGroupConfig = null;
    List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();
    List<HeaderGroup> headerGroups = new LinkedList<DisplayPanel.HeaderGroup>();
    for (ColumnDescriptor columnDescriptor : columnDescriptors)
    {
      String id = Integer.toString(columnOffset);
      String columnName = columnDescriptor.getColumnName();
      int delimiter = columnName.indexOf('|');
      if (delimiter != -1)
      {
        String groupName = columnName.substring(0, delimiter);
        columnName = columnName.substring(delimiter + 1);
        if (headerGroupConfig == null || !headerGroupConfig.getHtml().equals(groupName))
        {
          headerGroupConfig = new HeaderGroupConfig(groupName, 1, 1);
          headerGroups.add(new HeaderGroup(0, columnOffset, headerGroupConfig));
        }
        else
        {
          headerGroupConfig.setColspan(headerGroupConfig.getColspan() + 1);
        }
      }
      ColumnConfig columnConfig = new ColumnConfig();
      columnConfig.setId(id);
      columnConfig.setHeader(columnName);
      int columnType = columnDescriptor.getDataType();
      if (SqlColumnTypes.isNumber(columnType))
      {
        columnConfig.setAlignment(HorizontalAlignment.RIGHT);
      }
      else if (SqlColumnTypes.isDate(columnType))
      {
        columnConfig.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
      }
      columnConfigs.add(columnConfig);
      columnOffset++;
    }
    ColumnModel columnModel = new ColumnModel(columnConfigs);
    initializeColumnWidths(columnConfigs, data);
    for (HeaderGroup headerGroup : headerGroups)
    {
      columnModel.addHeaderGroup(headerGroup.getRowOffset(), headerGroup.getColumnOffset(), headerGroup.getHeaderGroupConfig());

    }
    return columnModel;
  }

  private void displayDetails(GridEvent<ModelData> be)
  {
    int rowOffset = be.getRowIndex();
    displayDetails(rowOffset);
  }

  private void displayDetails(int rowOffset)
  {
    ModelData selectedItem = grid.getSelectionModel().getSelectedItem();
    if (selectedItem != null)
    {
      int rowNumber = (toolBar.getActivePage() - 1) * toolBar.getPageSize() + rowOffset + 1;
      String heading = "Details for Row " + rowNumber;
      new DetailsWindow(heading, columnDescriptors, (ArrayModelData)selectedItem);
    }
  }

  private void initializeColumnWidths(List<ColumnConfig> columnConfigs, List<ModelData> data)
  {
    for (ColumnConfig columnConfig : columnConfigs)
    {
      String header = columnConfig.getHeader();
      int width = header.length();
      columnConfig.setWidth(width);
    }
    for (ModelData modelData : new MaximumItemIterable<ModelData>(data, MAX_COLUMN_WIDTH_CHECK_ROWS))
    {
      for (ColumnConfig columnConfig : columnConfigs)
      {
        String id = columnConfig.getId();
        Object value = modelData.get(id);
        if (value != null)
        {
          int width = value.toString().length();
          if (columnConfig.getWidth() < width)
          {
            columnConfig.setWidth(width);
          }
        }
      }
    }
    for (ColumnConfig columnConfig : columnConfigs)
    {
      int width = columnConfig.getWidth();
      if (width > MAX_CHARACTERS)
      {
        width = MAX_CHARACTERS;
      }
      width *= PIXEL_WIDTH_PER_CHARACTER;
      if (width < MIN_COLUMN_WIDTH)
      {
        width = MIN_COLUMN_WIDTH;
      }
      columnConfig.setWidth(width);
    }
  }

  private final class CellDoubleClickGridListener implements Listener<GridEvent<ModelData>>
  {
    @Override
    public void handleEvent(GridEvent<ModelData> be)
    {
      displayDetails(be);
    }

  }

  private final class PageResultRpcProxy extends RpcProxy<PageResult>
  {
    @Override
    public void load(Object loadConfig, final AsyncCallback<PageResult> callback)
    {
      // NB: loadConfig is the BasePagingLoadConfig created by constructor (or alternatively for BaseLoader.load(loadConfig)) and reused by PagingToolBar when isReuseConfig() is true
      QueryBuilderServiceBus.getPage(queryDescriptor, (PagingLoadConfig)loadConfig, callback);
    }
  }

  public class PreloadablePagingToolBar extends PagingToolBar
  {
    public PreloadablePagingToolBar(int pageSize)
    {
      super(pageSize);
    }

    @Override
    public void onLoad(LoadEvent event)
    {
      super.onLoad(event);
    }
  }

}
