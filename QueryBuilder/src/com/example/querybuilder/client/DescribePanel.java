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
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class DescribePanel extends LayoutContainer
{
  private Grid<ModelData> columnGrid;
  private ListStore<ModelData> columnListStore;

  public DescribePanel()
  {
    setLayout(new FitLayout());

    List<ColumnConfig> columnColumnConfigs = new LinkedList<ColumnConfig>();

    ColumnConfig columnConfig = new ColumnConfig(ColumnDefinition.TABLE_CATALOG, "Catalog", 100);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.COLUMN_NAME, "Column Name", 150);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.TYPE_NAME, "Type Name", 100);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.COLUMN_SIZE, "Column Size", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.DECIMAL_DIGITS, "Decimal Digits", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.CHARACTER_OCTET_LENGTH, "Octet Length", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.NULLABLE, "Nullable", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.AUTO_INCREMENT, "Auto Increment", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.DEFAULT_VALUE, "Default Value", 100);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.REMARKS, "Remarks", 100);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.DATA_TYPE, "Data Type", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    columnConfig = new ColumnConfig(ColumnDefinition.ORDINAL_POSITION, "Position", 100);
    columnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnColumnConfigs.add(columnConfig);

    // TODO: Add foreign key info
    
    ColumnModel columnColumnModel = new ColumnModel(columnColumnConfigs);
    columnListStore = new ListStore<ModelData>();
    columnGrid = new Grid<ModelData>(columnListStore, columnColumnModel);

    add(columnGrid);
  }

  public void display(List<ColumnDefinition> columnDefinitions)
  {
    int columnOffset = 0;
    boolean refreshHeader = false;
    ColumnModel columnModel = columnGrid.getColumnModel();
    for (ColumnConfig columnConfig : columnModel.getColumns())
    {
      String id = columnConfig.getId();
      boolean isEmpty = isEmpty(columnDefinitions, id);
      boolean isHidden = columnModel.isHidden(columnOffset);
      if (isEmpty != isHidden)
      {
        refreshHeader = true;
        columnModel.setHidden(columnOffset, isEmpty);
      }
      columnOffset++;
    }
    columnListStore.removeAll();
    columnListStore.add(columnDefinitions);
    if (refreshHeader)
    {
      columnGrid.getView().getHeader().updateColumnHidden(0, true);
    }
  }

  private boolean isEmpty(List<ColumnDefinition> columnDefinitions, String id)
  {
    for (ColumnDefinition columnDefinition : columnDefinitions)
    {
      Object object = columnDefinition.get(id);
      if (!(object == null || ((object instanceof String) && ((String)object).length() == 0)))
      {
        return false;
      }
    }
    return true;
  }


}
