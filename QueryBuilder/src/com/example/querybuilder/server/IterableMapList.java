// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides a means for treating an an iterable (or array) of maps of key,
 * value-list pairs as a single iterable of values.
 */
public class IterableMapList<T, U> implements Iterable<U>
{
  private Iterable<Map<T, List<U>>> maps;

  public IterableMapList(Map<T, List<U>>... maps)
  {
    this(new IterableArray<Map<T, List<U>>>(maps));
  }

  public IterableMapList(Iterable<Map<T, List<U>>> maps)
  {
    this.maps = maps;
  }

  @Override
  public Iterator<U> iterator()
  {
    return new MapListIterator(maps);
  }

  private final class MapListIterator implements Iterator<U>
  {
    private Iterator<U> currentList;
    private Iterator<List<U>> lists;
    private Iterator<Map<T, List<U>>> maps;

    public MapListIterator(Iterable<Map<T, List<U>>> map)
    {
      this.maps = map.iterator();
    }

    @Override
    public boolean hasNext()
    {
      if (currentList == null || currentList.hasNext() == false)
      {
        if (lists == null || lists.hasNext() == false)
        {
          if (maps.hasNext() == false)
          {
            return false;
          }
          lists = maps.next().values().iterator();
        }
        if (lists.hasNext() == false)
        {
          return false;
        }
        currentList = lists.next().iterator();
        return currentList.hasNext();
      }
      return true;
    }

    @Override
    public U next()
    {
      if (currentList == null || currentList.hasNext() == false)
      {
        if (lists == null || lists.hasNext() == false)
        {
          lists = maps.next().values().iterator();
        }
        currentList = lists.next().iterator();
      }
      return currentList.next();
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

}
