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
import com.example.querybuilder.shared.QueryResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;

public class ResultsTabPanel extends ResizableTabPanel
{
  private int itemCount;

  public ResultsTabPanel()
  {
    setBodyBorder(false);
    setBorders(false);
    setCloseContextMenu(true);
    setTabScroll(true);
    addListener(Events.Remove, new RemoveListener());
  }

  public void addDescribeTabItem(ModelData modelData, List<ColumnDefinition> columnDefinitions)
  {
    itemCount++;
    String title = itemCount + "-" + modelData.get(Keys.NAME);
    DescribeTabItem describeTabItem = new DescribeTabItem(modelData, columnDefinitions, title);
    add(describeTabItem);
    setSelection(describeTabItem);
  }

  public void addDisplayTabItem(ModelData modelData, QueryResult queryResult)
  {
    itemCount++;
    String title = itemCount + "-" + modelData.get(Keys.NAME);
    DisplayTabItem displayTabItem = new DisplayTabItem(modelData, queryResult, title);
    add(displayTabItem);
    setSelection(displayTabItem);
  }

  public void addQueryBuilder()
  {
    itemCount++;
    String title = itemCount + "-Query Builder";
    QueryBuilderTabItem queryTabItem = new QueryBuilderTabItem(title);
    add(queryTabItem);
    setSelection(queryTabItem);
  }

  private final class RemoveListener implements Listener<TabPanelEvent>
  {
    @Override
    public void handleEvent(TabPanelEvent be)
    {
      if (getItemCount() == 0)
      {
        itemCount = 0;
      }
    }
  }

}
