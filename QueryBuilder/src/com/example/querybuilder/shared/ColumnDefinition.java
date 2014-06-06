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

public class ColumnDefinition extends ColumnDescriptor implements Serializable
{
  public static final String AUTO_INCREMENT = "autoIncrement";
  public static final String CHARACTER_OCTET_LENGTH = "characterOctetLength";
  public static final String COLUMN_SIZE = "columnSize";
  public static final String DECIMAL_DIGITS = "decimalDigits";
  public static final String DEFAULT_VALUE = "defaultValue";
  public static final String NULLABLE = "nullable";
  public static final String ORDINAL_POSITION = "ordinalPosition";
  public static final String REMARKS = "remarks";
  public static final String TABLE_CATALOG = "tableCatalog";
  public static final String TYPE_NAME = "typeName";

  public ColumnDefinition()
  {
  }

  public ColumnDefinition(String tableCatalog, String columnName, int dataType, String typeName, int columnSize, int decimalDigits, String remarks, String defaultValue, int characterOctetLength, int ordinalPosition, String nullable, String autoIncrement)
  {
    super(columnName, dataType);
    
    set(TABLE_CATALOG, tableCatalog);
    set(TYPE_NAME, typeName);
    set(COLUMN_SIZE, columnSize);
    set(DECIMAL_DIGITS, decimalDigits);
    set(REMARKS, remarks);
    set(DEFAULT_VALUE, defaultValue);
    set(CHARACTER_OCTET_LENGTH, characterOctetLength);
    set(ORDINAL_POSITION, ordinalPosition);
    set(NULLABLE, nullable);
    set(AUTO_INCREMENT, autoIncrement);
  }

  public String getTypeName()
  {
    return get(TYPE_NAME);
  }

  public int getColumnSize()
  {
    return get(COLUMN_SIZE);
  }
  
  public int getDecimalDigits()
  {
    return get(DECIMAL_DIGITS);
  }
  
  public int getCharacterOctetLength()
  {
    return get(CHARACTER_OCTET_LENGTH);
  }
}
