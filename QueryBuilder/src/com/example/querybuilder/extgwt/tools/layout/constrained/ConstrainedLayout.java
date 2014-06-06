// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.extgwt.tools.layout.constrained;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.dom.client.Style.Position;

public class ConstrainedLayout extends AbsoluteLayout
{
  public static final String INITIAL_HEIGHT_PROPERTY = "com.bdac.extgwt.tools.layout.constrained.initialHeight";
  public static final String INITIAL_WIDTH_PROPERTY = "com.bdac.extgwt.tools.layout.constrained.initialWidth";

  private int allocatedHeight;
  private ArrayList<Integer> allocatedHeights;
  private int allocatedWidth;
  private ArrayList<Integer> allocatedWidths;
  private Constraint defaultConstraint = new Constraint(true, 1, 0, 5, 5, 5, 5);
  private boolean isDebug;
  private int totalHeight;
  private int totalWidth;

  protected void calculateSizes(Container<?> container, El target)
  {
    renderAll(container, target);
    processComponents(container, Pass.Calculate);
  }

  public final Constraint getDefaultConstraint()
  {
    return defaultConstraint;
  }

  public int getHeight(Component component)
  {
    int height;
    Integer initialHeight = component.getData(INITIAL_HEIGHT_PROPERTY);
    if (initialHeight == null)
    {
      height = component.getOffsetHeight();
    }
    else
    {
      height = initialHeight;
    }
    return height;
  }

  public int getNominalHeight(Container<?> container, El target)
  {
    if (allocatedHeight == 0)
    {
      calculateSizes(container, target);
    }
    return allocatedHeight;
  }

  public int getNominalWidth(Container<?> container, El target)
  {
    if (allocatedWidth == 0)
    {
      calculateSizes(container, target);
    }
    return allocatedWidth;
  }

  public int getWidth(Component component)
  {
    int width;
    Integer initialWidth = component.getData(INITIAL_WIDTH_PROPERTY);
    if (initialWidth == null)
    {
      width = component.getOffsetWidth();
    }
    else
    {
      width = initialWidth;
    }
    return width;
  }

  public void layout(int width, int height)
  {
    this.totalWidth = width;
    this.totalHeight = height;
    super.layout();
  }

  @Override
  protected void onLayout(Container<?> container, El target)
  {
    calculateSizes(container, target);
    processComponents(container, Pass.Position);
  }

