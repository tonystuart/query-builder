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

public class IterableArray<T> implements Iterable<T>
{
  private T[] array;

  public IterableArray(T... array)
  {
    this.array = array;
  }

  @Override
  public String toString()
  {
    return toString(this);
  }

  @Override
  public Iterator<T> iterator()
  {
    return new ArrayIterator();
  }

  public class ArrayIterator implements Iterator<T>
  {
    private int offset;

    @Override
    public boolean hasNext()
    {
      return offset < array.length;
    }

    @Override
    public T next()
    {
      return array[offset++];
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Convenience method to convert an Iterable to a string.
   */
  public static String toString(Iterable<? extends Object> iterable)
  {
    return toString(iterable, "", "", "", "", ", ");
  }

  /**
   * Convenience method to convert an Iterable to a string.
   */
  public static String toString(Iterable<? extends Object> iterable, String delimiter)
  {
    return toString(iterable, "", "", "", "", delimiter);
  }

  /**
   * Convenience method to convert an Iterable to a string.
   * @param itemPrefix TODO
   * @param itemSuffix TODO
   */
  public static String toString(Iterable<? extends Object> iterable, String prefix, String suffix, String itemPrefix, String itemSuffix, String delimiter)
  {
    StringBuilder s = new StringBuilder();
    s.append(prefix);
    for (Object value : iterable)
    {
      s.append(itemPrefix);
      if (s.length() > prefix.length())
      {
        s.append(delimiter);
      }
      s.append(value);
      s.append(itemSuffix);
    }
    s.append(suffix);
    return s.toString();
  }

}
