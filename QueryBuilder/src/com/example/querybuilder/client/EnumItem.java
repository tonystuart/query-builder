// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class EnumItem<T extends Enum<T>> extends BaseModelData
{
  public static final String NAME = "name";
  public static final String VALUE = "value";

  public EnumItem(String name, Enum<T> value)
  {
    set(NAME, name);
    set(VALUE, value);
  }

  public String getName()
  {
    return get(NAME);
  }

  public T getValue()
  {
    return get(VALUE);
  }
}