  protected void processComponents(Container<?> container, Pass pass)
  {
    int x = 0;
    int y = 0;
    int deltaY = 0;
    int row = -1;
    int maxHeight = 0;

    if (isDebug)
    {
      System.out.println("ConstrainedLayout.processComponents: totalWidth=" + totalWidth + ", totalHeight=" + totalHeight);
    }

    if (pass == Pass.Calculate)
    {
      allocatedWidths = new ArrayList<Integer>();
      allocatedHeights = new ArrayList<Integer>();
      allocatedWidth = 0;
      allocatedHeight = 0;
    }

    for (Component component : container.getItems())
    {
      int width = 0;
      int height = 0;

      Constraint constraint = (Constraint)getLayoutData(component);
      if (constraint == null)
      {
        constraint = defaultConstraint;
      }

      if (!constraint.isSameRow())
      {
        if (pass == Pass.Calculate && row != -1)
        {
          allocatedWidths.add(x);
          allocatedWidth = Math.max(allocatedWidth, x);
          allocatedHeights.add(maxHeight);
          maxHeight = 0;
        }
        x = 0;
        y += deltaY;
        deltaY = 0;
        row++;
      }

      if (constraint.getWidth() == 0)
      {
        width = getWidth(component);
      }
      else if (constraint.getWidth() < 0 && constraint.getWidth() >= -1)
      {
        if (pass == Pass.Position)
        {
          width = (int)((totalWidth - allocatedWidths.get(row)) * -constraint.getWidth());
          width -= constraint.getLeft() + constraint.getRight();
        }
      }
      else if (constraint.getWidth() > 0 && constraint.getWidth() <= 1)
      {
        width = (int)(totalWidth * constraint.getWidth());
        width -= constraint.getLeft() + constraint.getRight();
      }
      else
      {
        width = (int)constraint.getWidth();
      }

      if (constraint.getHeight() == 0)
      {
        height = getHeight(component);
      }
      else if (constraint.getHeight() < 0 && constraint.getHeight() >= -1)
      {
        if (pass == Pass.Position)
        {
          height = (int)((totalHeight - allocatedHeight) * -constraint.getHeight());
          height -= constraint.getTop() + constraint.getBottom();
        }
      }
      else if (constraint.getHeight() > 0 && constraint.getHeight() <= 1)
      {
        height = (int)(totalHeight * constraint.getHeight());
        height -= constraint.getTop() + constraint.getBottom();
      }
      else
      {
        height = (int)constraint.getHeight();
      }

      int top = y + constraint.getTop();
      int left = x + constraint.getLeft();

      if (pass == Pass.Position)
      {
        if (row == -1)
        {
          // Caller specified s or s=y for first component, so default to "next" (or "last") row
          row = 0;
        }
        int rowHeight = allocatedHeights.get(row);
        setBounds(component, constraint, width, height, top, left, rowHeight);
      }

      x += constraint.getLeft() + width + constraint.getRight();
      deltaY = Math.max(deltaY, constraint.getTop() + height + constraint.getBottom());
      maxHeight = Math.max(maxHeight, height);
    }

    if (pass == Pass.Calculate)
    {
      allocatedWidths.add(x);
      allocatedWidth = Math.max(allocatedWidth, x);
      allocatedHeights.add(maxHeight);
      allocatedHeight = y + deltaY;
    }
  }

  protected void setBounds(Component component, Constraint constraint, int width, int height, int top, int left, int allocatedHeight)
  {
    int deltaLeft = 0;
    int deltaTop = 0;
    int deltaWidth = 0;
    int deltaHeight = 0;
    int offsetWidth;
    int offsetHeight;
    switch (constraint.getHorizontalAlignment())
    {
      case LEFT:
        offsetWidth = getWidth(component);
        deltaWidth = width - offsetWidth;
        deltaLeft = 0;
        break;
      case CENTER:
        offsetWidth = getWidth(component);
        deltaWidth = width - offsetWidth;
        deltaLeft = deltaWidth / 2;
        break;
      case RIGHT:
        offsetWidth = getWidth(component);
        deltaWidth = width - offsetWidth;
        deltaLeft = deltaWidth;
        break;
    }
    switch (constraint.getVerticalAlignment())
    {
      case TOP:
        offsetHeight = getHeight(component);
        deltaHeight = allocatedHeight - offsetHeight;
        deltaTop = 0;
        break;
      case MIDDLE:
        offsetHeight = getHeight(component);
        deltaHeight = allocatedHeight - offsetHeight;
        deltaTop = deltaHeight / 2;
        break;
      case BOTTOM:
        offsetHeight = getHeight(component);
        deltaHeight = allocatedHeight - offsetHeight;
        deltaTop = deltaHeight;
        break;
    }
    component.getElement().getStyle().setPosition(Position.ABSOLUTE);
    setPosition(component, (int)left + deltaLeft, (int)top + deltaTop);
    setSize(component, (int)width - deltaWidth, (int)height - deltaHeight);

    if (isDebug)
    {
      System.out.println("ConstrainedLayout.setBounds: left=" + (left + deltaLeft) + ", top=" + (top + deltaTop) + ", width=" + (width - deltaWidth) + ", height=" + (height - deltaHeight) + ", component=" + component.getClass().getName());
    }
  }

  public void setDebug(boolean isDebug)
  {
    this.isDebug = isDebug;
  }

  public final void setDefaultConstraint(Constraint defaultConstraint)
  {
    this.defaultConstraint = defaultConstraint;
  }

  public enum Pass
  {
    Calculate, Position
  }

}
