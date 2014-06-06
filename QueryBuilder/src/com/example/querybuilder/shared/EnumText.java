// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

import java.util.HashMap;

public final class EnumText
{
  // TODO: Figure out best way to get i18n text to client. Note that enum values are needed before first async call completes.
  private static EnumText instance = new EnumText();
  private HashMap<Enum<?>, String> enumText = new HashMap<Enum<?>, String>();

  public static EnumText getInstance()
  {
    return instance;
  }

  private EnumText()
  {
    enumText.put(JoinType.INNER_JOIN, "INNER JOIN");
    enumText.put(JoinType.LEFT_OUTER_JOIN, "LEFT OUTER JOIN");
    enumText.put(JoinType.RIGHT_OUTER_JOIN, "RIGHT OUTER JOIN");
    
    enumText.put(Condition.LT, "<");
    enumText.put(Condition.LE, "<=");
    enumText.put(Condition.EQ, "=");
    enumText.put(Condition.GE, ">=");
    enumText.put(Condition.GT, ">");
    enumText.put(Condition.NE, "!=");
  }

  public String get(Enum<?> enumValue)
  {
    String text = enumText.get(enumValue);
    if (text == null)
    {
      text = enumValue.name();
    }
    return text;
  }

  public void put(Enum<?> enumItem, String text)
  {
    enumText.put(enumItem, text);
  }

}
