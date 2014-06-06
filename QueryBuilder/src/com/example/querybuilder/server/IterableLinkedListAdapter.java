// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.util.LinkedList;

public class IterableLinkedListAdapter<F, T> extends LinkedList<T>
{
  protected IterableLinkedListAdapter()
  {
    // For derived classes that require additional constructor initialization before invoking adaptAll
  }

  public IterableLinkedListAdapter(Iterable<F> fromIterable)
  {
    adaptAll(fromIterable);
  }

  protected void adaptAll(Iterable<F> fromIterable)
  {
    for (F f : fromIterable)
    {
      add(adapt(f));
    }
  }

  @SuppressWarnings("unchecked")
  public T adapt(F f)
  {
    return (T)f;
  }
}
