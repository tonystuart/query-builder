// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

public final class ColumnTypes
{
  public static final int UNKNOWN = 0;
  public static final int STRING = 1;
  public static final int INTEGER = 2;
  public static final int LONG = 3;
  public static final int DECIMAL = 4;
  public static final int DOUBLE = 5;
  public static final int DATE = 6;
  public static final int LOGICAL = 7;

  public static final String[] typeNames = new String[] {
      "UNKNOWN",
      "CHARACTER",
      "NUMBER (INT)",
      "NUMBER (LONG)",
      "NUMBER (DEC)",
      "NUMBER (FLOAT)",
      "DATE",
      "LOGICAL",
  };

  public static final boolean isNumber(int columnType)
  {
    return columnType == INTEGER || columnType == LONG || columnType == DECIMAL || columnType == DOUBLE;
  }

  public static final String getTypeName(int columnType)
  {
    //System.out.println("ColumnTypes.getTypesNames: columnType="+columnType+", typeName="+typeNames[columnType]);
    return typeNames[columnType];
  }
}
