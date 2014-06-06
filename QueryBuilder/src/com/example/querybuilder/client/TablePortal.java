// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.example.querybuilder.client.TablePortlet.TablePortletDoubleClickEvent;
import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.Constants;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.NodeType;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TablePortal extends Portal
{
  private static final int COLUMN_COUNT = 5;

  private String connectionName;
  private EmptyText emptyText;
  private QueryBuilderTabItem queryBuilderTabItem;
  private HashSet<String> tableAliases = new HashSet<String>();
  private HashSet<TablePortlet> tablePortlets = new HashSet<TablePortlet>();

  public TablePortal(QueryBuilderTabItem queryBuilderTabItem)
  {
    super(COLUMN_COUNT);

    this.queryBuilderTabItem = queryBuilderTabItem;

    setBorders(false);
    setStyleAttribute("backgroundColor", "white");

    double columnWidth = 1d / COLUMN_COUNT;
    for (int columnOffset = 0; columnOffset < COLUMN_COUNT; columnOffset++)
    {
      setColumnWidth(columnOffset, columnWidth);
    }

    new TablePortalDropTarget(this);

    emptyText = new EmptyText("<b>Drag Tables</b> from tree to build <b>New Query</b> here.");
    showEmptyText();
  }

  public void addTable(final TableReference tableReference)
  {
    String connectionName = getConnectionName(tableReference);
    if (tablePortlets.size() == 0)
    {
      hideEmptyText();
      this.connectionName = connectionName;
    }
    else
    {
      if (!this.connectionName.equals(connectionName))
      {
        MessageBox.info("Multiple Database Connections", "All the tables in a query must either be from the same database connection, or they must be imported.", null);
        return;
      }
    }

    QueryBuilderServiceBus.setStatusBusy("Getting Column Definitions...");

    // Do *NOT* factor this out into something that is far more complex and inefficient!
    QueryBuilder.queryBuilderService.getColumnDefinitions(tableReference, new ClearStatusCallback<List<ColumnDefinition>>()
    {
      @Override
      protected void process(List<ColumnDefinition> columnDefinitions)
      {
        addTablePortletResponse(tableReference, columnDefinitions);
      }
    });
  }

  private void addTablePortletResponse(TableReference tableReference, List<ColumnDefinition> columnDefinitions)
  {
    tableReference.setTableAlias(getTableAlias(tableReference.getTableName()));
    int bestColumnOffset = getBestColumnOffset();
    TablePortlet tablePortlet = new TablePortlet(tableReference, columnDefinitions);
    tablePortlet.addListener(TablePortlet.TablePortletClose, new Listener<BaseEvent>()
    {
      @Override
      public void handleEvent(BaseEvent be)
      {
        onTablePortletClose((TablePortlet)be.getSource());
      }
    });
    tablePortlet.addListener(TablePortlet.TablePortletDoubleClick, new Listener<TablePortletDoubleClickEvent>()
    {
      @Override
      public void handleEvent(TablePortletDoubleClickEvent tablePortletDoubleClickEvent)
      {
        List<ModelData> columns = tablePortletDoubleClickEvent.getColumns();
        queryBuilderTabItem.addColumns(columns);
      }
    });
    add(tablePortlet, bestColumnOffset);
    tablePortlets.add(tablePortlet);
    queryBuilderTabItem.onAddTablePortlet();
  }

  public void addTables(List<ModelData> wrappedTableReferences)
  {
    for (ModelData wrappedTableReference : wrappedTableReferences)
    {
      TableReference tableReference = wrappedTableReference.get(Keys.TABLE_REFERENCE);
      addTable(tableReference);
    }
  }

  public int getBestColumnOffset()
  {
    int bestColumnOffset = 0;
    int minimumItemCount = Integer.MAX_VALUE;

    for (int columnOffset = 0; columnOffset < COLUMN_COUNT; columnOffset++)
    {
      int itemCount = getItem(columnOffset).getItemCount();
      if (itemCount < minimumItemCount)
      {
        minimumItemCount = itemCount;
        bestColumnOffset = columnOffset;
      }
    }

    return bestColumnOffset;
  }

  private String getConnectionName(TableReference tableReference)
  {
    String connectionName;
    if (tableReference.isInternal())
    {
      connectionName = "";
    }
    else
    {
      connectionName = tableReference.getConnectionName();
    }
    return connectionName;
  }

  public int getPortletCount()
  {
    return tablePortlets.size();
  }

  private String getTableAlias(String name)
  {
    String firstLetter = name.substring(0, 1).toUpperCase();
    for (int number = 1; number < Integer.MAX_VALUE; number++)
    {
      String alias = firstLetter + Constants.NUMBER_DELIMITER + number;
      if (!tableAliases.contains(alias))
      {
        tableAliases.add(alias);
        return alias;
      }
    }
    throw new RuntimeException("Cannot generate available");
  }

  public HashSet<String> getTableAliases()
  {
    return tableAliases;
  }

  public List<TableReference> getTableReferences()
  {
    List<TableReference> tableReferences = new LinkedList<TableReference>();
    for (TablePortlet tablePortlet : tablePortlets)
    {
      TableReference tableReference = tablePortlet.getTableReference();
      tableReferences.add(tableReference);
    }
    return tableReferences;
  }

  public void hideEmptyText()
  {
    remove(emptyText);
    doLayout();
  }

  public void onTablePortletClose(TablePortlet tablePortlet)
  {
    String tableAlias = tablePortlet.getTableReference().getTableAlias();
    removeAlias(tableAlias);
    tablePortlets.remove(tablePortlet);
    if (tablePortlets.size() == 0)
    {
      showEmptyText();
    }
    queryBuilderTabItem.onRemoveTablePortlet(tableAlias);
  }

  public void removeAlias(String alias)
  {
    tableAliases.remove(alias);
  }

  public void showEmptyText()
  {
    insert(emptyText, 0);
    doLayout();
  }

  public class EmptyText extends LayoutContainer
  {
    public EmptyText(String emptyText)
    {
      super(new FitLayout());
      Html html = new Html("<div class='x-grid-empty'>" + emptyText + "</div>");
      add(html);
    }
  }

  public class TablePortalDropTarget extends DropTarget
  {
    public TablePortalDropTarget(TablePortal tablePortal)
    {
      super(tablePortal);
    }

    @Override
    protected void onDragDrop(DNDEvent e)
    {
      super.onDragDrop(e);
      Object data = e.getData();
      boolean convertTreeStoreModel = true;
      List<ModelData> wrappedTableReferences = prepareDropData(data, convertTreeStoreModel);
      addTables(wrappedTableReferences);
    }

    @Override
    protected void onDragEnter(DNDEvent e)
    {
      super.onDragEnter(e);
      Iterable<Object> data = e.getData();
      for (Object dragItem : data)
      {
        ModelData modelData = (ModelData)dragItem;
        TableReference tableReference = modelData.get(Keys.TABLE_REFERENCE);
        if (tableReference != null)
        {
          NodeType nodeType = tableReference.getNodeType();
          if (nodeType == NodeType.EXTERNAL_TABLE || nodeType == NodeType.EXTERNAL_VIEW)
          {
            e.getStatus().setStatus(true);
            return;
          }
        }
      }
      e.getStatus().setStatus(false);
    }

  }
}
