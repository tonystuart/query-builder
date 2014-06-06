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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class DoubleClickTreePanel<M extends ModelData> extends TreePanel<M>
{
  private boolean isEnableDoubleClickExpandCollapse;

  public DoubleClickTreePanel(TreeStore<M> store)
  {
    super(store);
  }

  public boolean isEnableDoubleClickExpandCollapse()
  {
    return isEnableDoubleClickExpandCollapse;
  }

  @SuppressWarnings("rawtypes")
  protected void onDoubleClick(TreePanelEvent tpe)
  {
    if (isEnableDoubleClickExpandCollapse)
    {
      super.onDoubleClick(tpe);
    }
  }

  public void setEnableDoubleClickExpandCollapse(boolean isEnableDoubleClickExpandCollapse)
  {
    this.isEnableDoubleClickExpandCollapse = isEnableDoubleClickExpandCollapse;
  }

  public boolean expand(String key, Object value, boolean expand, boolean deep, boolean select, boolean keepExisting)
  {
    return expand(null, key, value, expand, deep, select, keepExisting);
  }

  public boolean expand(M parent, String key, Object value, boolean expand, boolean deep, boolean select, boolean keepExisting)
  {
    List<M> children = parent == null ? store.getRootItems() : store.getChildren(parent);
    for (M child : children)
    {
      if (child.get(key).equals(value))
      {
        setExpanded(child, expand, deep);
        if (select)
        {
          getSelectionModel().select(child, keepExisting);
        }
        scrollIntoView(child);
        return true;
      }
      else if (deep)
      {
        boolean isFound = expand(child, key, value, expand, deep, select, keepExisting);
        if (isFound)
        {
          return true;
        }
      }
    }
    return false;
  }

}
