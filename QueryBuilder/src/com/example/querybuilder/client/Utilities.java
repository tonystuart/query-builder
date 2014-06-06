// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.List;

import com.example.querybuilder.shared.ServerException;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public final class Utilities
{
  public static void closeWindow(Widget widget)
  {
    Window window = getWindow(widget);
    if (window != null)
    {
      window.hide();
    }
  }

  public static Window getWindow(Widget widget)
  {
    while (widget != null)
    {
      if (widget instanceof Window)
      {
        return (Window)widget;
      }
      widget = widget.getParent();
    }
    return null;
  }

  public static int compareAlphaNumerically(String a, String b)
  {
    int aAlphaOffset = findAlphaOffset(a);
    if (aAlphaOffset > 0)
    {
      int bAlphaOffset = findAlphaOffset(b);
      if (bAlphaOffset > 0)
      {
        int aNumber = Integer.parseInt(a.substring(0, aAlphaOffset));
        int bNumber = Integer.parseInt(b.substring(0, bAlphaOffset));
        int delta = aNumber - bNumber;
        if (delta != 0)
        {
          return delta;
        }
      }
    }
    return a.compareTo(b);
  }

  public static void displayStackTrace(Throwable e)
  {
    String message;
    if (e instanceof ServerException)
    {
      message = e.getLocalizedMessage();
    }
    else
    {
      message = getStackTrace(e);
    }
    Dialog w = new Dialog();
    w.setSize(600, 400);
    w.setHeading("An Exception Occurred");
    w.setLayout(new FitLayout());
    Html html = new Html(message);
    html.setStyleAttribute("overflow", "auto");
    w.add(html);
    w.setHideOnButtonClick(true);
    w.show();
  }

  private static int findAlphaOffset(String a)
  {
    int length = a.length();
    for (int i = 0; i < length; i++)
    {
      char c = a.charAt(i);
      if (c < '0' || c > '9')
      {
        return i;
      }
    }
    return 0;
  }

  public static String getRootCause(Throwable throwable)
  {
    Throwable rootThrowable = throwable;
    while ((throwable = throwable.getCause()) != null)
    {
      rootThrowable = throwable;
    }
    String rootCause = rootThrowable.getLocalizedMessage();
    if (rootCause == null)
    {
      rootCause = rootThrowable.toString();
    }
    return rootCause;
  }

  public static String getStackTrace(Throwable throwable)
  {
    StringBuilder s = new StringBuilder();
    s.append("<b>");
    s.append(getRootCause(throwable));
    s.append("</b>\n<br/>\n<br/>\nIf you need additional assistance with this problem,\n<br/>\nplease forward the following information to your local support staff:\n<br/>\n<br/>\n");
    s.append(throwable.toString());
    s.append("\n<br/>\n");

    for (StackTraceElement ste : throwable.getStackTrace())
    {
      s.append("at ");
      s.append(ste.getClassName());
      s.append(".");
      s.append(ste.getMethodName());
      s.append("(");
      s.append(ste.getFileName());
      s.append(":");
      s.append(ste.getLineNumber());
      s.append(")");
      s.append("<br/>\n");
    }
    return s.toString();
  }

  public static boolean isLowerCase(char c)
  {
    return 'a' <= c && c <= 'z'; // TODO: Evaluate size/performance impact of GWT Character implementation
  }

  public static boolean isUpperCase(char c)
  {
    return 'A' <= c && c <= 'Z'; // TODO: Evaluate size/performance impact of GWT Character implementation
  }

  public static char toLowerCase(char c)
  {
    return isUpperCase(c) ? (char)(c + ('a' - 'A')) : c; // TODO: Evaluate size/performance impact of GWT Character implementation
  }

  public static char toUpperCase(char c)
  {
    return isLowerCase(c) ? (char)(c - ('a' - 'A')) : c; // TODO: Evaluate size/performance impact of GWT Character implementation
  }

  public static boolean equals(ModelData p, ModelData q, String... keys)
  {
    for (String key : keys)
    {
      Object pValue = p.get(key);
      Object qValue = q.get(key);
      if ((pValue == null && qValue != null) || !pValue.equals(qValue))
      {
        return false;
      }
    }
    return true;
  }

  public static String concatenate(List<ModelData> modelDataList, String name, String delimiter)
  {
    StringBuilder s = new StringBuilder();
    for (ModelData modelData : modelDataList)
    {
      if (s.length() > 0)
      {
        s.append(delimiter);
      }
      String value = modelData.get(name);
      s.append(value);
    }
    return s.toString();
  }
}
