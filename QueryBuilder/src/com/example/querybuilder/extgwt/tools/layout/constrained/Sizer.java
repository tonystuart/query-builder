// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.extgwt.tools.layout.constrained;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Sizer extends FlowPanel
  {
    private Style style;

    public Sizer()
    {
      Element element = getElement();
      style = element.getStyle();
      style.setPosition(Position.ABSOLUTE);
      style.setVisibility(Visibility.HIDDEN);
      style.setProperty("height", "auto");
      style.setProperty("width", "auto");
    }

    public Size getSize(Widget widget)
    {
      add(widget);
      int width = getOffsetWidth();
      int height = getOffsetHeight();
      Size size = new Size(width, height);
      remove(widget);
      return size;
    }

    public Size getBalancedSize(Widget widget)
    {
      add(widget);
      int height = getOffsetHeight();
      int width = getOffsetWidth();
      remove(widget);
      width = (int)Math.sqrt(height * width);
      style.setProperty("width", width + "px");
      add(widget);
      height = getOffsetHeight();
      Size size = new Size(width, height);
      remove(widget);
      style.setProperty("width", "auto");
      return size;
    }

    public int getWidth(Widget widget)
    {
      add(widget);
      int width = getOffsetWidth();
      remove(widget);
      return width;
    }

    public int getHeight(Widget widget)
    {
      add(widget);
      int height = getOffsetHeight();
      remove(widget);
      return height;
    }
  }

  
