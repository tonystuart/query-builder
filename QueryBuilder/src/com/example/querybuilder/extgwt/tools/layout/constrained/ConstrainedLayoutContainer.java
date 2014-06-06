// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.extgwt.tools.layout.constrained;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConstrainedLayoutContainer extends LayoutContainer
{
  protected String styleNames;
  protected ConstrainedLayout constrainedLayout;
  private static Sizer sizer = getSizer();

  private static Sizer getSizer()
  {
    sizer = new Sizer();
    RootPanel.get().add(sizer);
    return sizer;
  }

  public ConstrainedLayoutContainer()
  {
    constrainedLayout = new ConstrainedLayout();
    setLayout(constrainedLayout);
    setMonitorWindowResize(true);
  }

  @Override
  protected void onResize(int width, int height)
  {
    super.onResize(width, height);
    constrainedLayout.layout(width, height);
  }

  @Override
  protected void onRender(Element parent, int index)
  {
    super.onRender(parent, index);
    getElement().getStyle().setPosition(Position.RELATIVE);
  }

  @Override
  public int getOffsetWidth()
  {
    int offsetWidth = super.getOffsetWidth();
    if (offsetWidth == 0)
    {
      offsetWidth = constrainedLayout.getNominalWidth(this, getLayoutTarget());
    }
    return offsetWidth;
  }

  @Override
  public int getOffsetHeight()
  {
    int offsetHeight = super.getOffsetHeight();
    if (offsetHeight == 0)
    {
      offsetHeight = constrainedLayout.getNominalHeight(this, getLayoutTarget());
    }
    return offsetHeight;
  }

  @Override
  public void addStyleName(String styleName)
  {
    super.addStyleName(styleName);
    if (styleNames == null)
    {
      styleNames = styleName;
    }
    else
    {
      styleNames += " " + styleName;
    }
  }

  @Override
  public boolean add(Widget widget)
  {
    saveInitialSize(widget);
    return super.add(widget);
  }

  @Override
  public boolean add(Widget widget, LayoutData layoutData)
  {
    saveInitialSize(widget);
    return super.add(widget, layoutData);
  }

  @Override
  public boolean insert(Widget widget, int index, LayoutData layoutData)
  {
    saveInitialSize(widget);
    return super.insert(widget, index, layoutData);
  }

  private void saveInitialSize(Widget widget)
  {
    if (widget instanceof Component)
    {
      Component component = (Component)widget;
      // Something about adding a component that has been added before causes
      // the sizer to return zero for it's height and width. In this case, we
      // already have the size so we don't need to do size again, but it would
      // be nice to know why it doesn't work...
      if (component.getData(ConstrainedLayout.INITIAL_WIDTH_PROPERTY) == null)
      {
        sizer.setStyleName(styleNames);
        Size size = sizer.getSize(component);
        component.setData(ConstrainedLayout.INITIAL_WIDTH_PROPERTY, size.width);
        component.setData(ConstrainedLayout.INITIAL_HEIGHT_PROPERTY, size.height);
      }
    }
  }

}
