// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.sql.Types;

import com.example.querybuilder.shared.ColumnTypes;

public class ColumnTypeMap
{
  public static final int mapType(int columnType)
  {
    int mappedType;
    switch (columnType)
    {
      case Types.TINYINT:
      case Types.SMALLINT:
      case Types.INTEGER:
        mappedType = ColumnTypes.INTEGER;
        break;
      case Types.BIGINT:
        mappedType = ColumnTypes.LONG;
        break;
      case Types.FLOAT:
      case Types.REAL:
      case Types.DOUBLE:
      case Types.NUMERIC:
        mappedType = ColumnTypes.DOUBLE;
        break;
      case Types.DECIMAL:
        mappedType = ColumnTypes.DECIMAL;
        break;
      case Types.DATE:
      case Types.TIME:
      case Types.TIMESTAMP:
        mappedType = ColumnTypes.DATE;
        break;
      case Types.BIT:
      case Types.BOOLEAN:
        mappedType = ColumnTypes.LOGICAL;
        break;
      default:
        mappedType = ColumnTypes.STRING;
        break;
    }
    //System.out.println("ColumnTypeMap.mapType: mapped " + columnType + " to " + mappedType);
    return mappedType;
  }

}
