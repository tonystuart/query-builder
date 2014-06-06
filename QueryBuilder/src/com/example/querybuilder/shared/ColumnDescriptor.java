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

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ColumnDescriptor extends BaseModelData implements Serializable
{
  public static final String COLUMN_NAME = Keys.NAME;
  public static final String DATA_TYPE = "dataType";

  public ColumnDescriptor()
  {
  }

  public ColumnDescriptor(String columnName, int dataType)
  {
    set(COLUMN_NAME, columnName);
    set(DATA_TYPE, dataType);
  }

  public String getColumnName()
  {
    return get(COLUMN_NAME);
  }

  public int getDataType()
  {
    return get(DATA_TYPE);
  }
}
