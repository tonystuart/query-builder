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

import com.example.querybuilder.extgwt.tools.layout.constrained.ConstrainedLayoutContainer;
import com.example.querybuilder.extgwt.tools.layout.constrained.Constraint;
import com.example.querybuilder.shared.ArrayModelData;
import com.example.querybuilder.shared.ColumnDescriptor;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class DetailsWindow extends Window
{
  private RowDetailsPanel rowDetailsPanel = new RowDetailsPanel();

  public DetailsWindow(String heading, List<ColumnDescriptor> columnDescriptors, ArrayModelData arrayModelData)
  {
    setHeading(heading);
    rowDetailsPanel.display(columnDescriptors, arrayModelData);
    show();
    toFront();
    focus();
  }

  @Override
  protected void onRender(Element parent, int index)
  {
    super.onRender(parent, index);
    setLayout(new FitLayout());
    add(rowDetailsPanel);
    setSize(400, 400);
  }

  private final class RowDetailsPanel extends ConstrainedLayoutContainer
  {
    private Html html;

    public RowDetailsPanel()
    {
      html = new Html();
      html.setStyleAttribute("overflow", "auto");
    }

    public void display(List<ColumnDescriptor> columnDescriptors, ArrayModelData arrayModelData)
    {
      int offset = 0;
      String[] values = arrayModelData.getData();
      StringBuilder s = new StringBuilder();
      s.append("<table class='row-details'>\n");
      for (ColumnDescriptor columnDescriptor : columnDescriptors)
      {
        String name = columnDescriptor.getColumnName();
        String value = values[offset++];
        s.append("<tr><td class='row-details-name'>");
        s.append(name);
        s.append("</td><td class='row-details-value'>");
        s.append(value);
        s.append("</td></tr>\n");
      }
      s.append("</table>\n");
      html.setHtml(s.toString());
    }

    @Override
    protected void onRender(Element parent, int index)
    {
      super.onRender(parent, index);
      addStyleName("dm-form-font"); // Let the layout know what font we're using
      add(html, new Constraint("w=1,h=1"));
    }
  }

}
