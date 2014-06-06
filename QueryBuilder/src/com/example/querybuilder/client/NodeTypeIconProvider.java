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
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class NodeTypeIconProvider implements ModelIconProvider<ModelData>
{
  private static NodeTypeIconProvider instance;

  private NodeTypeIconProvider()
  {
  }

  public synchronized static NodeTypeIconProvider getInstance()
  {
    if (instance == null)
    {
      instance = new NodeTypeIconProvider();
    }
    return instance;
  }

  @Override
  public AbstractImagePrototype getIcon(ModelData modelData)
  {
    AbstractImagePrototype icon;
    NodeType nodeType = modelData.get(Keys.NODE_TYPE);
    icon = getIcon(nodeType);
    return icon;
  }

  public AbstractImagePrototype getIcon(NodeType nodeType)
  {
    AbstractImagePrototype icon;
    switch (nodeType)
    {
      case COLUMN:
        icon = Resources.BULLET_BLUE;
        break;
      case CONNECTION:
        icon = Resources.DATABASE_CONNECT;
        break;
      case CONNECTION_ERROR:
        icon = Resources.DATABASE_ERROR;
        break;
      case CONNECTION_ROOT:
        icon = Resources.DATABASE;
        break;
      case SCHEMA:
        icon = Resources.TABLE_MULTIPLE;
        break;
      case EXTERNAL_TABLE:
        icon = Resources.TABLE;
        break;
      case EXTERNAL_VIEW:
        icon = Resources.TABLE_LINK;
        break;
      case INTERNAL_TABLE:
        icon = Resources.TABLE_GO;
        break;
      default:
        icon = null;
        break;
    }
    return icon;
  }
}
