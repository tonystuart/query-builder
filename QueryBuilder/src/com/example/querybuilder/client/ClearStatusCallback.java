// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

public abstract class ClearStatusCallback<T> extends FailureReportingAsyncCallback<T>
{
  @Override
  public void onFailure(Throwable caught)
  {
    QueryBuilderServiceBus.clearStatus();
    super.onFailure(caught);
  }

  @Override
  public void onSuccess(T result)
  {
    QueryBuilderServiceBus.clearStatus();
    process(result);
  }

  protected abstract void process(T result);
}
