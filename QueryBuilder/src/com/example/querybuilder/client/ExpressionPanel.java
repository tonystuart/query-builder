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

import com.example.querybuilder.shared.Functions;
import com.example.querybuilder.shared.Keys;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Scroll;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class ExpressionPanel extends LayoutContainer
{
  private static final String FORMAT_FUNCTION_ARGUMENT_LIST = " (  )";
  private static final String FORMAT_READABILITY_SPACING = " ";
  private static final String FORMAT_SQL92_IDENTIFIER_QUOTE = "\""; // see http://db.apache.org/derby/docs/10.6/ref/crefsqlj34834.html#crefsqlj34834

  protected PositionRetainingTextArea formulaTextArea;
  protected EnumCellEditorComboBox<Functions> functionComboBox;

  public ExpressionPanel(String emptyText)
  {
    setLayout(new FitLayout());
    formulaTextArea = new PositionRetainingTextArea();
    formulaTextArea.setEmptyText(emptyText);
    new TextAreaDropTarget(formulaTextArea);
    add(formulaTextArea);

    functionComboBox = new EnumCellEditorComboBox<Functions>(Functions.values());
    functionComboBox.addListener(Events.Collapse, new FunctionCollapseListener());
    functionComboBox.select(Functions.values()[0]);
  }

  public void addColumns(List<ModelData> columns)
  {
    for (ModelData column : columns)
    {
      String tableAlias = column.get(Keys.TABLE_ALIAS);
      String columnName = column.get(Keys.COLUMN_NAME);
      String qualifiedColumnName = tableAlias + "." + columnName;
      insertColumn(qualifiedColumnName);
    }
  }

  public String getExpression()
  {
    return formulaTextArea.getValue();
  }

  public PositionRetainingTextArea getFormulaTextArea()
  {
    return formulaTextArea;
  }

  public EnumCellEditorComboBox<Functions> getFunctionComboBox()
  {
    return functionComboBox;
  }

  private void insertColumn(String name)
  {
    if (requiresQuotes(name))
    {
      name = FORMAT_SQL92_IDENTIFIER_QUOTE + name + FORMAT_SQL92_IDENTIFIER_QUOTE;
    }
    insertText(name);
  }

  private void insertFunction()
  {
    ModelData selectedItem = functionComboBox.getValue();
    if (selectedItem != null)
    {
      String name = selectedItem.get(Keys.NAME) + FORMAT_FUNCTION_ARGUMENT_LIST;
      insertText(name, 3);
    }
    DeferredCommand.addCommand(new Command()
    {
      @Override
      public void execute()
      {
        formulaTextArea.focus();
      }
    });
  }

  public void insertText(String text)
  {
    insertText(text, 0);
  }

  private void insertText(String text, int rightCursorOffset)
  {
    String formattedText = FORMAT_READABILITY_SPACING + text + FORMAT_READABILITY_SPACING;
    formulaTextArea.insertAtCursor(formattedText, true, rightCursorOffset);
  }

  private boolean requiresQuotes(String name)
  {
    int length = name.length();
    for (int offset = 0; offset < length; offset++)
    {
      char c = name.charAt(offset);
      if (Utilities.isLowerCase(c) || c == ' ')
      {
        return true;
      }
    }
    return false;
  }

  public void setExpression(String expression)
  {
    formulaTextArea.setValue(expression);
  }

  private final class FunctionCollapseListener implements Listener<BaseEvent>
  {
    @Override
    public void handleEvent(BaseEvent be)
    {
      insertFunction();
    }
  }

  public class PositionRetainingTextArea extends TextArea
  {
    public void insertAtCursor(String text, boolean isAdvanceCursor, int rightCursorOffset)
    {
      int cursorPos;
      String value = getValue();
      if (value == null)
      {
        value = "";
        cursorPos = 0;
      }
      else
      {
        cursorPos = getCursorPos();
      }
      String newValue = value.substring(0, cursorPos) + text + value.substring(cursorPos);
      El inputEl = getInputEl();
      Scroll scroll = inputEl.getScroll();
      int scrollTop = scroll.getScrollTop();
      inputEl.setValue(newValue);
      inputEl.setScrollTop(scrollTop);
      if (isAdvanceCursor)
      {
        cursorPos += text.length() - rightCursorOffset;
      }
      setCursorPos(cursorPos);
      inputEl.setFocus(true);
    }
  }

  private class TextAreaDropTarget extends DropTarget
  {
    public TextAreaDropTarget(Component target)
    {
      super(target);
    }

    @Override
    protected void onDragDrop(DNDEvent e)
    {
      List<ModelData> dropItems = e.getData();
      addColumns(dropItems);
    }
  }

}
