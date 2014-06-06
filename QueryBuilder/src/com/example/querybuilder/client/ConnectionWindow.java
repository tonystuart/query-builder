// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.example.querybuilder.client.QueryBuilderServiceBus.DisplayConnectionWindow;
import com.example.querybuilder.client.QueryBuilderServiceBus.GetMasterTreeResults;
import com.example.querybuilder.client.ServiceBus.ServiceProvider;
import com.example.querybuilder.client.ServiceBus.ServiceRequest;
import com.example.querybuilder.extgwt.tools.layout.constrained.ConstrainedLayoutContainer;
import com.example.querybuilder.extgwt.tools.layout.constrained.Constraint;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ConnectionWindow extends Window
{
  private ConnectionPanel connectionPanel;

  public ConnectionWindow()
  {
    setHeading("Create Connection");
    setIcon(Resources.DATABASE_CONNECT);
    setLayout(new FitLayout());
    connectionPanel = new ConnectionPanel();
    add(connectionPanel);
    ToolBar toolBar = connectionPanel.createToolBar();
    setBottomComponent(toolBar);
    connectionPanel.updateFormState();
    setSize(400, 350);
    QueryBuilderServiceBus.addServiceProvider(new ConnectionWindowServiceProvider());
  }

  public void activate()
  {
    show();
    toFront();
    setFocusWidget(connectionPanel.getFocusWidget());
    focus();
  }

  public class ConnectionPanel extends ConstrainedLayoutContainer implements CommandProcessor
  {
    private static final String CLEAR = "Clear";
    private static final String CREATE = "Create";
    private static final String OKAY = "Okay";

    private Button clearButton;
    private CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
    private TextField<String> connectionNameTextField;
    private Button createButton;
    private TextArea descriptionTextArea;
    private Button okayButton;
    private TextField<String> passwordTextField;
    private TextField<String> urlTextField;
    private TextField<String> userIdTextField;

    public ConnectionPanel()
    {
      addStyleName("dm-form-font"); // Let ConstrainedLayoutContainer know what font we're using

      TextAreaKeyListener enterKeyListener = new TextAreaKeyListener(false);

      add(new Label("Database JDBC URL:"), new Constraint("w=1,t=10,l=5,r=5"));
      urlTextField = new TextField<String>();
      urlTextField.setValue("jdbc:derby://localhost:1527/");
      urlTextField.addListener(Events.Change, new UrlChangeListener());
      urlTextField.addKeyListener(new TextAreaKeyListener(true));
      add(urlTextField, new Constraint("w=1,l=5,r=5"));

      add(new Label("User Defined Connection Name:"), new Constraint("w=1,t=10,l=5,r=5"));
      connectionNameTextField = new TextField<String>();
      connectionNameTextField.addKeyListener(enterKeyListener);
      add(connectionNameTextField, new Constraint("w=1,l=5,r=5"));

      add(new Label("Connection User ID:"), new Constraint("w=1,t=10,l=5,r=5"));
      userIdTextField = new TextField<String>();
      userIdTextField.addKeyListener(enterKeyListener);
      add(userIdTextField, new Constraint("w=1,l=5,r=5"));

      add(new Label("Connection Password:"), new Constraint("w=1,t=10,l=5,r=5"));
      passwordTextField = new TextField<String>();
      passwordTextField.setPassword(true);
      passwordTextField.addKeyListener(enterKeyListener);
      add(passwordTextField, new Constraint("w=1,l=5,r=5"));

      add(new Label("Description:"), new Constraint("w=1,t=10,l=5,r=5"));
      descriptionTextArea = new TextArea();
      add(descriptionTextArea, new Constraint("w=1,h=-1,l=5,r=5,b=5"));
    }

    private void clear()
    {
      connectionNameTextField.clear();
      urlTextField.clear();
      userIdTextField.clear();
      passwordTextField.clear();
      descriptionTextArea.clear();
      updateFormState();
    }

    private String convertUrlToName(String url)
    {
      if (url != null)
      {
        int lastOffset = url.length() - 1;
        for (int currentOffset = lastOffset; currentOffset >= 0; currentOffset--)
        {
          char c = url.charAt(currentOffset);
          if (c == '/' || c == ':')
          {
            String name = url.substring(currentOffset + 1, lastOffset + 1).toUpperCase();
            return name;
          }
          else if (c == ';')
          {
            lastOffset = currentOffset;
          }
        }
      }
      return "";
    }

    private void copyUrlToName()
    {
      String url = urlTextField.getValue();
      String name = convertUrlToName(url);
      connectionNameTextField.setValue(name);
    }

    private void createConnection(final boolean isCloseConnectionWindowOnSuccess)
    {
      final String connectionName = connectionNameTextField.getValue();
      String url = urlTextField.getValue();
      String userId = userIdTextField.getValue();
      String password = passwordTextField.getValue();
      String description = descriptionTextArea.getValue();
      QueryBuilderServiceBus.setStatusBusy("Connecting to " + url + "...");
      // Do *NOT* factor this out into something that is far more complex and inefficient!
      QueryBuilder.queryBuilderService.createConnection(connectionName, url, userId, password, description, new ClearStatusCallback<TreeModel>()
      {
        @Override
        protected void process(TreeModel masterTree)
        {
          if (isCloseConnectionWindowOnSuccess)
          {
            ConnectionWindow.this.hide();
          }
          QueryBuilderServiceBus.getMasterTreeResults(masterTree);
          QueryBuilderServiceBus.selectConnection(connectionName);
        }
      });
    }

    public ToolBar createToolBar()
    {
      ToolBar toolBar = new ToolBar();

      toolBar.add(new FillToolItem());

      clearButton = new Button(CLEAR, commandListener);
      toolBar.add(clearButton);

      createButton = new Button(CREATE, commandListener);
      toolBar.add(createButton);

      okayButton = new Button(OKAY, commandListener);
      toolBar.add(okayButton);

      return toolBar;
    }

    public Component getFocusWidget()
    {
      return urlTextField;
    }

    private boolean isSet(String url)
    {
      return url != null && !url.isEmpty();
    }

    @Override
    public void onCommand(String command, AbstractImagePrototype icon)
    {
      if (command.equals(CLEAR))
      {
        clear();
      }
      else if (command.equals(CREATE))
      {
        createConnection(false);
      }
      else if (command.equals(OKAY))
      {
        createConnection(true);
      }
    }

    public void onFieldKeyUp(ComponentEvent event, boolean isCopyUrlToName)
    {
      if (isCopyUrlToName)
      {
        copyUrlToName();
      }
      updateFormState();
      int keyCode = event.getKeyCode();
      if (keyCode == KeyCodes.KEY_ENTER)
      {
        createConnection(true);
      }
    }

    public void onUrlChange()
    {
      copyUrlToName();
      updateFormState();
    }

    public void updateFormState()
    {
      boolean isSetUrl = isSet(urlTextField.getValue());
      boolean isSetConnectionName = isSet(connectionNameTextField.getValue());
      boolean isSetUserId = isSet(userIdTextField.getValue());
      boolean isSetPassword = isSet(passwordTextField.getValue());

      clearButton.setEnabled(isSetUrl || isSetConnectionName || isSetUserId || isSetPassword);
      createButton.setEnabled(isSetUrl && isSetConnectionName);
    }

    private final class TextAreaKeyListener extends KeyListener
    {
      private boolean isCopyUrlToName;

      private TextAreaKeyListener(boolean isCopyUrlToName)
      {
        this.isCopyUrlToName = isCopyUrlToName;
      }

      @Override
      public void componentKeyUp(ComponentEvent event)
      {
        super.componentKeyUp(event);
        onFieldKeyUp(event, isCopyUrlToName);
      }
    }

    private final class UrlChangeListener implements Listener<FieldEvent>
    {
      @Override
      public void handleEvent(FieldEvent be)
      {
        onUrlChange();
      }

    }
  }

  public class ConnectionWindowServiceProvider implements ServiceProvider
  {
    @Override
    public void onServiceRequest(ServiceRequest serviceRequest)
    {
      if (serviceRequest instanceof DisplayConnectionWindow)
      {
        activate();
      }
      else if (serviceRequest instanceof GetMasterTreeResults)
      {
        GetMasterTreeResults getMasterTreeResults = (GetMasterTreeResults)serviceRequest;
        TreeModel connectionTree = getMasterTreeResults.getConnectionTree();
        if (connectionTree.getChildCount() == 0)
        {
          activate();
        }

      }
    }
  }

}
