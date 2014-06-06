// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.example.querybuilder.extgwt.tools.layout.constrained.ConstrainedLayoutContainer;
import com.example.querybuilder.extgwt.tools.layout.constrained.Constraint;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.SortDirection;
import com.example.querybuilder.shared.SummaryOperation;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ColumnEditorPanel extends ConstrainedLayoutContainer implements CommandProcessor
{
  private static final String APPLY = "Apply";
  private static final String OK = "OK";
  private static final String REVERT = "Revert";

  private Button applyButton;
  private TextField<String> columnAliasTextField;
  private TextField<String> columnNameTextField;
  private CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
  private ModelData modelData;
  private Button okayButton;
  private Button revertButton;
  private EnumCellEditorComboBox<SortDirection> sortComboBox;
  private EnumCellEditorComboBox<SummaryOperation> summaryOperationComboBox;
  private TextField<String> tableAliasTextField;

  public ColumnEditorPanel()
  {
    addStyleName("dm-form-font"); // Let the layout know what font we're using
    CollapseListener collapseListener = new CollapseListener();

    add(new Label("Table Alias:"), new Constraint("t=5,l=5"));
    tableAliasTextField = new TextField<String>();
    tableAliasTextField.setEnabled(false);
    add(tableAliasTextField, new Constraint("w=1,t=1,l=5,r=5"));

    add(new Label("Column Name:"), new Constraint("t=5,l=5"));
    columnNameTextField = new TextField<String>();
    columnNameTextField.setEnabled(false);
    add(columnNameTextField, new Constraint("w=1,t=1,l=5,r=5"));

    add(new Label("Column Alias:"), new Constraint("t=5,l=5"));
    columnAliasTextField = new TextField<String>();
    columnAliasTextField.addKeyListener(new TextAreaKeyListener());
    add(columnAliasTextField, new Constraint("w=1,t=1,l=5,r=5"));

    add(new Label("Sort Direction:"), new Constraint("t=5,l=5"));
    sortComboBox = new EnumCellEditorComboBox<SortDirection>(SortDirection.values());
    sortComboBox.addListener(Events.Collapse, collapseListener);
    add(sortComboBox, new Constraint("w=1,t=1,l=5,r=5"));

    add(new Label("Summary Operation:"), new Constraint("t=5,l=5"));
    summaryOperationComboBox = new EnumCellEditorComboBox<SummaryOperation>(SummaryOperation.values());
    summaryOperationComboBox.addListener(Events.Collapse, collapseListener);
    add(summaryOperationComboBox, new Constraint("w=1,t=1,l=5,r=5"));
  }

  public void apply()
  {
    saveModelData();
  }

  public void configure(ModelData modelData)
  {
    this.modelData = modelData;
    loadModelData();
  }

  public ToolBar createToolBar()
  {
    ToolBar toolBar = new ToolBar();
    toolBar.setAlignment(HorizontalAlignment.RIGHT);

    revertButton = new Button(REVERT, commandListener);
    revertButton.setToolTip("Discard changes and start over");
    revertButton.setEnabled(false);
    toolBar.add(revertButton);

    applyButton = new Button(APPLY, commandListener);
    applyButton.setToolTip("Apply changes to column");
    applyButton.setEnabled(false);
    toolBar.add(applyButton);

    okayButton = new Button(OK, commandListener);
    okayButton.setToolTip("Apply changes and close");
    okayButton.setEnabled(false);
    toolBar.add(okayButton);

    return toolBar;
  }

  public void display(ModelData modelData)
  {
    if (this.modelData != modelData)
    {
      if (this.modelData != null && isUnsavedChanges())
      {
        promptToSaveChanges(modelData);
      }
      else
      {
        configure(modelData);
      }
    }
  }

  public Widget getFocusWidget()
  {
    return columnAliasTextField;
  }

  private boolean isUnsavedChanges()
  {
    ModelData modelData = new BaseModelData();
    saveModelData(modelData);
    return !Utilities.equals(modelData, this.modelData, Keys.COLUMN_ALIAS, Keys.SORT_DIRECTION, Keys.SUMMARY_OPERATION);
  }

  private void loadModelData()
  {
    String tableAlias = modelData.get(Keys.TABLE_ALIAS);
    String columnName = modelData.get(Keys.COLUMN_NAME);
    String columnAlias = modelData.get(Keys.COLUMN_ALIAS);
    SortDirection sortDirection = modelData.get(Keys.SORT_DIRECTION);
    SummaryOperation summaryOperation = modelData.get(Keys.SUMMARY_OPERATION);

    tableAliasTextField.setValue(tableAlias);
    columnAliasTextField.setValue(columnAlias);
    columnNameTextField.setValue(columnName);
    sortComboBox.select(sortDirection);
    summaryOperationComboBox.select(summaryOperation);
  }

  public void okay()
  {
    apply();
    Utilities.getWindow(this).hide();
  }

  @Override
  public void onCommand(String command, AbstractImagePrototype icon)
  {
    if (command.equals(REVERT))
    {
      loadModelData();
    }
    else if (command.equals(APPLY))
    {
      apply();
    }
    else if (command.equals(OK))
    {
      okay();
    }
    updateFormState();
  }

  public void onTextAreaKeyListener(ComponentEvent event)
  {
    updateFormState();
    int keyCode = event.getKeyCode();
    if (keyCode == KeyCodes.KEY_ENTER)
    {
      okay();
    }
  }

  private void promptToSaveChanges(final ModelData modelData)
  {
    MessageBox box = new MessageBox();
    box.setButtons(MessageBox.YESNOCANCEL);
    box.setIcon(MessageBox.QUESTION);
    box.setTitle("Save Changes?");
    box.setMessage("Would you like to save your changes to the column?");
    box.addCallback(new Listener<MessageBoxEvent>()
    {
      @Override
      public void handleEvent(MessageBoxEvent be)
      {
        Button buttonClicked = be.getButtonClicked();
        String text = buttonClicked.getText();
        System.out.println("ColumnEditorPanel: text=" + text);
        if (text.equals("Yes"))
        {
          saveModelData();
          configure(modelData);
        }
        else if (text.equals("No"))
        {
          configure(modelData);
        }
      }
    });
    box.show();
  }

  protected void saveModelData()
  {
    saveModelData(modelData);
  }

  public void saveModelData(ModelData modelData)
  {
    String tableAlias = tableAliasTextField.getValue();
    String columnName = columnNameTextField.getValue();
    String columnAlias = columnAliasTextField.getValue();
    SortDirection sortDirection = sortComboBox.getValue().getValue();
    SummaryOperation summaryOperation = summaryOperationComboBox.getValue().getValue();

    modelData.set(Keys.TABLE_ALIAS, tableAlias);
    modelData.set(Keys.COLUMN_ALIAS, columnAlias);
    modelData.set(Keys.COLUMN_NAME, columnName);
    modelData.set(Keys.SORT_DIRECTION, sortDirection);
    modelData.set(Keys.SUMMARY_OPERATION, summaryOperation);
  }

  public void updateFormState()
  {
    boolean isUnsavedChanges = isUnsavedChanges();
    revertButton.setEnabled(isUnsavedChanges);
    applyButton.setEnabled(isUnsavedChanges);
    okayButton.setEnabled(isUnsavedChanges);
  }

  public class CollapseListener implements Listener<FieldEvent>
  {
    @Override
    public void handleEvent(FieldEvent be)
    {
      updateFormState();
    }
  }

  private final class TextAreaKeyListener extends KeyListener
  {
    @Override
    public void componentKeyUp(ComponentEvent event)
    {
      super.componentKeyUp(event);
      onTextAreaKeyListener(event);
    }

  }
}
