// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.example.querybuilder.shared.EnumText;
import com.extjs.gxt.ui.client.store.ListStore;

public class EnumCellEditorComboBox<T extends Enum<T>> extends ComboBoxCellEditor<EnumItem<T>>
{
  private ListStore<EnumItem<T>> enumList = new ListStore<EnumItem<T>>();

  public EnumCellEditorComboBox()
  {
    setStore(enumList);
    setDisplayField(EnumItem.NAME);
    setId(EnumItem.VALUE);
  }

  public EnumCellEditorComboBox(Enum<T>[] values)
  {
    this();

    for (Enum<T> value : values)
    {
      add(EnumText.getInstance().get(value), value);
    }
  }

  public void add(String name, Enum<T> value)
  {
    enumList.add(new EnumItem<T>(name, value));
  }

  public void select(Enum<T> value)
  {
    setValue(enumList.findModel(EnumItem.VALUE, value));
  }

  public void select(String name)
  {
    setValue(enumList.findModel(EnumItem.NAME, name));
  }

}
