// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MaximumItemIterable<T> implements Iterable<T>
{
  private Iterable<T> iterable;
  private int maximumItemCount;
  
  public MaximumItemIterable(Iterable<T> iterable, int maximumItemCount)
  {
    this.iterable = iterable;
    this.maximumItemCount = maximumItemCount;
  }

  @Override
  public Iterator<T> iterator()
  {
    return new MaximumItemIterator<T>(iterable.iterator(), maximumItemCount);
  }

  public class MaximumItemIterator<U> implements Iterator<U>
  {
    private Iterator<U> iterator;
    private int maximumItemCount;
    private int itemCount;

    public MaximumItemIterator(Iterator<U> iterator, int maximumItemCount)
    {
      this.iterator = iterator;
      this.maximumItemCount = maximumItemCount;
    }

    @Override
    public boolean hasNext()
    {
      return itemCount < maximumItemCount && iterator.hasNext();
    }

    @Override
    public U next()
    {
      int nextItemCount = itemCount + 1;
      if (nextItemCount > maximumItemCount)
      {
        throw new NoSuchElementException();
      }
      itemCount = nextItemCount;
      return iterator.next();
    }

    @Override
    public void remove()
    {
      iterator.remove();
    }

  }

}
