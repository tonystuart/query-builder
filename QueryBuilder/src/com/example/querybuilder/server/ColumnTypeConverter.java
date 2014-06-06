// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// See http://db.apache.org/ojb/docu/guides/jdbc-types.html

public class ColumnTypeConverter
{
  private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  public static final Object toJavaType(int columnType, String input)
  {
    try
    {
      Object output;
      switch (columnType)
      {
        case Types.TINYINT:
          output = new Byte(input);
          break;
        case Types.SMALLINT:
          output = new Short(input);
          break;
        case Types.INTEGER:
          output = new Integer(input);
          break;
        case Types.BIGINT:
          output = new Long(input);
          break;
        case Types.FLOAT:
          output = new Double(input);
          break;
        case Types.REAL:
          output = new Float(input);
          break;
        case Types.DOUBLE:
          output = new Double(input);
          break;
        case Types.NUMERIC:
          output = new BigDecimal(input);
          break;
        case Types.DECIMAL:
          output = new BigDecimal(input);
          break;
        case Types.DATE:
          output = new java.sql.Date(dateFormat.parse(input).getTime());
          break;
        case Types.TIME:
          output = new java.sql.Time(timeFormat.parse(input).getTime());
          break;
        case Types.TIMESTAMP:
          output = new java.sql.Timestamp(timestampFormat.parse(input).getTime());
          break;
        case Types.BIT:
        case Types.BOOLEAN:
          output = new Boolean(input);
          break;
        default:
          output = input;
          break;
      }
      return output;
    }
    catch (NumberFormatException e)
    {
      throw new RuntimeException(e);
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }

}
