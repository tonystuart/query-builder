// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.dom.client.Element;

public class ExtendedTreeGrid extends TreeGrid<ModelData>
{

  public ExtendedTreeGrid(MergeTreeStore treeStore, ColumnModel columnModel)
  {
    super(treeStore, columnModel);
  }

  public void scrollIntoView(String property, Object value, boolean isDeep, boolean isSelect, boolean isKeepExisting)
  {
    if (value != null)
    {
      ModelData modelData = ((MergeTreeStore)treeStore).find(property, value, isDeep);
      if (modelData != null)
      {
        scrollIntoView(modelData, isSelect, isKeepExisting);
      }
    }
  }

  public void scrollIntoView(ModelData modelData, boolean isSelect, boolean isKeepExisting)
  {
    if (isSelect)
    {
      getSelectionModel().select(isKeepExisting, modelData);
    }
    GridView gridView = getView();
    Element element = gridView.getRow(modelData);
    int row = gridView.findRowIndex(element);
    gridView.ensureVisible(row, 0, true);
  }

}
