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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class FormulaBuilderWindow extends Window implements CommandProcessor
{
  private static final String COMMAND_APPLY = "Apply";
  private static final String COMMAND_CANCEL = "Cancel";
  private static final String COMMAND_OKAY = "Okay";

  private Callback callback;
  protected CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
  private int displayCount;
  private ExpressionPanel expressionPanel;
  private boolean isInitialized;

  public FormulaBuilderWindow(String heading)
  {
    setHeading(heading);
    setIcon(Resources.CALCULATOR_ADD);
    setBodyBorder(false);
    setLayout(new FitLayout());
    expressionPanel = new ExpressionPanel("Drag columns from tables to build formula here.");
    add(expressionPanel);
    ToolBar topToolBar = createTopToolBar();
    setTopComponent(topToolBar);
    ToolBar bottomToolBar = createBottomToolBar();
    setBottomComponent(bottomToolBar);
    setSize(400, 175);
  }

  public void activate()
  {
    show();
    toFront();
    focus();
  }

  public void addColumns(List<ModelData> columns)
  {
    expressionPanel.addColumns(columns);
  }

  public ToolBar createBottomToolBar()
  {
    ToolBar toolBar = new ToolBar();

    toolBar.add(new FillToolItem());

    Button button = new Button(COMMAND_APPLY, commandListener);
    button.setToolTip("Save formula");
    toolBar.add(button);

    button = new Button(COMMAND_CANCEL, commandListener);
    button.setToolTip("Close without saving");
    toolBar.add(button);

    button = new Button(COMMAND_OKAY, commandListener);
    button.setToolTip("Save formula and close");
    toolBar.add(button);

    return toolBar;
  }

  public ToolBar createTopToolBar()
  {
    ToolBar toolBar = new ToolBar();

    Button button = new Button("+", commandListener);
    button.setToolTip("Add two values: a + 1");
    toolBar.add(button);

    button = new Button("-", commandListener);
    button.setToolTip("Subtract two values: a - 1");
    toolBar.add(button);

    button = new Button("*", commandListener);
    button.setToolTip("Multiply two values: a * 2");
    toolBar.add(button);

    button = new Button("/", commandListener);
    button.setToolTip("Divide two values: a / 2");
    toolBar.add(button);

    button = new Button("(", commandListener);
    button.setToolTip("Begin group of terms of higher precedence: (a + 1) * (b - 2)");
    toolBar.add(button);

    button = new Button(")", commandListener);
    button.setToolTip("End group of terms of higher precedence: (a + 1) * (b - 2)");
    toolBar.add(button);

    toolBar.add(new FillToolItem());

    toolBar.add(expressionPanel.getFunctionComboBox());

    return toolBar;
  }

  public int display(Component component, String formula, Callback callback)
  {
    if (!isInitialized)
    {
      setPosition(component.getAbsoluteLeft() + 50, component.getAbsoluteTop() + 50);
      isInitialized = true;
    }
    activate();
    display(formula, callback);
    return displayCount++;
  }

  public void display(String formula, Callback callback)
  {
    expressionPanel.setExpression(formula);
    this.callback = callback;
  }

  private void onApply()
  {
    String formula = expressionPanel.getExpression();
    callback.onSave(formula);
  }

  private void onCancel()
  {
    Utilities.closeWindow(this);
  }

  @Override
  public void onCommand(String command, AbstractImagePrototype icon)
  {
    if (command.equals(COMMAND_APPLY))
    {
      onApply();
    }
    else if (command.equals(COMMAND_CANCEL))
    {
      onCancel();
    }
    else if (command.equals(COMMAND_OKAY))
    {
      onOkay();
    }
    else
    {
      expressionPanel.insertText(command);
    }
  }

  private void onOkay()
  {
    onApply();
    onCancel();
  }

  public static interface Callback
  {
    public void onSave(String formula);
  }

}
