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

/**
 * A very cool class that adapts one form of Iterable to another. Because it can
 * hide a significant amount of processing at a low level, it should be used
 * with care and only under the following circumstances:
 * <ul>
 * <li>You are certain that there is only one trip through the Iterable</li>
 * <li>The <code>Iterable</code>s and/or <code>from</code> and <code>to</code>
 * classes are too big to be conveniently held in memory</li>
 * <li>The underlying values are changing and you need the latest value</li>
 * </ul>
 * Otherwise it is generally more efficient to use
 * <code>IterableLinkedListAdapter</code>.
 * <p>
 * Example:
 * <pre>
 * public static void main(String[] args)
 * {
 *   LinkedList&lt;MyFromClass&gt; fromList = new LinkedList&lt;MyFromClass&gt;();
 *   fromList.add(new MyFromClass(&quot;Now&quot;));
 *   fromList.add(new MyFromClass(&quot;is&quot;));
 *   fromList.add(new MyFromClass(&quot;the&quot;));
 * 
 *   for (MyToClass toString : new MyIterableAdapter(fromList))
 *   {
 *     System.out.println(toString);
 *   }
 * }
 * 
 * public static class MyFromClass
 * {
 *   private String value;
 * 
 *   public MyFromClass(String value)
 *   {
 *     this.value = value;
 *   }
 * 
 *   public String getValue()
 *   {
 *     return value;
 *   }
 * }
 * 
 * public static class MyToClass
 * {
 *   private String value;
 * 
 *   public MyToClass(String value)
 *   {
 *     this.value = value;
 *   }
 * 
 *   public String toString()
 *   {
 *     return value;
 *   }
 * }
 * 
 * public static class MyIterableAdapter extends PotentiallyVeryExpensiveAndHardToSpotIterableAdapter&lt;MyFromClass, MyToClass&gt;
 * {
 *   public MyIterableAdapter(Iterable&lt;MyFromClass&gt; fromIterable)
 *   {
 *     super(fromIterable);
 *   }
 * 
 *   public MyToClass adapt(MyFromClass fromValue)
 *   {
 *     return new MyToClass(&quot;To-&quot; + fromValue.getValue());
 *   }
 * }
 * 
 * </pre>
 * 
 * @param <F>
 *          from class
 * @param <T>
 *          to class
 */
public abstract class IterableAdapter<F, T> implements Iterable<T>
{
  private Iterable<F> fromIterable;

  public IterableAdapter(Iterable<F> fromIterable)
  {
    this.fromIterable = fromIterable;
  }

  @Override
  public Iterator<T> iterator()
  {
    return new IteratorAdapter(fromIterable.iterator());
  }

  public abstract T adapt(F fromValue);

  public class IteratorAdapter implements Iterator<T>
  {
    private Iterator<F> iterator;

    public IteratorAdapter(Iterator<F> iterator)
    {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    @Override
    public T next()
    {
      return adapt(iterator.next());
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }

  }
}
