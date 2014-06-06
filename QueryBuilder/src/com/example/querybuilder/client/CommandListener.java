// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class CommandListener<E extends ComponentEvent> extends SelectionListener<E>
{
  private CommandProcessor commandProcessor;

  public CommandListener(CommandProcessor commandProcessor)
  {
    this.commandProcessor = commandProcessor;
  }

  @Override
  public void componentSelected(E event)
  {
    if (event instanceof ButtonEvent)
    {
      ButtonEvent buttonEvent = (ButtonEvent)event;
      Button button = buttonEvent.getButton();
      if (button != null)
      {
        String command = button.getText();
        if (command == null)
        {
          command = "";
        }
        AbstractImagePrototype icon = button.getIcon();
        commandProcessor.onCommand(command, icon);
      }
    }
    else if (event instanceof MenuEvent)
    {
      MenuEvent menuEvent = (MenuEvent)event;
      Component item = menuEvent.getItem();
      if (item instanceof MenuItem)
      {
        MenuItem menuItem = (MenuItem)item;
        String command = menuItem.getText();
        AbstractImagePrototype icon = menuItem.getIcon();
        commandProcessor.onCommand(command, icon);
      }
    }
  }

}
