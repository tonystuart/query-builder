// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.derby;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Cache<K, V>
{
  private LinkedList<K> list = new LinkedList<K>();
  private Map<K, V> map = new HashMap<K, V>();
  private int maximumSize;

  public Cache(int maximumSize)
  {
    this.maximumSize = maximumSize;
  }

  public V get(K key)
  {
    V value = map.get(key);
    if (value == null ? map.containsKey(key) : true)
    {
      synchronized (map)
      {
        list.remove(key);
        list.addFirst(key);
      }
    }
    return value;
  }

  public V put(K key, V newValue)
  {
    V oldValue = map.get(key);
    if (oldValue == null ? map.containsKey(key) : true)
    {
      synchronized (map)
      {
        list.remove(key);
        list.addFirst(key);
      }
    }
    else
    {
      synchronized (map)
      {
        if (list.size() >= maximumSize)
        {
          map.remove(list.getLast());
          list.removeLast();
        }
        map.put(key, newValue);
        list.addFirst(key);
      }
    }
    return oldValue;
  }

}
