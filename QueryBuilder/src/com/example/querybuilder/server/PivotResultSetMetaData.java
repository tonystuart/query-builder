// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Provides a meta data for a pivot result set.
 * <p/>
 * The DataWarehouse requires only the following methods:
 * <ul>
 * <li>getColumnCount</li>
 * <li>getColumnName</li>
 * <li>getColumnType</li>
 * </ul>
 */
public class PivotResultSetMetaData implements ResultSetMetaData
{
  private int columnCount;
  private String[] columnNames;
  private int[] columnTypes;

  public PivotResultSetMetaData(int columnCount, String[] columnNames, int[] columnTypes)
  {
    this.columnCount = columnCount;
    this.columnNames = columnNames;
    this.columnTypes = columnTypes;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException
  {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException
  {
    return false;
  }

  @Override
  public int getColumnCount() throws SQLException
  {
    return columnCount;
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException
  {
    return false;
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException
  {
    return false;
  }

  @Override
  public boolean isSearchable(int column) throws SQLException
  {
    return false;
  }

  @Override
  public boolean isCurrency(int column) throws SQLException
  {
    return false;
  }

  @Override
  public int isNullable(int column) throws SQLException
  {
    return 0;
  }

  @Override
  public boolean isSigned(int column) throws SQLException
  {
    return false;
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException
  {
    return 0;
  }

  @Override
  public String getColumnLabel(int column) throws SQLException
  {
    return null;
  }

  @Override
  public String getColumnName(int column) throws SQLException
  {
    return columnNames[column - 1];
  }

  @Override
  public String getSchemaName(int column) throws SQLException
  {
    return null;
  }

  @Override
  public int getPrecision(int column) throws SQLException
  {
    return 0;
  }

  @Override
  public int getScale(int column) throws SQLException
  {
    return 0;
  }

  @Override
  public String getTableName(int column) throws SQLException
  {
    return null;
  }

  @Override
  public String getCatalogName(int column) throws SQLException
  {
    return null;
  }

  @Override
  public int getColumnType(int column) throws SQLException
  {
    return columnTypes[column - 1];
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException
  {
    return null;
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException
  {
    return false;
  }

  @Override
  public boolean isWritable(int column) throws SQLException
  {
    return false;
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException
  {
    return false;
  }

  @Override
  public String getColumnClassName(int column) throws SQLException
  {
    return null;
  }

}
