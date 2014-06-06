// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

import java.io.Serializable;

public class TableReference implements Serializable
{
  private static final int UNDEF = -1;

  private String connectionName;
  private NodeType nodeType;
  private String schemaName;
  private String tableAlias;
  private int tableId;
  private String tableName;

  public TableReference()
  {
  }

  public TableReference(int tableId)
  {
    this(NodeType.INTERNAL_TABLE, null, null, null, tableId);
  }

  public TableReference(NodeType nodeType, String connectionName, String schemaName, String tableName)
  {
    this(nodeType, connectionName, schemaName, tableName, UNDEF);
  }

  public TableReference(NodeType nodeType, String connectionName, String schemaName, String tableName, int tableId)
  {
    this.nodeType = nodeType;
    this.connectionName = connectionName;
    this.schemaName = schemaName;
    this.tableName = tableName;
    this.tableId = tableId;
  }

  public TableReference(String connectionName, String schemaName, String tableName, int tableId)
  {
    this(NodeType.INTERNAL_TABLE, connectionName, schemaName, tableName, tableId);
  }

  public String getConnectionName()
  {
    return connectionName;
  }

  public NodeType getNodeType()
  {
    return nodeType;
  }

  public String getSchemaName()
  {
    return schemaName;
  }

  public String getTableAlias()
  {
    return tableAlias;
  }

  public int getTableId()
  {
    return tableId;
  }

  public String getTableName()
  {
    return tableName;
  }

  public boolean isInternal()
  {
    return nodeType == NodeType.INTERNAL_TABLE;
  }

  public void setConnectionName(String connectionName)
  {
    this.connectionName = connectionName;
  }

  public void setNodeType(NodeType nodeType)
  {
    this.nodeType = nodeType;
  }

  public void setSchemaName(String schemaName)
  {
    this.schemaName = schemaName;
  }

  public void setTableAlias(String tableAlias)
  {
    this.tableAlias = tableAlias;
  }

  public void setTableId(int tableId)
  {
    this.tableId = tableId;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

}
