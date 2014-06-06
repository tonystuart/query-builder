// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.example.querybuilder.client.QueryBuilderServiceBus.AddTablesToQueryBuilder;
import com.example.querybuilder.client.FormulaBuilderWindow.Callback;
import com.example.querybuilder.client.ServiceBus.ServiceProvider;
import com.example.querybuilder.client.ServiceBus.ServiceRequest;
import com.example.querybuilder.shared.Condition;
import com.example.querybuilder.shared.Constants;
import com.example.querybuilder.shared.JoinType;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.PivotColumn;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.SortDirection;
import com.example.querybuilder.shared.SummaryOperation;
import com.example.querybuilder.shared.TableReference;
import com.example.querybuilder.shared.Values;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class QueryBuilderTabItem extends TabItem
{
  private static final int BORDER_MAX_SIZE = 10000;
  private static final int BORDER_MIN_SIZE = 0;

  private BorderLayout borderLayout;
  private ColumnTabPanel columnTabPanel;
  private DisplayPanel displayPanel;
  private BorderLayout outterSouthLayout;
  private TablePortal tablePortal;
  private FormulaBuilderWindow formulaBuilderWindow;
  private QueryBuilderTabItemServiceProvider queryBuilderTabItemServiceProvider;

  public String toString()
  {
    return getText();
  }

  public QueryBuilderTabItem(String title)
  {
    setText(title);
    setIcon(Resources.TABLE_LIGHTNING);
    setBorders(false);
    setClosable(true);

    borderLayout = new BorderLayout();
    setLayout(borderLayout);

    BorderLayoutData top = new BorderLayoutData(LayoutRegion.CENTER);
    top.setMinSize(BORDER_MIN_SIZE);
    top.setMaxSize(BORDER_MAX_SIZE);
    ContentPanel tablePortalContentPanel = new ContentPanel();
    tablePortalContentPanel.setLayout(new FitLayout());
    tablePortalContentPanel.setHeaderVisible(false);
    tablePortalContentPanel.setBorders(true);
    tablePortalContentPanel.setStyleAttribute("borderTop", "none");
    tablePortalContentPanel.setBodyBorder(false);
    tablePortal = new TablePortal(this);
    tablePortalContentPanel.add(tablePortal);
    add(tablePortalContentPanel, top);

    outterSouthLayout = new BorderLayout();
    LayoutContainer outterSouthLayoutContainer = new LayoutContainer(outterSouthLayout);
    BorderLayoutData outterSouth = new BorderLayoutData(LayoutRegion.SOUTH, .60f);
    outterSouth.setSplit(true);
    outterSouth.setHidden(true);
    outterSouth.setMargins(new Margins(5, 0, 0, 0));
    outterSouth.setMinSize(BORDER_MIN_SIZE);
    outterSouth.setMaxSize(BORDER_MAX_SIZE);
    add(outterSouthLayoutContainer, outterSouth);

    BorderLayoutData innerNorth = new BorderLayoutData(LayoutRegion.CENTER);
    columnTabPanel = new ColumnTabPanel(this);
    outterSouthLayoutContainer.add(columnTabPanel, innerNorth);

    BorderLayoutData bottom = new BorderLayoutData(LayoutRegion.SOUTH, .50f);
    bottom.setHidden(true);
    bottom.setSplit(true);
    bottom.setMargins(new Margins(5, 0, 0, 0));
    bottom.setMinSize(BORDER_MIN_SIZE);
    bottom.setMaxSize(BORDER_MAX_SIZE);
    displayPanel = new DisplayPanel();
    outterSouthLayoutContainer.add(displayPanel, bottom);

    formulaBuilderWindow = new FormulaBuilderWindow("Formula Builder");

    queryBuilderTabItemServiceProvider = new QueryBuilderTabItemServiceProvider();
    ServiceBus.addServiceProvider(queryBuilderTabItemServiceProvider);

    addListener(Events.Close, new Listener<TabPanelEvent>()
    {
      @Override
      public void handleEvent(TabPanelEvent be)
      {
        ServiceBus.removeServiceProvider(queryBuilderTabItemServiceProvider);
      }
    });
  }

  public void addColumns(List<ModelData> columns)
  {
    if (formulaBuilderWindow.isVisible())
    {
      formulaBuilderWindow.addColumns(columns);
    }
    else
    {
      columnTabPanel.addColumns(columns);
    }
  }

  private List<TableReference> getTableReferences()
  {
    return tablePortal.getTableReferences();
  }

  public void onAddTablePortlet()
  {
    int portletCount = tablePortal.getPortletCount();
    if (portletCount == 1)
    {
      borderLayout.show(LayoutRegion.SOUTH);
    }
    columnTabPanel.onAddTablePortlet();
  }

  public void onRemoveTablePortlet(String tableAlias)
  {
    int portletCount = tablePortal.getPortletCount();
    if (portletCount == 0)
    {
      borderLayout.hide(LayoutRegion.SOUTH);
    }
    columnTabPanel.onRemoveTablePortlet(tableAlias);
  }

  private final class QueryBuilderTabItemServiceProvider implements ServiceProvider
  {
    @Override
    public void onServiceRequest(ServiceRequest serviceRequest)
    {
      if (serviceRequest instanceof AddTablesToQueryBuilder)
      {
        if (QueryBuilderTabItem.this.isVisible())
        {
          AddTablesToQueryBuilder addTablesToQueryBuilder = (AddTablesToQueryBuilder)serviceRequest;
          List<ModelData> tables = addTablesToQueryBuilder.getTables();
          tablePortal.addTables(tables);
        }
      }
    }
  }

  private final class ColumnTabPanel extends TabPanel
  {
    private DisplayColumnPanel displayColumnPanel;
    private FilterCriteriaPanel filterCriteriaPanel;
    private JoinColumnPanel joinColumnPanel;
    private TabItem joinTabItem;

    private ColumnTabPanel(QueryBuilderTabItem queryBuilderTabItem)
    {
      setBodyBorder(false);
      setBorders(false);

      displayColumnPanel = new DisplayColumnPanel();
      TabItem displayTabItem = new TabItem("Display Columns");
      displayTabItem.setIcon(Resources.TABLE_SORT);
      displayTabItem.setLayout(new FitLayout());
      displayTabItem.add(displayColumnPanel);
      add(displayTabItem);

      filterCriteriaPanel = new FilterCriteriaPanel();
      TabItem filterTabItem = new TabItem("Filter Criteria");
      filterTabItem.setIcon(Resources.TABLE_ROW_DELETE);
      filterTabItem.setLayout(new FitLayout());
      filterTabItem.add(filterCriteriaPanel);
      add(filterTabItem);

      joinColumnPanel = new JoinColumnPanel();
      joinTabItem = new TabItem("Join Conditions");
      joinTabItem.setIcon(Resources.TABLE_RELATIONSHIP);
      joinTabItem.setLayout(new FitLayout());
      joinTabItem.add(joinColumnPanel);

      updateFormStateAll();
    }

    private void updateFormStateAll()
    {
      displayColumnPanel.updateFormState();
      filterCriteriaPanel.updateFormState();
      joinColumnPanel.updateFormState();
    }

    private void addColumns(List<ModelData> columns)
    {
      if (displayColumnPanel.isVisible())
      {
        displayColumnPanel.addColumns(columns);
      }
      else if (filterCriteriaPanel.isVisible())
      {
        filterCriteriaPanel.addColumns(columns);
      }
      else
      {
        joinColumnPanel.addColumns(columns);
      }
    }

    private void deleteTableReferences(String tableAlias)
    {
      displayColumnPanel.deleteTableReferences(tableAlias);
      joinColumnPanel.deleteTableReferences(tableAlias);
    }

    private boolean isRunnable()
    {
      int tableCount = tablePortal.getPortletCount();
      int displayCount = displayColumnPanel.getDisplayCount();
      int joinCount = joinColumnPanel.getJoinCount();
      boolean containsData = displayCount > 0;
      boolean isRunnable = containsData && (tableCount == 1 || (tableCount > 1 && tableCount == joinCount));
      return isRunnable;
    }

    private void onAddTablePortlet()
    {
      int tableCount = tablePortal.getPortletCount();
      if (tableCount == 2)
      {
        add(joinTabItem);
      }
      updateFormStateAll();
    }

    private void onRemoveTablePortlet(String tableAlias)
    {
      deleteTableReferences(tableAlias);

      int tableCount = tablePortal.getPortletCount();
      if (tableCount == 1)
      {
        remove(joinTabItem);
      }
      updateFormStateAll();
    }

    private void runQuery()
    {
      List<TableReference> tableReferences = getTableReferences();
      List<ModelData> displayColumnModels = displayColumnPanel.listStore.getModels();
      List<ModelData> joinColumnModels = joinColumnPanel.getJoinColumnModels();
      String filterCriteria = filterCriteriaPanel.getFilterCriteria();
      final QueryDescriptor queryDescriptor = new QueryDescriptor(tableReferences, displayColumnModels, joinColumnModels, filterCriteria);
      BasePagingLoadConfig pagingLoadConfig = new BasePagingLoadConfig(0, 50);
      QueryBuilderServiceBus.setStatusBusy("Running...");

      // Do *NOT* factor this out into something that is far more complex and inefficient!
      QueryBuilder.queryBuilderService.runQuery(queryDescriptor, pagingLoadConfig, new ClearStatusCallback<QueryResult>()
      {
        @Override
        protected void process(QueryResult queryResult)
        {
          outterSouthLayout.show(LayoutRegion.SOUTH);
          displayPanel.display(queryDescriptor, queryResult);
        }
      });
    }

    private void select(Grid<ModelData> grid, ModelData modelData)
    {
      grid.getSelectionModel().select(false, modelData);
      GridView gridView = grid.getView();
      Element element = gridView.getRow(modelData);
      int row = gridView.findRowIndex(element);
      gridView.ensureVisible(row, 0, true);
    }

    private class DisplayColumnPanel extends ContentPanel implements CommandProcessor
    {
      private static final String COMMAND_ADD = "Add";
      private static final String COMMAND_DELETE = "Delete";
      private static final String COMMAND_RUN = "Run";

      private static final int MAX_SQL92_IDENTIFIER_LENGTH = 128;

      private Button addButton;
      private CommandListener<ButtonEvent> buttonListener = new CommandListener<ButtonEvent>(this);
      private Button deleteButton;
      private int formulaColumn;
      private EditorGrid<ModelData> grid;
      private ListStore<ModelData> listStore;
      private Button runButton;

      private DisplayColumnPanel()
      {
        setHeaderVisible(false);
        setBodyBorder(true); // ContentPanel inside TabItem requires removing top border, see onRender
        setBorders(false);
        setLayout(new FitLayout());

        GridStoreListener gridStoreListener = new GridStoreListener();
        GridSelectionChangedListener gridSelectionChangedListener = new GridSelectionChangedListener();

        listStore = new ListStore<ModelData>();
        listStore.setMonitorChanges(true);
        listStore.addStoreListener(gridStoreListener);

        final EditablePropertyRenderer editablePropertyRenderer = new EditablePropertyRenderer();

        List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();

        ColumnConfig tableAliasColumnConfig = new ColumnConfig(Keys.TABLE_ALIAS, "Source", 100);
        columnConfigs.add(tableAliasColumnConfig);

        ColumnConfig columnNameColumnConfig = new ColumnConfig(Keys.COLUMN_NAME, "Value", 100);
        formulaColumn = columnConfigs.size();
        columnNameColumnConfig.setRenderer(new FormulaColumnRenderer(editablePropertyRenderer));
        columnConfigs.add(columnNameColumnConfig);

        ColumnConfig columnAliasColumnConfig = new ColumnConfig(Keys.COLUMN_ALIAS, "Name", 100);
        ColumnAliasEditor columnAliasTextField = new ColumnAliasEditor();
        CellEditor columnAliasCellEditor = columnAliasTextField.getCellEditor();
        columnAliasColumnConfig.setEditor(columnAliasCellEditor);
        columnAliasColumnConfig.setRenderer(editablePropertyRenderer);
        columnConfigs.add(columnAliasColumnConfig);

        ColumnConfig sortDirectionColumnConfig = new ColumnConfig(Keys.SORT_DIRECTION, "Sort Direction", 100);
        sortDirectionColumnConfig.setEditor(new EnumCellEditorComboBox<SortDirection>(SortDirection.values()).getCellEditor());
        sortDirectionColumnConfig.setRenderer(editablePropertyRenderer);
        columnConfigs.add(sortDirectionColumnConfig);

        ColumnConfig summaryOperationColumnConfig = new ColumnConfig(Keys.SUMMARY_OPERATION, "Summary Operation", 100);
        summaryOperationColumnConfig.setEditor(new EnumCellEditorComboBox<SummaryOperation>(SummaryOperation.values()).getCellEditor());
        summaryOperationColumnConfig.setRenderer(editablePropertyRenderer);
        columnConfigs.add(summaryOperationColumnConfig);

        ColumnConfig pivotColumnConfig = new ColumnConfig(Keys.PIVOT_COLUMN, "Pivot Column", 100);
        pivotColumnConfig.setEditor(new EnumCellEditorComboBox<PivotColumn>(PivotColumn.values()).getCellEditor());
        pivotColumnConfig.setRenderer(editablePropertyRenderer);
        columnConfigs.add(pivotColumnConfig);

        ColumnModel columnModel = new ColumnModel(columnConfigs);

        grid = new FormulaEditorGrid(listStore, columnModel);
        grid.setClicksToEdit(ClicksToEdit.TWO);
        grid.setSelectionModel(new GridSelectionModel<ModelData>());
        grid.getView().setEmptyText("<b>Drag Columns</b> from tables to define <b>Display Columns</b> here.");
        grid.getView().setAutoFill(true);
        grid.getSelectionModel().addSelectionChangedListener(gridSelectionChangedListener);
        new GridDragSource(grid);
        GridDropTarget target = new DisplayGridDropTarget(grid);
        target.setAllowSelfAsSource(true);
        target.setFeedback(Feedback.INSERT);
        add(grid);

        ToolBar toolBar = createToolBar();
        setBottomComponent(toolBar);
      }

      private void addColumn(ModelData modelData)
      {
        normalizeData(modelData);
        grid.getStore().add(modelData);
        select(grid, modelData);
      }

      private void addColumns(List<ModelData> columns)
      {
        for (ModelData modelData : columns)
        {
          addColumn(modelData);
        }
      }

      private void clearColumnHeaderSort()
      {
        listStore.setStoreSorter(null);
        listStore.setSortField(null);
        listStore.setSortDir(SortDir.NONE);
        grid.getView().getHeader().refresh();
      }

      private boolean columnExists(String columnAlias)
      {
        return listStore.findModel(Keys.COLUMN_ALIAS, columnAlias) != null;
      }

      private ToolBar createToolBar()
      {
        ToolBar toolBar = new ToolBar();

        addButton = new Button(COMMAND_ADD, buttonListener);
        addButton.setToolTip("Add formula column");
        toolBar.add(addButton);

        deleteButton = new Button(COMMAND_DELETE, buttonListener);
        deleteButton.setToolTip("Remove selected column(s) from query");
        toolBar.add(deleteButton);

        runButton = new Button(COMMAND_RUN, buttonListener);
        runButton.setToolTip("Run current query and display results");
        toolBar.add(runButton);

        return toolBar;
      }

      private void deleteColumn()
      {
        for (ModelData selectedItem : grid.getSelectionModel().getSelectedItems())
        {
          listStore.remove(selectedItem);
        }
      }

      private void deleteTableReferences(String deletedTableAlias)
      {
        for (ModelData modelData : listStore.getModels()) // Must use copy to prevent CME
        {
          String tableAlias = modelData.get(Keys.TABLE_ALIAS);
          if (tableAlias.equals(deletedTableAlias))
          {
            listStore.remove(modelData);
          }
          else if (tableAlias.equals(Values.FORMULA_TABLE_ALIAS))
          {
            // TODO: Implement a client side expression parser
            String formula = modelData.get(Keys.COLUMN_NAME);
            String formulaLowerCase = formula.toLowerCase();
            String deletedTableAliasLowerCaseWithDot = deletedTableAlias.toLowerCase() + ".";
            int fromIndex = 0;
            int offset;
            while ((offset = formulaLowerCase.indexOf(deletedTableAliasLowerCaseWithDot, fromIndex)) != -1)
            {
              if (offset == 0 || isDelimiter(formulaLowerCase.charAt(offset - 1)))
              {
                listStore.remove(modelData);
                return;
              }
              fromIndex = offset + 1; // assumes length(deletedTableAliasLowerCaseWithDot) > 1
            }
          }
        }
      }

      private int getDisplayCount()
      {
        return listStore.getCount();
      }

      private boolean isDelimiter(char c)
      {
        return " +-*/()".indexOf(c) != -1;
      }

      private boolean isFormula(ModelData modelData)
      {
        String tableAlias = modelData.get(Keys.TABLE_ALIAS);
        boolean isFormula = Values.FORMULA_TABLE_ALIAS.equals(tableAlias);
        return isFormula;
      }

      private void normalizeData(ModelData modelData)
      {
        SortDirection sortDirection = modelData.get(Keys.SORT_DIRECTION);
        boolean isExternalData = sortDirection == null;
        if (isExternalData)
        {
          modelData.set(Keys.SORT_DIRECTION, SortDirection.NONE);
          modelData.set(Keys.SUMMARY_OPERATION, SummaryOperation.NONE);
          modelData.set(Keys.PIVOT_COLUMN, PivotColumn.NO);

          int count = 0;
          String columnAlias = modelData.get(Keys.COLUMN_ALIAS);
          String uniqueColumnAlias = columnAlias;

          while (columnExists(uniqueColumnAlias))
          {
            uniqueColumnAlias = columnAlias + Constants.NUMBER_DELIMITER + (++count);
          }

          if (!columnAlias.equals(uniqueColumnAlias))
          {
            modelData.set(Keys.COLUMN_ALIAS, uniqueColumnAlias);
          }
        }
      }

      private void onAddFormulaColumn()
      {
        String formula = "";
        formulaBuilderWindow.display(this, formula, new FormulaBuilderCallback(null));
      }

      @Override
      public void onCommand(String command, AbstractImagePrototype icon)
      {
        if (command.equals(COMMAND_ADD))
        {
          onAddFormulaColumn();
        }
        else if (command.equals(COMMAND_DELETE))
        {
          onDelete();
        }
        else if (command.equals(COMMAND_RUN))
        {
          onRunButton();
        }
      }

      private void onDelete()
      {
        deleteColumn();
      }

      private void onEditFormulaColumn()
      {
        final ModelData selectedItem = grid.getSelectionModel().getSelectedItem();
        if (selectedItem != null)
        {
          if (isFormula(selectedItem))
          {
            String formula = selectedItem.get(Keys.COLUMN_NAME);
            formulaBuilderWindow.display(this, formula, new FormulaBuilderCallback(selectedItem));
          }
        }
      }

      private void onGridSelectionChanged()
      {
        updateFormState(); // local only
      }

      @Override
      protected void onRender(com.google.gwt.user.client.Element parent, int pos)
      {
        super.onRender(parent, pos);
        getBody().setStyleAttribute("borderTop", "none"); // See ContentPanel.onRender: if (!bodyBorder) 
      }

      private void onRunButton()
      {
        runQuery();
      }

      private void onStoreEvent()
      {
        listStore.commitChanges();
        updateFormStateAll();
      }

      private void updateFormState()
      {
        ModelData selectedItem = grid.getSelectionModel().getSelectedItem();
        boolean isItemSelected = selectedItem != null;
        boolean isRunnable = isRunnable();

        deleteButton.setEnabled(isItemSelected);
        runButton.setEnabled(isRunnable);
      }

      private class ColumnAliasEditor extends TextField<String>
      {
        private String originalValue;

        private ColumnAliasEditor()
        {
          addKeyListener(new TextFieldKeyListener());
        }

        private CellEditor getCellEditor()
        {
          return new ColumnAliasCellEditor(this);
        }

        private void validateColumnName(ComponentEvent event)
        {
          String message = null;
          String newValue = getValue();

          if (newValue == null || newValue.length() == 0)
          {
            message = "Must not be blank";
          }
          else if (newValue.equals(originalValue))
          {
            // If it was originally valid, it is still valid
          }
          else if (columnExists(newValue))
          {
            message = "Column already exists";
          }

          if (message == null)
          {
            clearInvalid();
          }
          else
          {
            forceInvalid(message);
          }
        }

        private class ColumnAliasCellEditor extends CellEditor
        {
          private ColumnAliasCellEditor(Field<? extends Object> field)
          {
            super(field);
          }

          @Override
          public Object preProcessValue(Object value)
          {
            ColumnAliasEditor.this.originalValue = (String)value;
            return value;
          }
        }

        private final class TextFieldKeyListener extends KeyListener
        {
          @Override
          public void componentKeyUp(ComponentEvent event)
          {
            super.componentKeyUp(event);
            validateColumnName(event);
          }
        }
      }

      private class DisplayGridDropTarget extends GridDropTarget
      {
        private DisplayGridDropTarget(Grid<ModelData> grid)
        {
          super(grid);
        }

        @Override
        protected void onDragDrop(DNDEvent e)
        {
          clearColumnHeaderSort();
          super.onDragDrop(e);
          List<ModelData> list = e.getData();
          if (list.size() > 0)
          {
            select(grid, list.get(0));
          }
        }

        @Override
        protected List<ModelData> prepareDropData(Object data, boolean convertTreeStoreModel)
        {
          List<ModelData> dropData = super.prepareDropData(data, convertTreeStoreModel);
          for (ModelData modelData : dropData)
          {
            normalizeData(modelData);
          }
          return dropData;
        }
      }

      private final class FormulaBuilderCallback implements Callback
      {
        private ModelData modelData;

        private FormulaBuilderCallback(ModelData modelData)
        {
          this.modelData = modelData;
        }

        @Override
        public void onSave(String formula)
        {
          String columnAlias = formula.substring(0, Math.min(formula.length(), MAX_SQL92_IDENTIFIER_LENGTH));
          if (modelData == null)
          {
            modelData = new BaseModel();
            modelData.set(Keys.TABLE_ALIAS, Values.FORMULA_TABLE_ALIAS);
            modelData.set(Keys.COLUMN_NAME, formula);
            modelData.set(Keys.COLUMN_ALIAS, columnAlias);
            addColumn(modelData);
          }
          else
          {
            if (modelData.get(Keys.COLUMN_ALIAS).equals(modelData.get(Keys.COLUMN_NAME)))
            {
              // If the name is still set to the default value (formula) then update it
              modelData.set(Keys.COLUMN_ALIAS, columnAlias);
            }
            modelData.set(Keys.COLUMN_NAME, formula);
          }
        }
      }

      private final class FormulaColumnRenderer implements GridCellRenderer<ModelData>
      {
        private final EditablePropertyRenderer editablePropertyRenderer;

        private FormulaColumnRenderer(EditablePropertyRenderer editablePropertyRenderer)
        {
          this.editablePropertyRenderer = editablePropertyRenderer;
        }

        @Override
        public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ModelData> store, Grid<ModelData> grid)
        {
          if (isFormula(model))
          {
            return editablePropertyRenderer.render(model, property, config, rowIndex, colIndex, store, grid);
          }
          return null;
        }
      }

      private final class FormulaEditorGrid extends EditorGrid<ModelData>
      {
        private FormulaEditorGrid(ListStore<ModelData> store, ColumnModel cm)
        {
          super(store, cm);
        }

        private boolean isFormulaCell(GridEvent<ModelData> e)
        {
          boolean isFormulaCell = false;
          if (e.getColIndex() == formulaColumn)
          {
            int rowIndex = e.getRowIndex();
            ModelData modelData = store.getAt(rowIndex);
            if (isFormula(modelData))
            {
              isFormulaCell = true;
            }
          }
          return isFormulaCell;
        }

        protected void onDoubleClick(GridEvent<ModelData> e)
        {
          if (isFormulaCell(e))
          {
            onEditFormulaColumn();
          }
          else
          {
            super.onDoubleClick(e);
          }
        }
      }

      private final class GridSelectionChangedListener extends SelectionChangedListener<ModelData>
      {
        @Override
        public void selectionChanged(SelectionChangedEvent<ModelData> se)
        {
          onGridSelectionChanged();
        }
      }

      private final class GridStoreListener extends StoreListener<ModelData>
      {
        @Override
        public void handleEvent(StoreEvent<ModelData> e)
        {
          onStoreEvent();
        }
      }

    }

    private class FilterCriteriaPanel extends ContentPanel implements CommandProcessor
    {
      private static final String COMMAND_CLEAR = "Clear";
      private static final String COMMAND_RUN = "Run";

      private CommandListener<ButtonEvent> commandListener = new CommandListener<ButtonEvent>(this);
      private ExpressionPanel expressionPanel;
      private Button clearButton;
      private Button runButton;

      private FilterCriteriaPanel()
      {
        setHeaderVisible(false);
        setBodyBorder(true); // ContentPanel inside TabItem requires removing top border, see onRender
        setBorders(false);
        setLayout(new FitLayout());

        expressionPanel = new ExpressionPanel("Drag columns from tables to build filter criteria here.");
        // An Ext-GWT TextArea is a textarea inside a div... we use this style to give it the right border
        expressionPanel.getFormulaTextArea().addStyleName("dp-filter-criteria");
        add(expressionPanel);

        ToolBar topToolBar = createTopToolBar();
        topToolBar.setStyleAttribute("borderTop", "none");
        setTopComponent(topToolBar);

        ToolBar bottomToolBar = createBottomToolBar();
        setBottomComponent(bottomToolBar);
      }

      private String getFilterCriteria()
      {
        return expressionPanel.getExpression();
      }

      private void addColumns(List<ModelData> columns)
      {
        expressionPanel.addColumns(columns);
      }

      private ToolBar createBottomToolBar()
      {
        ToolBar toolBar = new ToolBar();

        clearButton = new Button(COMMAND_CLEAR, commandListener);
        clearButton.setToolTip("Run current query and display results");
        toolBar.add(clearButton);

        runButton = new Button(COMMAND_RUN, commandListener);
        runButton.setToolTip("Run current query and display results");
        toolBar.add(runButton);

        return toolBar;
      }

      private ToolBar createTopToolBar()
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

        button = new Button("<", commandListener);
        button.setToolTip("Less than: a < 100");
        toolBar.add(button);

        button = new Button("<=", commandListener);
        button.setToolTip("Less than or equal to: a <= 100");
        toolBar.add(button);

        button = new Button("=", commandListener);
        button.setToolTip("Equal to: a = 100");
        toolBar.add(button);

        button = new Button(">=", commandListener);
        button.setToolTip("Greater than or equal to: a >= 100");
        toolBar.add(button);

        button = new Button(">", commandListener);
        button.setToolTip("Greater than: a > 100");
        toolBar.add(button);

        button = new Button("!=", commandListener);
        button.setToolTip("Not equal to: a != 100");
        toolBar.add(button);

        button = new Button(":=", commandListener);
        button.setToolTip("Assign to variable: x := a * 0.33");
        toolBar.add(button);

        button = new Button("AND", commandListener);
        button.setToolTip("Return true when both values are true: (a < 100) and (b > 10)");
        toolBar.add(button);

        button = new Button("OR", commandListener);
        button.setToolTip("Return true when either value is true: (a < 100) or (b > 10)");
        toolBar.add(button);

        button = new Button("NOT", commandListener);
        button.setToolTip("Return true when value is false: not (a < 100)");
        toolBar.add(button);

        toolBar.add(new FillToolItem());

        toolBar.add(expressionPanel.getFunctionComboBox());

        return toolBar;
      }

      @Override
      public void onCommand(String command, AbstractImagePrototype icon)
      {
        if (command.equals(COMMAND_CLEAR))
        {
          expressionPanel.getFormulaTextArea().clear();
        }
        else if (command.equals(COMMAND_RUN))
        {
          runQuery();
        }
        else
        {
          expressionPanel.insertText(command);
        }
      }

      @Override
      protected void onRender(com.google.gwt.user.client.Element parent, int pos)
      {
        super.onRender(parent, pos);
        getBody().setStyleAttribute("borderTop", "none"); // See ContentPanel.onRender: if (!bodyBorder) 
      }

      private void updateFormState()
      {
        boolean isRunnable = isRunnable();
        runButton.setEnabled(isRunnable);
      }
    }

    private class JoinColumnPanel extends ContentPanel implements CommandProcessor
    {
      private static final String COMMAND_ADD = "Add";
      private static final String COMMAND_DELETE = "Delete";
      private static final String COMMAND_RUN = "Run";

      private Button addButton;
      private CommandListener<ButtonEvent> buttonListener = new CommandListener<ButtonEvent>(this);
      private Button deleteButton;
      private EditorGrid<ModelData> grid;
      private ListStore<ModelData> listStore;
      private Button runButton;

      private JoinColumnPanel()
      {
        setHeaderVisible(false);
        setBodyBorder(true); // ContentPanel inside TabItem requires removing top border, see onRender
        setBorders(false);
        setLayout(new FitLayout());

        GridStoreListener gridStoreListener = new GridStoreListener();
        GridSelectionChangedListener gridSelectionChangedListener = new GridSelectionChangedListener();

        listStore = new ListStore<ModelData>();
        listStore.setMonitorChanges(true);
        listStore.addStoreListener(gridStoreListener);
        List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();

        EditablePropertyRenderer editablePropertyRenderer = new EditablePropertyRenderer();

        ColumnConfig joinType = new ColumnConfig(Keys.JOIN_TYPE, "Join Type", 100);
        EnumCellEditorComboBox<JoinType> joinTypeEnumCellEditorComboBox = new EnumCellEditorComboBox<JoinType>(JoinType.values());
        joinType.setEditor(joinTypeEnumCellEditorComboBox.getCellEditor());
        joinType.setRenderer(editablePropertyRenderer);
        columnConfigs.add(joinType);

        ColumnConfig leftTable = new ColumnConfig(Keys.JOIN_LEFT_TABLE_ALIAS, "Left Table Alias", 100);
        columnConfigs.add(leftTable);

        ColumnConfig leftColumn = new ColumnConfig(Keys.JOIN_LEFT_COLUMN_NAME, "Left Column Name", 150);
        columnConfigs.add(leftColumn);

        ColumnConfig condition = new ColumnConfig(Keys.JOIN_CONDITION, "Condition", 100);
        EnumCellEditorComboBox<Condition> conditionEnumCellEditorComboBox = new EnumCellEditorComboBox<Condition>(Condition.values());
        condition.setEditor(conditionEnumCellEditorComboBox.getCellEditor());
        condition.setRenderer(editablePropertyRenderer);
        columnConfigs.add(condition);

        ColumnConfig rightTable = new ColumnConfig(Keys.JOIN_RIGHT_TABLE_ALIAS, "Right Table Alias", 100);
        columnConfigs.add(rightTable);

        ColumnConfig rightColumn = new ColumnConfig(Keys.JOIN_RIGHT_COLUMN_NAME, "Right Column Name", 150);
        columnConfigs.add(rightColumn);

        ColumnModel columnModel = new ColumnModel(columnConfigs);
        grid = new EditorGrid<ModelData>(listStore, columnModel);
        grid.setClicksToEdit(ClicksToEdit.TWO);
        grid.setSelectionModel(new GridSelectionModel<ModelData>());
        grid.getView().setEmptyText("<b>Drag Columns</b> from tables to define <b>Join Conditions</b> here.");
        grid.getView().setAutoFill(true);
        grid.getSelectionModel().addSelectionChangedListener(gridSelectionChangedListener);
        new JoinGridDropTarget(grid);
        add(grid);

        ToolBar toolBar = createToolBar();
        setBottomComponent(toolBar);
      }

      private void addColumns(List<ModelData> items)
      {
        if (items.size() > 0)
        {
          ModelData item = items.get(0);
          int listStoreCount = listStore.getCount();
          if (listStoreCount > 0)
          {
            ModelData lastListStoreModelData = listStore.getAt(listStoreCount - 1);
            if (lastListStoreModelData.get(Keys.JOIN_RIGHT_TABLE_ALIAS) == null)
            {
              lastListStoreModelData.set(Keys.JOIN_RIGHT_TABLE_ALIAS, item.get(Keys.TABLE_ALIAS));
              lastListStoreModelData.set(Keys.JOIN_RIGHT_COLUMN_NAME, item.get(Keys.COLUMN_NAME));
              return;
            }
          }
          BaseModel newItem = new BaseModel();
          newItem.set(Keys.JOIN_TYPE, JoinType.INNER_JOIN);
          newItem.set(Keys.JOIN_LEFT_TABLE_ALIAS, item.get(Keys.TABLE_ALIAS));
          newItem.set(Keys.JOIN_LEFT_COLUMN_NAME, item.get(Keys.COLUMN_NAME));
          newItem.set(Keys.JOIN_CONDITION, Condition.EQ);
          listStore.add(newItem);
          select(grid, newItem);
        }
      }

      private ToolBar createToolBar()
      {
        ToolBar toolBar = new ToolBar();

        addButton = new Button(COMMAND_ADD, buttonListener);
        addButton.setToolTip("Add join condition");
        toolBar.add(addButton);

        deleteButton = new Button(COMMAND_DELETE, buttonListener);
        deleteButton.setToolTip("Remove selected column(s) from query");
        toolBar.add(deleteButton);

        runButton = new Button(COMMAND_RUN, buttonListener);
        runButton.setToolTip("Run current query and display results");
        toolBar.add(runButton);

        return toolBar;
      }

      private void deleteJoinColumn()
      {
        for (ModelData selectedItem : grid.getSelectionModel().getSelectedItems())
        {
          listStore.remove(selectedItem);
        }
      }

      private void deleteTableReferences(String tableAlias)
      {
        for (ModelData modelData : listStore.getModels()) // Must use copy to prevent CME
        {
          String leftTableAlias = modelData.get(Keys.JOIN_LEFT_TABLE_ALIAS);
          String rightTableAlias = modelData.get(Keys.JOIN_RIGHT_TABLE_ALIAS); // May be null
          if (tableAlias.equals(leftTableAlias) || tableAlias.equals(rightTableAlias))
          {
            listStore.remove(modelData);
          }
        }
      }

      private List<ModelData> getJoinColumnModels()
      {
        return listStore.getModels();
      }

      private int getJoinCount()
      {
        int itemCount = listStore.getCount();
        HashSet<String> tableAliases = new HashSet<String>();
        for (int offset = 0; offset < itemCount; offset++)
        {
          ModelData modelData = listStore.getAt(offset);
          String leftTable = modelData.get(Keys.JOIN_LEFT_TABLE_ALIAS);
          tableAliases.add(leftTable);
          String rightTable = modelData.get(Keys.JOIN_RIGHT_TABLE_ALIAS);
          if (rightTable != null)
          {
            tableAliases.add(rightTable);
          }
        }
        int joinCount = tableAliases.size();
        return joinCount;
      }

      private void onAdd()
      {
      }

      @Override
      public void onCommand(String command, AbstractImagePrototype icon)
      {
        if (command.equals(COMMAND_ADD))
        {
          onAdd();
        }
        else if (command.equals(COMMAND_DELETE))
        {
          onDelete();
        }
        else if (command.equals(COMMAND_RUN))
        {
          onRunButton();
        }
      }

      private void onDelete()
      {
        deleteJoinColumn();
      }

      private void onGridSelectionChanged()
      {
        updateFormState(); // local only
      }

      @Override
      protected void onRender(com.google.gwt.user.client.Element parent, int pos)
      {
        super.onRender(parent, pos);
        getBody().setStyleAttribute("borderTop", "none"); // See ContentPanel.onRender: if (!bodyBorder) 
      }

      private void onRunButton()
      {
        runQuery();
      }

      private void onStoreEvent()
      {
        listStore.commitChanges();
        updateFormStateAll();
      }

      private void updateFormState()
      {
        boolean isItemSelected = grid.getSelectionModel().getSelectedItem() != null;
        boolean isRunnable = isRunnable();

        addButton.setEnabled(isItemSelected);
        deleteButton.setEnabled(isItemSelected);
        runButton.setEnabled(isRunnable);
      }

      private final class GridSelectionChangedListener extends SelectionChangedListener<ModelData>
      {
        @Override
        public void selectionChanged(SelectionChangedEvent<ModelData> se)
        {
          onGridSelectionChanged();
        }
      }

      private final class GridStoreListener extends StoreListener<ModelData>
      {
        @Override
        public void handleEvent(StoreEvent<ModelData> e)
        {
          onStoreEvent();
        }
      }

      private class JoinGridDropTarget extends GridDropTarget
      {
        private JoinGridDropTarget(Grid<ModelData> grid)
        {
          super(grid);
        }

        @Override
        protected void onDragDrop(DNDEvent e)
        {
          super.onDragDrop(e);
          List<ModelData> items = e.getData();
          addColumns(items);
        }

        @Override
        protected List<ModelData> prepareDropData(Object data, boolean convertTreeStoreModel)
        {
          return new LinkedList<ModelData>();
        }
      }
    }
  }

}
