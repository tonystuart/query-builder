// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.extgwt.tools.layout.constrained;

import com.extjs.gxt.ui.client.widget.layout.LayoutData;

public class Constraint extends LayoutData
{
  public enum HorizontalAlignment
  {
    NONE, LEFT, CENTER, RIGHT,
  }

  public enum VerticalAlignment
  {
    NONE, TOP, MIDDLE, BOTTOM,
  }

  private boolean isSameRow;
  private double width;
  private double height;
  private int top;
  private int right;
  private int bottom;
  private int left;
  private HorizontalAlignment horizontalAlignment = HorizontalAlignment.NONE;
  private VerticalAlignment verticalAlignment = VerticalAlignment.NONE;

  /**
   * Creates a Constraint object using a specification string. The specification
   * string contains one or more single valued flags or name=value pairs,
   * separated by commas. The default values are:
   * 
   * <pre>
   * &quot;s=n,w=0,h=0,t=0,r=0,b=0,l=0,H=n,V=n&quot;
   * </pre>
   * 
   * Where:
   * <ul>
   * <li>s = same row (may be specified as a single valued flag, meaning s=y)
   * <ul>
   * <li>n = no (next row)</li>
   * <li>y = yes (same row)</li>
   * </ul>
   * <li>w = width and h = height</li>
   * <ul>
   * <li>percentage of remaining space: -1 <= value < 0</li>
   * <li>natural width or height: value = 0</li>
   * <li>percentage of total space: 0 < value <= 1</li>
   * <li>size in pixels: value > 1</li>
   * </ul>
   * <li>t, r, b, l = top, right, bottom, left margins</li> <li>H = horizontal
   * alignment if allocated width is greater than natural width</li>
   * <ul>
   * <li>l = left</li>
   * <li>c = center</li>
   * <li>r = right</li>
   * <li>n = none</li>
   * </ul>
   * <li>V = vertical alignment if allocated height is greater than natural
   * height</li>
   * <ul>
   * <li>t = top</li>
   * <li>m = middle</li>
   * <li>b = bottom</li>
   * <li>n = none</li>
   * </ul>
   * </ul>
   * 
   * @param layoutSpecification
   *          specification string
   */
  public Constraint(String layoutSpecification)
  {
    setLayout(layoutSpecification);
  }

  public Constraint(boolean isSameRow, double width, double height, int top, int right, int bottom, int left)
  {
    this(isSameRow, width, height, top, right, bottom, left, HorizontalAlignment.NONE, VerticalAlignment.NONE);
  }

  public Constraint(boolean isSameRow, double width, double height, int top, int right, int bottom, int left, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment)
  {
    this.isSameRow = isSameRow;
    this.width = width;
    this.height = height;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
    this.horizontalAlignment = horizontalAlignment;
    this.verticalAlignment = verticalAlignment;
  }

  public Constraint()
  {
  }

  public final void setLayout(String layoutSpecification)
  {
    if (layoutSpecification != null && layoutSpecification.length() > 0)
    {
      String[] tokens = layoutSpecification.split(",");
      for (String token : tokens)
      {
        String[] pair = token.split("=");
        String name = pair[0];
        char nameChar = name.charAt(0);
        if (pair.length == 1)
        {
          switch (nameChar)
          {
            case 's':
              isSameRow = true;
              break;
          }
        }
        if (pair.length == 2)
        {
          String value = pair[1];
          char valueChar = value.charAt(0);
          switch (nameChar)
          {
            case 's':
              isSameRow = value.equals("y");
              break;
            case 'w':
              width = Double.parseDouble(value);
              break;
            case 'h':
              height = Double.parseDouble(value);
              break;
            case 't':
              top = Integer.parseInt(value);
              break;
            case 'r':
              right = Integer.parseInt(value);
              break;
            case 'b':
              bottom = Integer.parseInt(value);
              break;
            case 'l':
              left = Integer.parseInt(value);
              break;
            case 'H':
              setHorizontalAlignment(valueChar);
              break;
            case 'V':
              setVerticalAlignment(valueChar);
              break;
          }
        }
      }
    }
  }

  public final void setHorizontalAlignment(char valueChar)
  {
    switch (valueChar)
    {
      case 'l':
        horizontalAlignment = HorizontalAlignment.LEFT;
        break;
      case 'c':
        horizontalAlignment = HorizontalAlignment.CENTER;
        break;
      case 'r':
        horizontalAlignment = HorizontalAlignment.RIGHT;
        break;
      case 'n':
        horizontalAlignment = HorizontalAlignment.NONE;
        break;
    }
  }

  public final void setVerticalAlignment(char valueChar)
  {
    switch (valueChar)
    {
      case 't':
        verticalAlignment = VerticalAlignment.TOP;
        break;
      case 'm':
        verticalAlignment = VerticalAlignment.MIDDLE;
        break;
      case 'b':
        verticalAlignment = VerticalAlignment.BOTTOM;
        break;
      case 'n':
        verticalAlignment = VerticalAlignment.NONE;
        break;
    }
  }

  public boolean isSameRow()
  {
    return isSameRow;
  }

  public void setSameRow(boolean isSameRow)
  {
    this.isSameRow = isSameRow;
  }

  public final void setWidth(double width)
  {
    this.width = width;
  }

  public final double getWidth()
  {
    return width;
  }

  public final void setHeight(double height)
  {
    this.height = height;
  }

  public final double getHeight()
  {
    return height;
  }

  public final void setTop(int top)
  {
    this.top = top;
  }

  public final int getTop()
  {
    return top;
  }

  public final void setRight(int right)
  {
    this.right = right;
  }

  public final int getRight()
  {
    return right;
  }

  public final void setBottom(int bottom)
  {
    this.bottom = bottom;
  }

  public final int getBottom()
  {
    return bottom;
  }

  public final void setLeft(int left)
  {
    this.left = left;
  }

  public final int getLeft()
  {
    return left;
  }

  public final HorizontalAlignment getHorizontalAlignment()
  {
    return horizontalAlignment;
  }

  public final void setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
  {
    this.horizontalAlignment = horizontalAlignment;
  }

  public final VerticalAlignment getVerticalAlignment()
  {
    return verticalAlignment;
  }

  public final void setVerticalAlignment(VerticalAlignment verticalAlignment)
  {
    this.verticalAlignment = verticalAlignment;
  }

}
