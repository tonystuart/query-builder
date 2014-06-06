// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.LinkedList;
import java.util.List;

import com.example.querybuilder.client.QueryBuilderServiceBus.GetMasterTreeResults;
import com.example.querybuilder.client.QueryBuilderServiceBus.SelectConnection;
import com.example.querybuilder.client.QueryBuilderServiceBus.SelectTable;
import com.example.querybuilder.client.ServiceBus.ServiceProvider;
import com.example.querybuilder.client.ServiceBus.ServiceRequest;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.NodeType;
import com.example.querybuilder.shared.TableReference;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class TableTreeGridPanel extends ContentPanel implements CommandProcessor
{
  private static final String ADD = "Add";
  private static final String DELETE = "Delete";
  private static final String DESCRIBE = "Describe";
  private static final String DISPLAY = "Display";
  private static final String IMPORT = "Import";
  private static final String REFRESH = "Refresh";
  private static final String UPDATE = "Update";

  private Button addButton;
  private CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
  private Button deleteButton;
  private Button describeButton;
  private Button displayButton;
  private Button expandButton;
  private Button importButton;
  private Button refreshButton;
  protected ExtendedTreeGrid tableTreeGrid;
  protected MergeTreeStore tableTreeStore;
  private ToggleButton timeStampButton;
  private Button updateButton;

  public TableTreeGridPanel()
  {
    setHeading("Tables");
    setIcon(Resources.FOLDER_TABLE);
    setBodyBorder(true); // ContentPanel inside TabItem requires removing top border, see onRender
    setLayout(new FitLayout());

    List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();

    ColumnConfig nameColumnConfig = new ColumnConfig(Keys.NAME, "Name", 100);
    nameColumnConfig.setRenderer(new TreeGridCellRenderer<ModelData>());
    columnConfigs.add(nameColumnConfig);

    ColumnConfig rowCountColumnConfig = new ColumnConfig(Keys.ROW_COUNT, "Rows", 65);
    rowCountColumnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnConfigs.add(rowCountColumnConfig);

    ColumnConfig columnCountColumnConfig = new ColumnConfig(Keys.COLUMN_COUNT, "Columns", 65);
    columnCountColumnConfig.setAlignment(HorizontalAlignment.RIGHT);
    columnConfigs.add(columnCountColumnConfig);

    ColumnConfig dateColumnConfig = new ColumnConfig(Keys.DATE, "Date", 75);
    dateColumnConfig.setAlignment(HorizontalAlignment.RIGHT);
    dateColumnConfig.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
    columnConfigs.add(dateColumnConfig);

    ColumnConfig timeColumnConfig = new ColumnConfig(Keys.DATE, "Time", 75);
    timeColumnConfig.setAlignment(HorizontalAlignment.RIGHT);
    timeColumnConfig.setDateTimeFormat(DateTimeFormat.getMediumTimeFormat());
    columnConfigs.add(timeColumnConfig);

    ColumnModel columnModel = new ColumnModel(columnConfigs);

    tableTreeStore = new MergeTreeStore(Keys.NAME, Keys.NODE_TYPE);
    tableTreeStore.setMonitorChanges(true);

    tableTreeGrid = new ExtendedTreeGrid(tableTreeStore, columnModel);
    tableTreeGrid.setIconProvider(NodeTypeIconProvider.getInstance());
    tableTreeGrid.setAutoExpand(true);
    tableTreeGrid.setAutoExpandColumn(Keys.NAME);
    tableTreeGrid.setAutoExpandMin(100);
    tableTreeGrid.setBorders(false);
    tableTreeGrid.getSelectionModel().addSelectionChangedListener(new TableTreeGridSelectionChangedListener());
    tableTreeGrid.addListener(Events.OnDoubleClick, new TableTreeGridDoubleClickListener());
    new TableTreeGridDragSource(tableTreeGrid);

    add(tableTreeGrid);

    ToolBar toolBar = createToolBar();
    setBottomComponent(toolBar);

    QueryBuilderServiceBus.addServiceProvider(new TableTreeGridPanelServiceProvider());
  }

  public void addTablesToDefaultQueryBuilder()
  {
    List<ModelData> selectedTables = getSelectedTables();
    QueryBuilderServiceBus.addTablesToQueryBuilder(selectedTables);
  }

  public static TableReference createTableReference(ModelData modelData)
  {
    TreeModel tableTreeModel = (TreeModel)modelData;
    TreeModel schemaTreeModel = tableTreeModel.getParent();
    TreeModel connectionTreeModel = schemaTreeModel.getParent();

    NodeType nodeType = modelData.get(Keys.NODE_TYPE);
    String tableName = tableTreeModel.get(Keys.NAME);
    String schemaName = schemaTreeModel.get(Keys.NAME);
    String connectionName = connectionTreeModel.get(Keys.NAME);

    TableReference tableReference = new TableReference();
    tableReference.setNodeType(nodeType);
    tableReference.setConnectionName(connectionName);
    tableReference.setSchemaName(schemaName);
    tableReference.setTableName(tableName);

    if (nodeType == NodeType.INTERNAL_TABLE)
    {
      int tableId = modelData.get(Keys.TABLE_ID);
      tableReference.setTableId(tableId);
    }

    return tableReference;
  }

  public ToolBar createToolBar()
  {
    ToolBar toolBar = new ToolBar();

    refreshButton = new Button(REFRESH, commandListener);
    refreshButton.setToolTip("Refresh status of existing connections");
    toolBar.add(refreshButton);

    importButton = new Button(IMPORT, commandListener);
    importButton.setToolTip("Import selected table");
    toolBar.add(importButton);

    deleteButton = new Button(DELETE, commandListener);
    deleteButton.setToolTip("Delete selected connections and imported table(s)");
    toolBar.add(deleteButton);

    describeButton = new Button(DESCRIBE, commandListener);
    describeButton.setToolTip("Describe columns for selected table(s)");
    toolBar.add(describeButton);

    displayButton = new Button(DISPLAY, commandListener);
    displayButton.setToolTip("Display rows for selected table(s)");
    toolBar.add(displayButton);

    updateButton = new Button(UPDATE, commandListener);
    updateButton.setToolTip("Update selected database connection");
    toolBar.add(updateButton);

    addButton = new Button(ADD, commandListener);
    addButton.setToolTip("Add selected column(s) to query builder");
    toolBar.add(addButton);

    toolBar.add(new FillToolItem());

    timeStampButton = new ToggleButton();
    timeStampButton.setIcon(Resources.TIME_ADD);
    timeStampButton.setToolTip("Assign imported tables a unique version number");
    toolBar.add(timeStampButton);

    expandButton = new Button();
    expandButton.setIcon(Resources.TEXT_LINESPACING);
    expandButton.addSelectionListener(commandListener);
    expandButton.setToolTip("Expand or collapse all items in tree");
    toolBar.add(expandButton);

    return toolBar;
  }

  public List<ModelData> getSelectedTables()
  {
    List<ModelData> dragList = new LinkedList<ModelData>();
    List<ModelData> selectedItems = tableTreeGrid.getSelectionModel().getSelectedItems();
    for (ModelData selectedItem : selectedItems)
    {
      TableReference tableReference = createTableReference(selectedItem);
      ModelData wrappedTableReference = new BaseModelData();
      wrappedTableReference.set(Keys.TABLE_REFERENCE, tableReference);
      dragList.add(wrappedTableReference);
    }
    return dragList;
  }

  private void importTables()
  {
    List<TableReference> tableReferences = new LinkedList<TableReference>();
    List<ModelData> selectedItems = tableTreeGrid.getSelectionModel().getSelectedItems();
    for (ModelData selectedItem : selectedItems)
    {
      NodeType nodeType = selectedItem.get(Keys.NODE_TYPE);
      if (nodeType == NodeType.EXTERNAL_TABLE || nodeType == NodeType.EXTERNAL_VIEW)
      {
        TableReference tableReference = createTableReference(selectedItem);
        tableReferences.add(tableReference);
      }
    }
    if (tableReferences.size() > 0)
    {
      boolean isGenerateVersion = timeStampButton.isPressed();
      QueryBuilderServiceBus.importTable(tableReferences, isGenerateVersion);
    }
  }

  public void onAddButton()
  {
    addTablesToDefaultQueryBuilder();
  }

  public void onCollapseButton()
  {
    tableTreeGrid.collapseAll();
  }

  @Override
  public void onCommand(String command, AbstractImagePrototype icon)
  {
    if (command.equals(UPDATE))
    {
      update();
    }
    else if (command.equals(REFRESH))
    {
      refresh();
    }
    else if (command.equals(IMPORT))
    {
      onImportButton();
    }
    else if (command.equals(DELETE))
    {
      onDeleteButton();
    }
    else if (command.equals(DESCRIBE))
    {
      onDescribeButton();
    }
    else if (command.equals(DISPLAY))
    {
      onDisplayButton();
    }
    else if (command.equals(ADD))
    {
      onAddButton();
    }
    else if (icon == Resources.TEXT_LINESPACING)
    {
      onExpandButton();
    }
  }

  public void onDeleteButton()
  {
    final List<ModelData> selectedItems = tableTreeGrid.getSelectionModel().getSelectedItems();
    int selectedItemCount = selectedItems.size();
    if (selectedItemCount > 0)
    {
      String items = Utilities.concatenate(selectedItems, Keys.NAME, ", ");
      MessageBox.confirm("Delete Items", "Would you like to delete: " + items, new DeleteConfirmListener(selectedItems));
    }
  }

  private void onDescribeButton()
  {
    for (ModelData selectedItem : tableTreeGrid.getSelectionModel().getSelectedItems())
    {
      TableReference tableReference = createTableReference(selectedItem);
      QueryBuilderServiceBus.describeTable(selectedItem, tableReference);
    }
  }

  private void onDisplayButton()
  {
    for (ModelData selectedItem : tableTreeGrid.getSelectionModel().getSelectedItems())
    {
      TableReference tableReference = createTableReference(selectedItem);
      QueryBuilderServiceBus.displayTable(selectedItem, tableReference);
    }
  }

  public void onDoubleClick()
  {
    addTablesToDefaultQueryBuilder();
  }

  public void onExpandButton()
  {
    if (looksExpanded())
    {
      tableTreeGrid.collapseAll();
    }
    else
    {
      tableTreeGrid.expandAll();
    }
  }

  private boolean looksExpanded()
  {
    int childCount = tableTreeStore.getChildCount();
    for (int childOffset = 0; childOffset < childCount; childOffset++)
    {
      ModelData child = tableTreeStore.getChild(childOffset);
      if (tableTreeGrid.isExpanded(child))
      {
        return true;
      }
    }
    return false;
  }

  private void onImportButton()
  {
    importTables();
  }

  protected void onLeafDoubleClick()
  {
    addTablesToDefaultQueryBuilder();
  }

  protected void onParentDoubleClick()
  {
    onUpdateButton();
  }

  @Override
  protected void onRender(Element parent, int pos)
  {
    super.onRender(parent, pos);
    getBody().setStyleAttribute("borderTop", "none"); // See ContentPanel.onRender: if (!bodyBorder) 
  }

  public void onSelectionChanged(ModelData selectedItem)
  {
    updateFormState();
  }

  private void onUpdateButton()
  {
  }

  public void refresh()
  {
    QueryBuilderServiceBus.getMasterTree();
  }

  public void update()
  {
  }

  private void updateFormState()
  {
    int connectionCount = 0;
    int externalCount = 0;
    int internalCount = 0;

    List<ModelData> selectedItems = tableTreeGrid.getSelectionModel().getSelectedItems();

    for (ModelData selectedItem : selectedItems)
    {
      NodeType nodeType = selectedItem.get(Keys.NODE_TYPE);
      switch (nodeType)
      {
        case CONNECTION:
          connectionCount++;
          break;
        case EXTERNAL_TABLE:
        case EXTERNAL_VIEW:
          externalCount++;
          break;
        case INTERNAL_TABLE:
          internalCount++;
          break;
      }
    }

    int selectedItemCount = selectedItems.size();

    boolean isConnection = selectedItemCount > 0 && selectedItemCount == connectionCount;
    boolean isExternal = selectedItemCount > 0 && selectedItemCount == externalCount;
    boolean isInternal = selectedItemCount > 0 && selectedItemCount == internalCount;
    boolean isTable = selectedItemCount > 0 && selectedItemCount == externalCount + internalCount;

    updateButton.setEnabled(isConnection && selectedItemCount == 1);
    importButton.setEnabled(isExternal);
    deleteButton.setEnabled(isConnection || isInternal);
    describeButton.setEnabled(isTable);
    displayButton.setEnabled(isTable);
    addButton.setEnabled(isTable);
    expandButton.setEnabled(tableTreeStore.getChildCount() > 0);
  }

  private final class DeleteConfirmListener implements Listener<MessageBoxEvent>
  {
    private final List<ModelData> selectedItems;

    private DeleteConfirmListener(List<ModelData> selectedItems)
    {
      this.selectedItems = selectedItems;
    }

    @Override
    public void handleEvent(MessageBoxEvent be)
    {
      if (be.getButtonClicked().getText().equals("Yes"))
      {
        List<String> connectionNames = new LinkedList<String>();
        List<Integer> tables = new LinkedList<Integer>();
        for (ModelData modelData : selectedItems)
        {
          NodeType nodeType = modelData.get(Keys.NODE_TYPE);
          if (nodeType == NodeType.CONNECTION)
          {
            String connectionName = modelData.get(Keys.NAME);
            connectionNames.add(connectionName);
          }
          else if (nodeType == NodeType.INTERNAL_TABLE)
          {
            int tableId = modelData.get(Keys.TABLE_ID);
            tables.add(tableId);
          }
        }
        if (connectionNames.size() > 0)
        {
          QueryBuilderServiceBus.deleteConnections(connectionNames);
        }
        if (tables.size() > 0)
        {
          QueryBuilderServiceBus.deleteTables(tables);
          // TODO: Decide whether to delete references or leave them in case user re-imports tables.
        }
      }
    }
  }

  private final class TableTreeGridDoubleClickListener implements Listener<TreeGridEvent<ModelData>>
  {
    @Override
    public void handleEvent(TreeGridEvent<ModelData> e)
    {
      onDoubleClick();
    }
  }

  private final class TableTreeGridDragSource extends TreeGridDragSource
  {
    private TableTreeGridDragSource(ExtendedTreeGrid tree)
    {
      super(tree);
    }

    @Override
    protected void onDragDrop(DNDEvent event)
    {
      // Suppress call to base class, which handles move
    }

    @Override
    protected void onDragStart(DNDEvent e)
    {
      TreeGrid<?>.TreeNode n = treeGrid.findNode(e.getTarget());
      if (n == null)
      {
        e.setCancelled(true);
        return;
      }

      ModelData m = n.getModel();
      if (!treeGrid.getTreeView().isSelectableTarget(m, e.getTarget()))
      {
        e.setCancelled(true);
        return;
      }

      updateFormState();
      if (!addButton.isEnabled())
      {
        e.setCancelled(true);
        return;
      }

      List<ModelData> dragList = getSelectedTables();
      e.setData(dragList);
      e.getStatus().update(Format.substitute(getStatusText(), dragList.size()));
    }
  }

  public class TableTreeGridPanelServiceProvider implements ServiceProvider
  {
    @Override
    public void onServiceRequest(ServiceRequest serviceRequest)
    {
      if (serviceRequest instanceof SelectTable)
      {
        SelectTable selectTable = (SelectTable)serviceRequest;
        ModelData modelData = selectTable.getModelData();
        tableTreeGrid.scrollIntoView(modelData, true, false);
      }
      else if (serviceRequest instanceof SelectConnection)
      {
        SelectConnection selectConnection = (SelectConnection)serviceRequest;
        String connectionName = selectConnection.getConnectionName();
        tableTreeGrid.expandAll();
        tableTreeGrid.scrollIntoView(Keys.NAME, connectionName, false, true, false);
        updateFormState();
      }
      else if (serviceRequest instanceof GetMasterTreeResults)
      {
        GetMasterTreeResults getMasterTreeResults = (GetMasterTreeResults)serviceRequest;
        Integer tableId = getMasterTreeResults.getTableId();
        TreeModel connectionTree = getMasterTreeResults.getConnectionTree();
        tableTreeStore.merge(connectionTree);
        tableTreeGrid.expandAll();
        if (tableId != null)
        {
          tableTreeGrid.scrollIntoView(Keys.TABLE_ID, tableId, true, true, false);
        }
        updateFormState();

      }
    }

  }

  private final class TableTreeGridSelectionChangedListener extends SelectionChangedListener<ModelData>
  {
    @Override
    public void selectionChanged(SelectionChangedEvent<ModelData> se)
    {
      ModelData selectedItem = se.getSelectedItem();
      onSelectionChanged(selectedItem);
    }
  }

}
