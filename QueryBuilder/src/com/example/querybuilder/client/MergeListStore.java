// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;

public class MergeListStore extends ListStore<ModelData>
{
  private String name;

  public MergeListStore(String name)
  {
    this.name = name;
  }

  public void merge(List<ModelData> newModels)
  {
    int oldIndex = 0;
    while (oldIndex < getCount())
    {
      ModelData oldModel = getAt(oldIndex);
      int newIndex = findModel(newModels, oldModel);
      if (newIndex == -1)
      {
        remove(oldIndex);
      }
      else
      {
        oldIndex++;
      }
    }

    for (ModelData newModel : newModels)
    {
      String value = newModel.get(name);
      ModelData oldModel = findModel(name, value);
      if (oldModel == null)
      {
        int index = findInsertPoint(value);
        insert(newModel, index);
      }
    }
  }

  public int findInsertPoint(String value)
  {
    int itemOffset = 0;
    for (ModelData oldModel : all)
    {
      String testValue = oldModel.get(name);
      if (Utilities.compareAlphaNumerically(testValue, value) > 0)
      {
        return itemOffset;
      }
      itemOffset++;
    }
    return itemOffset;
  }

  private int findModel(List<ModelData> models, ModelData model)
  {
    int offset = 0;
    String value = model.get(name);
    for (ModelData testModel : models)
    {
      String testValue = testModel.get(name);
      if (testValue.equals(value))
      {
        return offset;
      }
      offset++;
    }
    return -1;
  }
}
