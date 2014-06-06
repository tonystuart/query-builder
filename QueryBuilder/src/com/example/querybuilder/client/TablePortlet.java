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

import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.NodeType;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class TablePortlet extends Portlet
{
  public static final EventType TablePortletClose = new EventType();
  public static final EventType TablePortletDoubleClick = new EventType();

  private Grid<ModelData> grid;
  private TableReference tableReference;

  public TablePortlet(TableReference tableReference, List<ColumnDefinition> columnDefinitions)
  {
    this.tableReference = tableReference;

    String connectionName = tableReference.getConnectionName();
    String schemaName = tableReference.getSchemaName();
    String tableName = tableReference.getTableName();
    String tableAlias = tableReference.getTableAlias();

    setIcon(NodeTypeIconProvider.getInstance().getIcon(tableReference.getNodeType()));
    setHeading(tableName + " (" + tableAlias + ")");
    setLayout(new FitLayout());
    setHeight(175);
    setCollapsible(false);
    setAnimCollapse(false);
    getHeader().addTool(new ToolButton("x-tool-close", new CloseListener()));

    StringBuilder s = new StringBuilder();
    s.append("<div style='text-align:center'>");
    s.append("<p>Connection Name</p>");
    s.append("<p><b>" + connectionName + "</b></p>");
    s.append("<p>Schema Name</p>");
    s.append("<p><b>" + schemaName + "</b></p>");
    s.append("<p>Table Name</p>");
    s.append("<p><b>" + tableName + "</b></p>");
    s.append("<p>Table Alias</p>");
    s.append("<p><b>" + tableAlias + "</b></p>");
    s.append("</div>");
    getHeader().setToolTip(s.toString());

    configureColumns(columnDefinitions);
  }

  private void addColumnsToColumnPanel()
  {
    List<ModelData> columns = getSelectedColumns();
    fireEvent(TablePortletDoubleClick, new TablePortletDoubleClickEvent(this, columns));
  }

  private void configureColumns(List<ColumnDefinition> columnDefinitions)
  {
    List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();
    ColumnConfig columnConfig = new ColumnConfig(Keys.NAME, 150);
    columnConfigs.add(columnConfig);
    ColumnModel columnModel = new ColumnModel(columnConfigs);
    ListStore<ModelData> listStore = new ListStore<ModelData>();
    listStore.add(columnDefinitions);
    grid = new Grid<ModelData>(listStore, columnModel);
    grid.setBorders(true);
    grid.setHideHeaders(true);
    grid.setAutoExpandColumn(Keys.NAME);
    grid.addListener(Events.OnDoubleClick, new GridDoubleClickListener());
    add(grid);
    new TablePortalDragSource(grid);
    doLayout();
  }

  public List<ModelData> getSelectedColumns()
  {
    String tableAlias = tableReference.getTableAlias();
    List<ModelData> dragList = new LinkedList<ModelData>();
    List<ModelData> selectedItems = grid.getSelectionModel().getSelectedItems();
    for (ModelData selectedItem : selectedItems)
    {
      String columnName = selectedItem.get(Keys.NAME);

      ModelData dragItem = new BaseModel();

      dragItem.set(Keys.COLUMN_ALIAS, columnName);
      dragItem.set(Keys.COLUMN_NAME, columnName);
      dragItem.set(Keys.TABLE_ALIAS, tableAlias);
      dragItem.set(Keys.NODE_TYPE, NodeType.COLUMN);

      dragList.add(dragItem);
    }

    return dragList;
  }

  public TableReference getTableReference()
  {
    return tableReference;
  }

  private void onClose()
  {
    removeFromParent();
    fireEvent(TablePortletClose, new BaseEvent(this));
  }

  private void onDoubleClick()
  {
    addColumnsToColumnPanel();
  }

  public static final class TablePortletDoubleClickEvent extends BaseEvent
  {
    private List<ModelData> columns;

    private TablePortletDoubleClickEvent(Object source, List<ModelData> columns)
    {
      super(source);
      this.columns = columns;
    }

    public List<ModelData> getColumns()
    {
      return columns;
    }
  }

  private final class CloseListener extends SelectionListener<IconButtonEvent>
  {
    @Override
    public void componentSelected(IconButtonEvent ce)
    {
      onClose();
    }
  }

  private final class GridDoubleClickListener implements Listener<GridEvent<ModelData>>
  {
    @Override
    public void handleEvent(GridEvent<ModelData> e)
    {
      onDoubleClick();
    }
  }

  private final class TablePortalDragSource extends GridDragSource
  {
    public TablePortalDragSource(Grid<ModelData> grid)
    {
      super(grid);
    }

    @Override
    protected void onDragDrop(DNDEvent event)
    {
      // Suppress invocation of base class, which handles move
    }

    @Override
    protected void onDragStart(DNDEvent e)
    {
      Element row = grid.getView().findRow(e.getTarget()).cast();
      if (row == null)
      {
        e.setCancelled(true);
        return;
      }

      List<ModelData> dragList = getSelectedColumns();
      int dragListSize = dragList.size();
      if (dragListSize == 0)
      {
        e.setCancelled(true);
        return;
      }

      e.setData(dragList);

      String statusText = getStatusText();
      if (statusText == null)
      {
        e.getStatus().update(GXT.MESSAGES.grid_ddText(dragListSize));
      }
      else
      {
        e.getStatus().update(Format.substitute(statusText, dragListSize));
      }

    }

  }

}
