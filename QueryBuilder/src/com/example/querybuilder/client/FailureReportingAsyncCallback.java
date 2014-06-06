// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class FailureReportingAsyncCallback<T> implements AsyncCallback<T>
{

  @Override
  public void onFailure(Throwable caught)
  {
    //Throwable unwrappedException = unwrap(caught);
    Utilities.displayStackTrace(caught);
  }

  public Throwable unwrap(Throwable e)
  {
    Throwable cause = e;
    while ((e = e.getCause())!= null)
    {
      cause = e;
    }
    return cause;
  }


}
