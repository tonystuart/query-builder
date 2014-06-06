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
import com.example.querybuilder.shared.NodeType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DescribeTabItem extends TabItem
{
  private final ModelData modelData;

  public DescribeTabItem(ModelData modelData, List<ColumnDefinition> columnDefinitions, String title)
  {
    this.modelData = modelData;
    
    setText(title);
    setIcon(getIcon());
    setBorders(true);
    setStyleAttribute("borderTop", "none");
    setClosable(true);
    setLayout(new FitLayout());
    final DescribePanel describePanel = new DescribePanel();
    add(describePanel);
    addListener(Events.Select, new SelectListener());
    DeferredCommand.addCommand(new DeferredDisplay(describePanel, columnDefinitions));
  }

  public AbstractImagePrototype getIcon()
  {
    AbstractImagePrototype icon = null;
    NodeType nodeType = modelData.get(Keys.NODE_TYPE);
    switch (nodeType)
    {
      case EXTERNAL_TABLE:
        icon = Resources.APPLICATION;
        break;
      case EXTERNAL_VIEW:
        icon = Resources.APPLICATION_LINK;
        break;
      case INTERNAL_TABLE:
        icon = Resources.APPLICATION_GO;
        break;
    }
    return icon;
  }

  /**
   * Give panel a chance to render itself
   */
  private final class DeferredDisplay implements Command
  {
    private final DescribePanel describePanel;
    private final List<ColumnDefinition> columnDefinitions;

    private DeferredDisplay(DescribePanel describePanel, List<ColumnDefinition> columnDefinitions)
    {
      this.describePanel = describePanel;
      this.columnDefinitions = columnDefinitions;
    }

    @Override
    public void execute()
    {
      describePanel.display(columnDefinitions);
    }
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
