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
import com.extjs.gxt.ui.client.widget.TabPanel;

public class ResizableTabPanel extends TabPanel
{
  public ResizableTabPanel()
  {
    setMonitorWindowResize(true);
  }

  @Override
  protected void onResize(int width, int height)
  {
    super.onResize(width, height);
    int itemCount = getItemCount();
    for (int itemOffset = 0; itemOffset < itemCount; itemOffset++)
    {
      TabItem tabItem = getItem(itemOffset);
      tabItem.setSize(width, height);
      tabItem.layout(true);
    }
  }

}
