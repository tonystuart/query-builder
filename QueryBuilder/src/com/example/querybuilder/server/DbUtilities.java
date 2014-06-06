// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

public class DbUtilities
{

  public static boolean isUpperCase(String columnName)
  {
    int length = columnName.length();
    for (int i = 0; i < length; i++)
    {
      char c = columnName.charAt(i);
      if (Character.isLetter(c) && !Character.isUpperCase(c))
      {
        return false;
      }
    }
    return true;
  }

}
