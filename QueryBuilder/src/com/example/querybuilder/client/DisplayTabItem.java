// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.NodeType;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DisplayTabItem extends TabItem
{
  private final ModelData modelData;

  public DisplayTabItem(ModelData modelData, QueryResult queryResult, String title)
  {
    this.modelData = modelData;
    
    setText(title);
    setIcon(getIcon());
    setBorders(true);
    setStyleAttribute("borderTop", "none");
    setClosable(true);
    setLayout(new FitLayout());
    DisplayPanel displayPanel = new DisplayPanel();
    displayPanel.setBorders(false); // TODO: DisplayPanel borders are used in query results, should probably set explicitly in that container just as we do here.
    add(displayPanel);
    addListener(Events.Select, new SelectListener());
    
    TableReference tableReference = TableTreeGridPanel.createTableReference(modelData);
    QueryDescriptor queryDescriptor = new QueryDescriptor(tableReference);
    displayPanel.display(queryDescriptor, queryResult);
  }

  public AbstractImagePrototype getIcon()
  {
    AbstractImagePrototype icon = null;
    NodeType nodeType = modelData.get(Keys.NODE_TYPE);
    switch (nodeType)
    {
      case EXTERNAL_TABLE:
        icon = Resources.TABLE;
        break;
      case EXTERNAL_VIEW:
        icon = Resources.TABLE_LINK;
        break;
      case INTERNAL_TABLE:
        icon = Resources.TABLE_GO;
        break;
    }
    return icon;
  }

  private final class SelectListener implements Listener<TabPanelEvent>
  {
    @Override
    public void handleEvent(TabPanelEvent be)
    {
      QueryBuilderServiceBus.selectTable(modelData);
    }
  }
}
