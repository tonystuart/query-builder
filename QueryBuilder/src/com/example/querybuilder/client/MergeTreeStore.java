// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.Map;
import java.util.Map.Entry;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.store.TreeStore;

public class MergeTreeStore extends TreeStore<ModelData>
{
  private String[] keys;

  public MergeTreeStore(String... keys)
  {
    this.keys = keys;
  }

  @Override
  public void add(ModelData parent, ModelData item, boolean addChildren)
  {
    if (parent == null)
    {
      add(item, addChildren);
    }
    else
    {
      super.add(parent, item, addChildren);
    }
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public int compareKeys(ModelData oldItem, ModelData newItem)
  {
    for (String key : keys)
    {
      Comparable oldValue = oldItem.get(key);
      Comparable newValue = newItem.get(key);
      int delta = oldValue.compareTo(newValue);
      if (delta != 0)
      {
        return delta;
      }
    }
    return 0;
  }

  public ModelData find(ModelData parent, String property, Object value, boolean isDeep)
  {
    int childCount = getChildCount(parent);
    for (int childOffset = 0; childOffset < childCount; childOffset++)
    {
      ModelData childNode = getChild(parent, childOffset);
      if (value.equals(childNode.get(property)))
      {
        return childNode;
      }
      else if (isDeep)
      {
        ModelData item = find(childNode, property, value, isDeep);
        if (item != null)
        {
          return item;
        }
      }
    }
    return null;
  }

  public ModelData find(String property, Object value)
  {
    return find(null, property, value, true);
  }

  public ModelData find(String property, Object value, boolean isDeep)
  {
    return find(null, property, value, isDeep);
  }

  public int findChildInTreeModel(TreeModel newParent, ModelData oldChild)
  {
    int childCount = newParent.getChildCount();
    for (int childIndex = 0; childIndex < childCount; childIndex++)
    {
      ModelData newChild = newParent.getChild(childIndex);
      if (isEqual(oldChild, newChild))
      {
        return childIndex;
      }
    }
    return -1;
  }

  private int findChildInTreeStore(ModelData oldParent, ModelData newChild)
  {
    int childCount = getChildCount(oldParent);
    for (int childIndex = 0; childIndex < childCount; childIndex++)
    {
      ModelData oldChild = getChild(oldParent, childIndex);
      if (isEqual(oldChild, newChild))
      {
        return childIndex;
      }
    }
    return -1;
  }

  public int findInsertPoint(ModelData parent, ModelData newItem)
  {
    int itemCount = getChildCount(parent);
    for (int itemOffset = 0; itemOffset < itemCount; itemOffset++)
    {
      ModelData oldItem = getChild(parent, itemOffset);
      if (compareKeys(oldItem, newItem) > 0)
      {
        return itemOffset;
      }
    }
    return itemCount;
  }

  public void insert(ModelData parent, ModelData item, int index, boolean addChildren)
  {
    if (parent == null)
    {
      insert(item, index, addChildren);
    }
    else
    {
      super.insert(parent, item, index, addChildren);
    }
  }

  public boolean isEqual(ModelData oldChild, ModelData newChild)
  {
    for (String key : keys)
    {
      Object oldValue = oldChild.get(key);
      Object newValue = newChild.get(key);
      if (!oldValue.equals(newValue))
      {
        return false;
      }
    }
    return true;
  }

  public void merge(ModelData oldParent, TreeModel newParent)
  {
    int oldIndex = 0;
    while (oldIndex < getChildCount(oldParent))
    {
      ModelData oldChild = getChild(oldParent, oldIndex);
      int newIndex = findChildInTreeModel((TreeModel)newParent, oldChild);
      if (newIndex == -1)
      {
        remove(oldParent, oldChild);
      }
      else
      {
        ModelData newChild = newParent.getChild(newIndex);
        updateProperties(oldChild, newChild);
        if (newChild instanceof TreeModel)
        {
          merge(oldChild, (TreeModel)newChild);
        }
        oldIndex++;
      }
    }

    int newIndex = 0;
    while (newIndex < newParent.getChildCount())
    {
      ModelData newChild = newParent.getChild(newIndex);
      oldIndex = findChildInTreeStore(oldParent, newChild);
      if (oldIndex == -1)
      {
        int index = findInsertPoint(oldParent, newChild);
        insert(oldParent, newChild, index, true);
      }
      newIndex++;
    }
  }

  public void merge(TreeModel newTree)
  {
    merge(null, newTree);
  }

  @Override
  public void remove(ModelData parent, ModelData child)
  {
    if (parent == null)
    {
      remove(child);
    }
    else
    {
      super.remove(parent, child);
    }
  }

  public void updateProperties(ModelData oldChild, ModelData newChild)
  {
    Map<String, Object> oldProperties = oldChild.getProperties();
    for (Entry<String, Object> entry : newChild.getProperties().entrySet())
    {
      String key = entry.getKey();
      if (!key.equals("gxt-parent")) // See com/extjs/gxt/ui/client/data/BaseTreeModel.java
      {
        Object value = entry.getValue();
        if (!oldProperties.get(key).equals(value))
        {
          oldChild.set(key, value);
        }
      }
    }
  }

}
