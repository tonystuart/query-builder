// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.util.Comparator;
import java.util.Iterator;

public class IterableComparator<T> implements Comparator<Iterable<T>>
{
  @Override
  @SuppressWarnings("unchecked")
  public int compare(Iterable<T> iterable1, Iterable<T> iterable2)
  {
    Iterator<T> iterator1 = iterable1.iterator();
    Iterator<T> iterator2 = iterable2.iterator();
    while (iterator1.hasNext())
    {
      if (!iterator2.hasNext())
      {
        return 1;
      }
      T value1 = iterator1.next();
      T value2 = iterator2.next();
      if (value1 == null)
      {
        if (value2 != null)
        {
          return -1;
        }
      }
      else if (value2 == null)
      {
        return 1;
      }
      else
      {
        try
        {
          int relationship = ((Comparable<T>)value1).compareTo(value2);
          if (relationship != 0)
          {
            return relationship;
          }
        }
        catch (ClassCastException e)
        {
          int relationship = value1.toString().compareTo(value2.toString());
          if (relationship != 0)
          {
            return relationship;
          }
        }
      }
    }
    if (iterator2.hasNext())
    {
      return -1;
    }
    return 0;
  }
}
