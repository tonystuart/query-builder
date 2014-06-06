// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.util.ArrayList;
import java.util.HashMap;

public class ArrayListHashMap<K, V> extends HashMap<K, V>
{
  private ArrayList<K> keys = new ArrayList<K>();
  
  public V put(K key, V value) 
  {
    if (!containsKey(key))
    {
      keys.add(key);
    }
    V previousValue = super.put(key, value);
    return previousValue;
  }
  
  public V getValueAt(int offset)
  {
    K key = getKeyAt(offset);
    V value = get(key);
    return value;
  }

  public K getKeyAt(int offset)
  {
    K key = keys.get(offset);
    return key;
  }

}
