// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class SourceTabPanel extends ResizableTabPanel
{
  public SourceTabPanel()
  {
    setBodyBorder(false);
    setBorders(false);

    TableTreeGridPanel tableTreeGridPanel = new TableTreeGridPanel();
    tableTreeGridPanel.setHeaderVisible(false);
    TabItem tabItem = new TabItem("Tables");
    tabItem.setIcon(Resources.FOLDER_TABLE);
    tabItem.setLayout(new FitLayout());
    tabItem.add(tableTreeGridPanel);
    add(tabItem);
    setSelection(tabItem);

    tabItem = new TabItem("Queries");
    tabItem.setIcon(Resources.FOLDER_DATABASE);
    add(tabItem);
    
    tabItem = new TabItem("Snapshots");
    tabItem.setIcon(Resources.FOLDER_IMAGE);
    add(tabItem);
    
    tabItem = new TabItem("History");
    tabItem.setIcon(Resources.FOLDER_BRICK);
    add(tabItem);
  }
}
