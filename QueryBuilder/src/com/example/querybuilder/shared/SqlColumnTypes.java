// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

public class SqlColumnTypes
{
  public final static int ARRAY = 2003;
  public final static int BIGINT = -5;
  public final static int BINARY = -2;
  public final static int BIT = -7;
  public final static int BLOB = 2004;
  public final static int BOOLEAN = 16;
  public final static int CHAR = 1;
  public final static int CLOB = 2005;
  public final static int DATALINK = 70;
  public final static int DATE = 91;
  public final static int DECIMAL = 3;
  public final static int DISTINCT = 2001;
  public final static int DOUBLE = 8;
  public final static int FLOAT = 6;
  public final static int INTEGER = 4;
  public final static int JAVA_OBJECT = 2000;
  public static final int LONGNVARCHAR = -16;
  public final static int LONGVARBINARY = -4;
  public final static int LONGVARCHAR = -1;
  public static final int NCHAR = -15;
  public static final int NCLOB = 2011;
  public final static int NULL = 0;
  public final static int NUMERIC = 2;
  public static final int NVARCHAR = -9;
  public final static int OTHER = 1111;
  public final static int REAL = 7;
  public final static int REF = 2006;
  public final static int ROWID = -8;
  public final static int SMALLINT = 5;
  public static final int SQLXML = 2009;
  public final static int STRUCT = 2002;
  public final static int TIME = 92;
  public final static int TIMESTAMP = 93;
  public final static int TINYINT = -6;
  public final static int VARBINARY = -3;
  public final static int VARCHAR = 12;

  public static boolean isDate(int columnType)
  {
    switch (columnType)
    {
      case DATE:
        return true;
      default:
        return false;
    }
  }

  public static boolean isNumber(int columnType)
  {
    switch (columnType)
    {
      case BIGINT:
      case DECIMAL:
      case DOUBLE:
      case FLOAT:
      case INTEGER:
      case NUMERIC:
      case REAL:
      case SMALLINT:
      case TINYINT:
        return true;
      default:
        return false;
    }
  }

}
