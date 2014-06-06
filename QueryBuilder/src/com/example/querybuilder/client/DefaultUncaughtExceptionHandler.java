// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;

// Setting the uncaught exception handler lets us display a message rather
// than have it written to the DevelopmentMode log window which is typically
// the wrong size and/or not visible.

public final class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler
{
  public void onUncaughtException(Throwable e)
  {
    Utilities.displayStackTrace(e);
  }

}

