// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.example.querybuilder.shared.Keys;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

/**
 * A versatile ComboBox that can be used as either a regular ComboBox
 * or in an EditorGrid.
 */
public class ComboBoxCellEditor<T extends ModelData> extends ComboBox<T>
{
  private CellEditor cellEditor;
  private boolean doExpand;
  private String id = Keys.NAME;

  public ComboBoxCellEditor()
  {
    this(Keys.NAME);
  }

  public ComboBoxCellEditor(String displayField)
  {
    setDisplayField(displayField);
    setEditable(false);
    setForceSelection(true);
    setTriggerAction(TriggerAction.ALL);
    addListener(Events.Collapse, new CollapseListener());
  }

  @Override
  public void focus()
  {
    super.focus();
    if (doExpand)
    {
      expand();
      doExpand = false;
    }
  }

  public CellEditor getCellEditor()
  {
    if (cellEditor == null)
    {
      cellEditor = new DefaultCellEditor(this);
    }
    return cellEditor;
  }

  public String getId()
  {
    return id;
  }

  public Object postProcess(Object value)
  {
    if (value == null)
    {
      return null;
    }
    return ((ModelData)value).get(id);
  }

  public Object preProcess(Object value)
  {
    doExpand = true;
    if (value == null)
    {
      return null;
    }
    return findModel(id, value.toString());
  }

  public void setId(String id)
  {
    this.id = id;
  }

  @Override
  public void setStore(ListStore<T> listStore)
  {
    super.setStore(listStore);
  }

  public class CollapseListener implements Listener<FieldEvent>
  {
    @Override
    public void handleEvent(FieldEvent be)
    {
      if (cellEditor != null)
      {
        cellEditor.completeEdit();
      }
    }
  }

  private final class DefaultCellEditor extends CellEditor
  {
    private DefaultCellEditor(Field<? extends Object> field)
    {
      super(field);
    }

    @Override
    public Object postProcessValue(Object value)
    {
      return postProcess(value);
    }

    @Override
    public Object preProcessValue(Object value)
    {
      return preProcess(value);
    }
  }

}
