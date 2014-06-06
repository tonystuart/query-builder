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
import com.extjs.gxt.ui.client.widget.ListView;

public class EnumListView<T extends Enum<T>> extends ListView<EnumItem<T>>
{
  private ListStore<EnumItem<T>> enumList = new ListStore<EnumItem<T>>();

  public EnumListView()
  {
    setStore(enumList);
    setDisplayProperty(EnumItem.NAME);
    setId(EnumItem.VALUE);
  }

  public EnumListView(Enum<T>[] values)
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
    getSelectionModel().select(enumList.findModel(EnumItem.VALUE, value), false);
  }

  public void select(String name)
  {
    getSelectionModel().select(enumList.findModel(EnumItem.NAME, name), false);
  }

  public Enum<T> getSelectedItem()
  {
    Enum<T> value = null;
    EnumItem<T> selectedItem = getSelectionModel().getSelectedItem();
    if (selectedItem != null)
    {
      value = selectedItem.get(EnumItem.VALUE);
    }
    return value;
  }
}
