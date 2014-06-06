// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.example.querybuilder.shared.ArrayModelData;
import com.example.querybuilder.shared.ColumnDefinition;
import com.example.querybuilder.shared.ColumnDescriptor;
import com.example.querybuilder.shared.Condition;
import com.example.querybuilder.shared.Constants;
import com.example.querybuilder.shared.EnumText;
import com.example.querybuilder.shared.JoinType;
import com.example.querybuilder.shared.Keys;
import com.example.querybuilder.shared.NodeType;
import com.example.querybuilder.shared.PageResult;
import com.example.querybuilder.shared.PivotColumn;
import com.example.querybuilder.shared.QueryDescriptor;
import com.example.querybuilder.shared.QueryResult;
import com.example.querybuilder.shared.SortDirection;
import com.example.querybuilder.shared.SummaryOperation;
import com.example.querybuilder.shared.TableReference;
import com.example.querybuilder.shared.Values;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.TreeModel;

public final class DataWarehouse
{
  //private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
  //private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

  private static final String MASTER_SCHEMA_CONFIGURATION = "CONFIGURATION";
  private static final String MASTER_SCHEMA_DATA = "DATA";
  private static final String MASTER_TABLE_CONNECTIONS = "CONNECTIONS";
  private static final String MASTER_TABLE_SCHEMAS = "SCHEMAS";
  private static final String MASTER_TABLE_TABLES = "TABLES";
  private static final String MASTER_URL = "jdbc:derby://localhost:1527/Master";
  private static final String TEMPLATE_TABLE = "TABLE_%06d";

  private static final String TYPE_TABLE = "TABLE";
  private static final String TYPE_VIEW = "VIEW";

  private static final String[] TYPES = new String[] {
      TYPE_TABLE,
      TYPE_VIEW
  };

  /**
   * Return the DataWarehouse singleton using the Bill Pugh approach.
   */
  public static DataWarehouse getInstance()
  {
    return DataWarehouseHolder.INSTANCE;
  }

  private Connection masterConnection;

  private DataWarehouse()
  {
    masterConnection = Jdbc.open(MASTER_URL);
    dropOrphanTables();
  }

  public TreeModel createConnection(String connectionName, String url, String userName, String password, String description)
  {
    createPersistentConnection(connectionName, url, userName, password, description);
    TreeModel masterTree = getMasterTree();
    return masterTree;
  }

  public void createPersistentConnection(String connectionName, String url, String userName, String password, String description)
  {
    if (isEmpty(connectionName))
    {
      throw new RuntimeException("Missing user defined connection name");
    }

    if (isEmpty(url))
    {
      throw new RuntimeException("Missing database JDBC URL");
    }

    ConnectionDescriptor connectionDescriptor = getConnectionDescriptor(connectionName);
    if (connectionDescriptor != null)
    {
      throw new RuntimeException("Connection " + connectionName + " is already defined");
    }

    Connection connection = Jdbc.open(url, userName, password);
    Jdbc.close(connection);

    saveDatabaseConnection(connectionName, url, userName, password, description);
  }

  private void deleteConnection(String connectionName)
  {
    StringBuilder s = new StringBuilder();
    s.append("delete\nfrom ");
    s.append(MASTER_SCHEMA_CONFIGURATION);
    s.append(".");
    s.append(MASTER_TABLE_CONNECTIONS);
    s.append("\nwhere connection_name = ?");

    PreparedStatement preparedStatement = Jdbc.createPreparedStatement(masterConnection, s, connectionName);
    try
    {
      Jdbc.execute(preparedStatement);
    }
    finally
    {
      Jdbc.close(preparedStatement);
    }
  }

  public TreeModel deleteConnections(List<String> connectionNames)
  {
    for (String connectionName : connectionNames)
    {
      deleteConnection(connectionName);
    }
    TreeModel masterTree = getMasterTree();
    return masterTree;
  }

  public void deleteTable(int tableId)
  {
    StringBuilder deleteSql = new StringBuilder();
    deleteSql.append("delete from ");
    deleteSql.append(MASTER_SCHEMA_CONFIGURATION);
    deleteSql.append(".");
    deleteSql.append(MASTER_TABLE_TABLES);
    deleteSql.append("\nwhere id = ?");

    PreparedStatement deleteStatement = Jdbc.createPreparedStatement(masterConnection, deleteSql);
    try
    {
      Jdbc.execute(deleteStatement, tableId);
    }
    finally
    {
      Jdbc.close(deleteStatement);
    }

    String tableName = getTableName(tableId);

    StringBuilder dropSql = new StringBuilder();
    dropSql.append("drop table ");
    dropSql.append(MASTER_SCHEMA_DATA);
    dropSql.append(".");
    dropSql.append(tableName);

    PreparedStatement dropStatement = Jdbc.prepareStatement(masterConnection, dropSql.toString());
    try
    {
      Jdbc.execute(dropStatement);
    }
    finally
    {
      Jdbc.close(dropStatement);
    }
  }

  public TreeModel deleteTables(List<Integer> tableIds)
  {
    for (Integer tableId : tableIds)
    {
      deleteTable(tableId);
    }
    TreeModel masterTree = getMasterTree();
    return masterTree;
  }

  private void dropOrphanTables()
  {
    HashSet<String> tables = getTables();
    DatabaseMetaData metaData = Jdbc.getMetaData(masterConnection);
    ResultSet resultSet = Jdbc.getTables(metaData, null, MASTER_SCHEMA_DATA, null, TYPES);
    try
    {
      while (Jdbc.next(resultSet))
      {
        String tableName = Jdbc.getString(resultSet, 3);
        if (!tables.contains(tableName))
        {
          System.out.println("DataWarehouse.dropOrphanTables: dropping " + tableName);
          dropTable(tableName);
        }
      }
    }
    finally
    {
      Jdbc.close(resultSet);
    }

  }

  public void dropTable(String tableName)
  {
    PreparedStatement dropDataTable = Jdbc.createPreparedStatement(masterConnection, "drop table " + MASTER_SCHEMA_DATA + "." + tableName);
    try
    {
      Jdbc.execute(dropDataTable);
    }
    finally
    {
      Jdbc.close(dropDataTable);
    }
  }

  /**
   * Assumes insertName is presented in ascending order and childOffset is one
   * more than offset of last match (first possible match)
   */
  private int findInsertOffset(TreeModel parentItem, String insertName, int childOffset)
  {
    int childCount = parentItem.getChildCount();
    while (childOffset < childCount)
    {
      ModelData childItem = parentItem.getChild(childOffset);
      String childName = childItem.get(Keys.NAME);
      if (childName.compareTo(insertName) > 0)
      {
        return childOffset;
      }
      childOffset++;
    }
    return childOffset;
  }

  private TreeModel findNode(TreeModel parent, String name, String value)
  {
    for (ModelData child : parent.getChildren())
    {
      if (child.get(name).equals(value))
      {
        return (TreeModel)child;
      }
    }
    return null;
  }

  private String formatTableReference(TableReference tableReference)
  {
    String schemaName;
    String tableName;

    if (tableReference.isInternal())
    {
      int tableId = tableReference.getTableId();
      tableName = getTableName(tableId);
      schemaName = MASTER_SCHEMA_DATA;
    }
    else
    {
      schemaName = tableReference.getSchemaName();
      tableName = tableReference.getTableName();
    }

    StringBuilder s = new StringBuilder();

    s.append(schemaName);
    s.append(".");
    s.append(tableName);

    return s.toString();
  }

  public int getColumnCount(Connection connection, String schemaName, String tableName)
  {
    int columnCount = 0;
    DatabaseMetaData databaseMetaData = Jdbc.getMetaData(connection);
    ResultSet columns = Jdbc.getColumns(databaseMetaData, null, schemaName, tableName, null);
    try
    {
      while (Jdbc.next(columns))
      {
        columnCount++;
      }
      return columnCount;
    }
    finally
    {
      Jdbc.close(columns);
    }
  }

  public List<ColumnDefinition> getColumnDefinitions(Connection connection, String schemaName, String tableName)
  {
    DatabaseMetaData databaseMetaData = Jdbc.getMetaData(connection);
    ResultSet resultSet = Jdbc.getColumns(databaseMetaData, null, schemaName.toUpperCase(), tableName.toUpperCase(), null);
    try
    {
      List<ColumnDefinition> columnDefinitions = new LinkedList<ColumnDefinition>();
      while (Jdbc.next(resultSet))
      {
        String tableCatalog = Jdbc.getString(resultSet, 1);
        String columnName = Jdbc.getString(resultSet, 4);
        int dataType = Jdbc.getInt(resultSet, 5);
        String typeName = Jdbc.getString(resultSet, 6);
        int columnSize = Jdbc.getInt(resultSet, 7);
        int decimalDigits = Jdbc.getInt(resultSet, 9);
        String remarks = Jdbc.getString(resultSet, 12);
        String defaultValue = Jdbc.getString(resultSet, 13);
        int characterOctetLength = Jdbc.getInt(resultSet, 16);
        int ordinalPosition = Jdbc.getInt(resultSet, 17);
        String nullable = Jdbc.getString(resultSet, 18);
        String autoIncrement = Jdbc.getString(resultSet, 23);
        ColumnDefinition columnDefinition = new ColumnDefinition(tableCatalog, columnName, dataType, typeName, columnSize, decimalDigits, remarks, defaultValue, characterOctetLength, ordinalPosition, nullable, autoIncrement);
        columnDefinitions.add(columnDefinition);
      }
      // TODO: Add foreign key relationships
      return columnDefinitions;
    }
    finally
    {
      Jdbc.close(resultSet);
    }
  }

  public List<ColumnDefinition> getColumnDefinitions(TableReference tableReference)
  {
    List<ColumnDefinition> columnDefinitions;
    if (tableReference.isInternal())
    {
      int tableId = tableReference.getTableId();
      String tableName = getTableName(tableId);
      columnDefinitions = getColumnDefinitions(masterConnection, MASTER_SCHEMA_DATA, tableName);
    }
    else
    {
      String connectionName = tableReference.getConnectionName();
      ConnectionDescriptor connectionDescriptor = lookupExistingConnectionName(connectionName);
      Connection connection = openDatabase(connectionDescriptor);
      try
      {
        String schemaName = tableReference.getSchemaName();
        String tableName = tableReference.getTableName();
        columnDefinitions = getColumnDefinitions(connection, schemaName, tableName);
      }
      finally
      {
        Jdbc.close(connection);
      }
    }
    return columnDefinitions;
  }

  public ArrayList<ColumnDescriptor> getColumnDescriptors(ResultSet resultSet)
  {
    ArrayList<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
    ResultSetMetaData resultSetMetaData = Jdbc.getMetaData(resultSet);
    int columnCount = Jdbc.getColumnCount(resultSetMetaData);
    for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++)
    {
      ColumnDescriptor columnDescriptor = new ColumnDescriptor();
      columnDescriptor.set(ColumnDescriptor.COLUMN_NAME, Jdbc.getColumnName(resultSetMetaData, columnNumber));
      columnDescriptor.set(ColumnDescriptor.DATA_TYPE, Jdbc.getColumnType(resultSetMetaData, columnNumber));
      columnDescriptors.add(columnDescriptor);
    }
    return columnDescriptors;
  }

  public ConnectionDescriptor getConnectionDescriptor(String connectionName)
  {
    ConnectionDescriptor connectionDescriptor = null;

    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select id, url, user_name, password\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_CONNECTIONS);
    selectSql.append("\n");
    selectSql.append("where connection_name = ?");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql, connectionName);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      if (Jdbc.next(resultSet))
      {
        int connectionId = Jdbc.getInt(resultSet, 1);
        String url = Jdbc.getString(resultSet, 2);
        String userName = Jdbc.getString(resultSet, 3);
        String password = Jdbc.getString(resultSet, 4);
        connectionDescriptor = new ConnectionDescriptor(connectionId, url, userName, password);
      }
    }
    finally
    {
      if (selectStatement != null)
      {
        Jdbc.close(selectStatement);
      }
    }

    return connectionDescriptor;
  }

  private String getCreateSql(String tableName, ResultSet resultSet, List<ColumnReference> columnReferences)
  {
    StringBuilder s = new StringBuilder();

    s.append("create table ");
    s.append(MASTER_SCHEMA_DATA);

    s.append(".");
    s.append(tableName);
    s.append("\n(");

    ResultSetMetaData resultSetMetaData = Jdbc.getMetaData(resultSet);
    int columnCount = Jdbc.getColumnCount(resultSetMetaData);

    for (int columnOffset = 0, columnNumber = 1; columnOffset < columnCount; columnOffset++, columnNumber++)
    {
      String columnName = Jdbc.getColumnName(resultSetMetaData, columnNumber);
      int dataType = Jdbc.getColumnType(resultSetMetaData, columnNumber);
      int precision = Jdbc.getPrecision(resultSetMetaData, columnNumber);
      int scale = Jdbc.getScale(resultSetMetaData, columnNumber);

      String typeName = null;

      switch (dataType)
      {
        case Types.BIGINT:
          typeName = "bigint";
          break;
        case Types.CHAR:
          typeName = "char(" + precision + ")";
          break;
        case Types.DATE:
          typeName = "date";
          break;
        case Types.DECIMAL:
          typeName = "decimal(" + precision + "," + scale + ")";
          break;
        case Types.DOUBLE:
          typeName = "double";
          break;
        case Types.FLOAT:
          typeName = "float(" + precision + ")";
          break;
        case Types.INTEGER:
          typeName = "integer";
          break;
        case Types.LONGVARCHAR:
          typeName = "long varchar";
          break;
        case Types.NUMERIC:
          typeName = "numeric(" + precision + "," + scale + ")";
          break;
        case Types.REAL:
          typeName = "real";
          break;
        case Types.SMALLINT:
          typeName = "smallint";
          break;
        case Types.VARCHAR:
          typeName = "varchar(" + precision + ")";
          break;
        case Types.TIME:
          typeName = "time";
          break;
        case Types.TIMESTAMP:
          typeName = "timestamp";
          break;
      }

      if (typeName != null)
      {
        if (columnOffset == 0)
        {
          s.append("\n  ");
        }
        else
        {
          s.append(",\n  ");
        }

        s.append("\"");
        s.append(columnName);
        s.append("\"");
        s.append(" ");
        s.append(typeName);

        ColumnReference columnReference = new ColumnReference(columnName, columnNumber);
        columnReferences.add(columnReference);
      }

    }

    s.append("\n)\n");

    return s.toString();
  }

  public TreeModel getExternalConnections()
  {
    TreeModel connectionRoot = new BaseTreeModel();
    connectionRoot.set(Keys.NAME, Keys.CONNECTIONS);
    connectionRoot.set(Keys.NODE_TYPE, NodeType.CONNECTION_ROOT);
    connectionRoot.set(Keys.DATE, new Date());

    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select \n");
    selectSql.append("  id,\n");
    selectSql.append("  connection_name,\n");
    selectSql.append("  url,\n");
    selectSql.append("  user_name,\n");
    selectSql.append("  password\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_CONNECTIONS);
    selectSql.append("\n");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      while (Jdbc.next(resultSet))
      {
        int id = Jdbc.getInt(resultSet, 1);
        String connectionName = Jdbc.getString(resultSet, 2);
        String url = Jdbc.getString(resultSet, 3);
        String userName = Jdbc.getString(resultSet, 4);
        String password = Jdbc.getString(resultSet, 5);
        TreeModel connectionItem = new BaseTreeModel(connectionRoot);
        connectionItem.set(Keys.NAME, connectionName);
        try
        {
          Connection connection = Jdbc.open(url, userName, password);
          try
          {
            connectionItem.set(Keys.NODE_TYPE, NodeType.CONNECTION);
            getExternalSchemas(connection, connectionItem, connectionName);
          }
          finally
          {
            Jdbc.close(connection);
          }
        }
        catch (SqlRuntimeException e)
        {
          connectionItem.set(Keys.NODE_TYPE, NodeType.CONNECTION_ERROR);
        }
        getInternalSchemas(connectionItem, id);
      }
      return connectionRoot;
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
  }

  private void getExternalSchemas(Connection connection, TreeModel connectionItem, String connectionName)
  {
    DatabaseMetaData databaseMetaData = Jdbc.getMetaData(connection);
    ResultSet schemas = Jdbc.getSchemas(databaseMetaData);
    try
    {
      while (Jdbc.next(schemas))
      {
        String schemaName = Jdbc.getString(schemas, 1);
        TreeModel child = new BaseTreeModel();
        getExternalTables(connection, child, schemaName);
        if (child.getChildCount() > 0)
        {
          child.set(Keys.NAME, schemaName);
          child.set(Keys.NODE_TYPE, NodeType.SCHEMA);
          connectionItem.add(child);
        }
      }
    }
    finally
    {
      Jdbc.close(schemas);
    }
  }

  private void getExternalTables(Connection connection, TreeModel child, String schemaName)
  {
    DatabaseMetaData databaseMetaData = Jdbc.getMetaData(connection);
    ResultSet tables = Jdbc.getTables(databaseMetaData, null, schemaName, null, TYPES);
    try
    {
      while (Jdbc.next(tables))
      {
        String tableName = Jdbc.getString(tables, 3);
        String tableType = Jdbc.getString(tables, 4);
        String remarks = Jdbc.getString(tables, 5);

        NodeType nodeType = tableType.equals(TYPE_TABLE) ? NodeType.EXTERNAL_TABLE : NodeType.EXTERNAL_VIEW;
        int rowCount = getRowCount(connection, schemaName, tableName);
        int columnCount = getColumnCount(connection, schemaName, tableName);

        TreeModel tableTreeModel = new BaseTreeModel(child);
        tableTreeModel.set(Keys.NODE_TYPE, nodeType);
        tableTreeModel.set(Keys.NAME, tableName);
        tableTreeModel.set(Keys.ROW_COUNT, rowCount);
        tableTreeModel.set(Keys.COLUMN_COUNT, columnCount);
        tableTreeModel.set(Keys.REMARKS, remarks);
        tableTreeModel.set(Keys.DATE, new Date());
      }

      sort(child, Keys.NAME);
    }
    finally
    {
      Jdbc.close(tables);
    }
  }

  private String getGenerationName(String tableName, int generation)
  {
    String generationName;
    if (generation == 0)
    {
      generationName = tableName;
    }
    else
    {
      generationName = tableName + Constants.NUMBER_DELIMITER + generation;
    }
    return generationName;
  }

  private String getInsertSql(int tableId, List<ColumnReference> columnReferences)
  {
    String tableName = getTableName(tableId);

    StringBuilder s = new StringBuilder();
    StringBuilder t = new StringBuilder();

    s.append("insert into ");

    s.append(MASTER_SCHEMA_DATA);
    s.append(".");
    s.append(tableName);
    s.append("\n(");

    int columnCount = 0;
    for (ColumnReference columnReference : columnReferences)
    {
      if (columnCount++ == 0)
      {
        s.append("\n  ");
      }
      else
      {
        s.append(",\n  ");
        t.append(", ");
      }
      s.append("\"");
      s.append(columnReference.getName());
      s.append("\"");
      t.append("?");
    }

    s.append("\n)\nvalues\n(\n  ");
    s.append(t);
    s.append("\n)");

    return s.toString();
  }

  private void getInternalSchemas(TreeModel connectionItem, int connectionId)
  {
    StringBuilder selectSql = new StringBuilder();

    selectSql.append("select\n");
    selectSql.append("  id,\n");
    selectSql.append("  schema_name\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_SCHEMAS);
    selectSql.append("\n");
    selectSql.append("where connection_id = ?");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql, connectionId);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      while (Jdbc.next(resultSet))
      {
        int schemaId = Jdbc.getInt(resultSet, 1);
        String schemaName = Jdbc.getString(resultSet, 2);
        TreeModel schemaItem = findNode(connectionItem, Keys.NAME, schemaName);
        if (schemaItem == null)
        {
          schemaItem = new BaseTreeModel();
          schemaItem.set(Keys.NAME, schemaName);
          schemaItem.set(Keys.NODE_TYPE, NodeType.SCHEMA);
          getInternalTables(schemaItem, schemaId);
          if (schemaItem.getChildCount() > 0)
          {
            connectionItem.add(schemaItem);
          }
        }
        else
        {
          getInternalTables(schemaItem, schemaId);
        }
      }
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
  }

  private void getInternalTables(TreeModel schemaItem, int schemaId)
  {
    StringBuilder selectSql = new StringBuilder();

    selectSql.append("select\n");
    selectSql.append("  id,\n");
    selectSql.append("  generation,\n");
    selectSql.append("  source_table_name,\n");
    selectSql.append("  row_count,\n");
    selectSql.append("  column_count,\n");
    selectSql.append("  import_time\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_TABLES);
    selectSql.append("\n");
    selectSql.append("where schema_id = ?\n");
    selectSql.append("order by source_table_name, generation");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql, schemaId);
    try
    {
      int insertOffset = -1;
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      while (Jdbc.next(resultSet))
      {
        int tableId = Jdbc.getInt(resultSet, 1);
        int generation = Jdbc.getInt(resultSet, 2);
        String sourceTableName = Jdbc.getString(resultSet, 3);
        int rowCount = Jdbc.getInt(resultSet, 4);
        int columnCount = Jdbc.getInt(resultSet, 5);
        Date importTime = Jdbc.getTimestamp(resultSet, 6);
        String tableName = getGenerationName(sourceTableName, generation);

        TreeModel tableTreeModel = new BaseTreeModel();
        tableTreeModel.set(Keys.NODE_TYPE, NodeType.INTERNAL_TABLE);
        tableTreeModel.set(Keys.NAME, tableName);
        tableTreeModel.set(Keys.TABLE_ID, tableId);
        tableTreeModel.set(Keys.GENERATION, generation);
        tableTreeModel.set(Keys.ROW_COUNT, rowCount);
        tableTreeModel.set(Keys.COLUMN_COUNT, columnCount);
        tableTreeModel.set(Keys.DATE, importTime);

        insertOffset = findInsertOffset(schemaItem, sourceTableName, insertOffset + 1);
        schemaItem.insert(tableTreeModel, insertOffset);
      }
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
  }

  public TreeModel getMasterTree()
  {
    TreeModel masterTree = new BaseTreeModel();
    TreeModel connectionTree = getExternalConnections();
    masterTree.add(connectionTree);
    return masterTree;
  }

  public PageResult getPage(PagingLoadConfig pagingLoadConfig, ResultSet resultSet)
  {
    int pageRowLimit = pagingLoadConfig.getLimit();
    int pageRowOffset = pagingLoadConfig.getOffset();
    List<ModelData> dataList = new LinkedList<ModelData>();
    ResultSetMetaData metaData = Jdbc.getMetaData(resultSet);
    int columnCount = Jdbc.getColumnCount(metaData);
    if (pageRowOffset != 0)
    {
      Jdbc.absolute(resultSet, pageRowOffset);
    }
    int rowCount = 0;
    while (Jdbc.next(resultSet) && rowCount++ < pageRowLimit)
    {
      String[] data = new String[columnCount];
      for (int columnOffset = 0, columnNumber = 1; columnOffset < columnCount; columnOffset++, columnNumber++)
      {
        data[columnOffset] = Jdbc.getString(resultSet, columnNumber);
      }
      ArrayModelData rowData = new ArrayModelData(data);
      dataList.add(rowData);
    }
    Jdbc.last(resultSet);
    int totalLength = Jdbc.getRow(resultSet);
    PageResult pageResult = new PageResult();
    pageResult.setData(dataList);
    pageResult.setOffset(pageRowOffset);
    pageResult.setTotalLength(totalLength);
    return pageResult;
  }

  public PageResult getPage(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig)
  {
    GetPageResultSetProcessor getPageResultSetProcessor = new GetPageResultSetProcessor();
    PageResult pageResult = runQuery(queryDescriptor, pagingLoadConfig, getPageResultSetProcessor);
    return pageResult;
  }

  public String getQuerySql(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig)
  {
    StringBuilder s = new StringBuilder();
    StringBuilder sortClause = new StringBuilder();

    int columnCount = 0;
    List<String> groupByColumns = new LinkedList<String>();
    List<TableReference> tableReferences = queryDescriptor.getTables();
    List<ModelData> columns = queryDescriptor.getColumns();
    String filterCriteria = queryDescriptor.getFilterCriteria();
    boolean isGroupByRequired = false;

    s.append("select distinct");

    if (columns == null)
    {
      s.append(" *");
    }
    else
    {
      for (ModelData column : columns)
      {
        String tableAlias = column.get(Keys.TABLE_ALIAS);
        String columnName = column.get(Keys.COLUMN_NAME);
        String columnAlias = column.get(Keys.COLUMN_ALIAS);
        SortDirection sortDirection = column.get(Keys.SORT_DIRECTION);
        SummaryOperation summaryOperation = column.get(Keys.SUMMARY_OPERATION);

        boolean isFormulaColumn = tableAlias.equals(Values.FORMULA_TABLE_ALIAS);
        boolean isSummaryOperation = summaryOperation != SummaryOperation.NONE;
        boolean isNameChange = !columnName.equals(columnAlias);

        String qualifiedColumnName;
        if (isFormulaColumn)
        {
          qualifiedColumnName = columnName;
        }
        else
        {
          qualifiedColumnName = tableAlias + "." + columnName;
        }

        if (columnCount++ == 0)
        {
          s.append("\n  ");
        }
        else
        {
          s.append(",\n  ");
        }

        if (sortDirection != SortDirection.NONE)
        {
          if (sortClause.length() == 0)
          {
            sortClause.append("\norder by ");
          }
          else
          {
            sortClause.append(", ");
          }
          sortClause.append(columnCount);
          switch (sortDirection)
          {
            case ASCENDING:
              sortClause.append(" asc");
              break;
            case DESCENDING:
              sortClause.append(" desc");
              break;
          }
        }

        switch (summaryOperation)
        {
          case NONE:
            groupByColumns.add(qualifiedColumnName);
            s.append(qualifiedColumnName);
            break;
          case COUNT_DISTINCT:
            isGroupByRequired = true;
            s.append("COUNT(DISTINCT ");
            s.append(qualifiedColumnName);
            s.append(")");
            break;
          default:
            isGroupByRequired = true;
            s.append(summaryOperation.name());
            s.append("(");
            s.append(qualifiedColumnName);
            s.append(")");
            break;
        }

        if (isFormulaColumn || isSummaryOperation || isNameChange)
        {
          s.append(" as \"");
          s.append(columnAlias);
          s.append("\"");
        }
      }
    }

    s.append("\nfrom ");

    int tableCount = 0;

    List<ModelData> joins = queryDescriptor.getJoins();
    if (joins == null || joins.size() == 0)
    {
      for (TableReference tableReference : tableReferences)
      {
        if (tableCount++ == 0)
        {
          s.append("\n  ");
        }
        else
        {
          s.append(",\n  ");
        }

        String formattedTableReference = formatTableReference(tableReference);
        s.append(formattedTableReference);

        String tableAlias = tableReference.getTableAlias();
        if (tableAlias != null)
        {
          s.append(" as ");
          s.append(tableAlias);
        }
      }
    }
    else
    {
      for (ModelData modelData : joins)
      {
        String leftTableAlias = modelData.get(Keys.JOIN_LEFT_TABLE_ALIAS);
        String leftColumnName = modelData.get(Keys.JOIN_LEFT_COLUMN_NAME);
        TableReference leftTableReference = getTableReference(tableReferences, leftTableAlias);
        String leftFormattedTableReference = formatTableReference(leftTableReference);

        String rightTableAlias = modelData.get(Keys.JOIN_RIGHT_TABLE_ALIAS);
        String rightColumnName = modelData.get(Keys.JOIN_RIGHT_COLUMN_NAME);
        TableReference rightTableReference = getTableReference(tableReferences, rightTableAlias);
        String rightFormattedTableReference = formatTableReference(rightTableReference);

        JoinType joinType = modelData.get(Keys.JOIN_TYPE);
        Condition condition = modelData.get(Keys.JOIN_CONDITION);

        s.append("\n  ");

        if (tableCount++ == 0)
        {
          s.append(leftFormattedTableReference);
          s.append(" as ");
          s.append(leftTableAlias);
        }

        s.append("\n");
        s.append(EnumText.getInstance().get(joinType));
        s.append(" ");

        s.append(rightFormattedTableReference);
        s.append(" as ");
        s.append(rightTableAlias);

        s.append("\n");
        s.append("on ");

        s.append(leftTableAlias);
        s.append(".");
        s.append(leftColumnName);
        s.append(" ");
        s.append(EnumText.getInstance().get(condition));
        s.append(" ");
        s.append(rightTableAlias);
        s.append(".");
        s.append(rightColumnName);
      }
    }

    if (filterCriteria != null && filterCriteria.trim().length() > 0)
    {
      s.append("\nwhere ");
      s.append(filterCriteria);
    }

    if (isGroupByRequired)
    {
      int groupByColumnCount = 0;
      for (String groupByColumn : groupByColumns)
      {
        if (groupByColumnCount++ == 0)
        {
          s.append("\ngroup by ");
        }
        else
        {
          s.append(", ");
        }
        s.append(groupByColumn);
      }
    }

    if (pagingLoadConfig != null)
    {
      SortDir sortDir = pagingLoadConfig.getSortDir();
      if (sortDir != SortDir.NONE)
      {
        int columnNumber = Integer.parseInt(pagingLoadConfig.getSortField()) + 1;
        sortClause = new StringBuilder(); // override any existing sort clause
        sortClause.append("\norder by ");
        sortClause.append(columnNumber);
        sortClause.append(" ");
        sortClause.append(sortDir.name());
      }
    }

    if (sortClause.length() > 0)
    {
      s.append(sortClause);
    }

    //  if (pagingLoadConfig != null)
    //  {
    //    int limit = pagingLoadConfig.getLimit();
    //    int offset = pagingLoadConfig.getOffset();
    //    s.append("\noffset ");
    //    s.append(offset);
    //    s.append(" rows\nfetch next ");
    //    s.append(limit);
    //    s.append(" rows only");
    //  }

    System.out.println("DataWarehouse.getQuerySql: s=" + s);

    return s.toString();
  }

  public int getRowCount(Connection connection, String schemaName, String tableName)
  {
    int rowCount = 0;

    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select count(*)\n");
    selectSql.append("from ");
    selectSql.append(schemaName);
    selectSql.append(".");
    selectSql.append(tableName);

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(connection, selectSql);
    try
    {
      ResultSet rows = Jdbc.executeQuery(selectStatement);
      if (Jdbc.next(rows))
      {
        rowCount = Jdbc.getInt(rows, 1);
      }
      return rowCount;
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
  }

  private Integer getSchemaId(int connectionId, String schemaName)
  {
    Integer schemaId = null;
    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select id\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_SCHEMAS);
    selectSql.append("\n");
    selectSql.append("where connection_id = ?\n");
    selectSql.append("and schema_name = ?");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql, connectionId, schemaName);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      if (Jdbc.next(resultSet))
      {
        schemaId = Jdbc.getInt(resultSet, 1);
      }
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
    return schemaId;
  }

  public Integer getTableId(int schemaId, String sourceTableName, int generation)
  {
    int tableId = -1;
    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select\n");
    selectSql.append("  id\n");
    selectSql.append("from ");
    selectSql.append(MASTER_SCHEMA_CONFIGURATION);
    selectSql.append(".");
    selectSql.append(MASTER_TABLE_TABLES);
    selectSql.append("\n");
    selectSql.append("where schema_id = ?\n");
    selectSql.append("and source_table_name = ?\n");
    selectSql.append("and generation = ?");

    PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, selectSql, schemaId, sourceTableName, generation);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(selectStatement);
      if (Jdbc.next(resultSet))
      {
        tableId = Jdbc.getInt(resultSet, 1);
      }
    }
    finally
    {
      Jdbc.close(selectStatement);
    }
    return tableId;
  }

  public String getTableName(int tableId)
  {
    String tableName = String.format(TEMPLATE_TABLE, tableId);
    return tableName;
  }

  private TableReference getTableReference(List<TableReference> tableReferences, String tableAlias)
  {
    for (TableReference tableReference : tableReferences)
    {
      if (tableAlias.equals(tableReference.getTableAlias()))
      {
        return tableReference;
      }
    }
    return null;
  }

  public HashSet<String> getTables()
  {
    HashSet<String> tables = new HashSet<String>();

    StringBuilder s = new StringBuilder();
    s.append("select id\n");
    s.append("from ");
    s.append(MASTER_SCHEMA_CONFIGURATION);
    s.append(".");
    s.append(MASTER_TABLE_TABLES);

    PreparedStatement select = Jdbc.createPreparedStatement(masterConnection, s);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(select);
      while (Jdbc.next(resultSet))
      {
        int id = Jdbc.getInt(resultSet, 1);
        String tableName = getTableName(id);
        tables.add(tableName);
      }
      return tables;
    }
    finally
    {
      Jdbc.close(select);
    }
  }

  public int importTable(TableReference tableReference, boolean isGenerateVersion)
  {
    int tableId;

    String connectionName = tableReference.getConnectionName();
    String schemaName = tableReference.getSchemaName();
    String tableName = tableReference.getTableName();

    StringBuilder selectSql = new StringBuilder();
    selectSql.append("select *\n");
    selectSql.append("from ");
    selectSql.append(schemaName);
    selectSql.append(".");
    selectSql.append(tableName);

    ConnectionDescriptor connectionDescriptor = getConnectionDescriptor(connectionName);
    Connection connection = openDatabase(connectionDescriptor);
    try
    {
      int connectionId = connectionDescriptor.getConnectionId();
      int schemaId = realizeSchemaId(connectionId, schemaName);
      tableId = realizeTableId(schemaId, tableName, isGenerateVersion);

      PreparedStatement selectStatement = Jdbc.createPreparedStatement(connection, selectSql);
      try
      {
        ResultSet resultSet = Jdbc.executeQuery(selectStatement);
        saveResultSet(resultSet, tableId);
      }
      finally
      {
        Jdbc.close(selectStatement);
      }
    }
    finally
    {
      Jdbc.close(connection);
    }

    return tableId;
  }

  public TreeModel importTables(List<TableReference> tableReferences, boolean isGenerateVersion)
  {
    int tableId = 0;
    for (TableReference tableReference : tableReferences)
    {
      tableId = importTable(tableReference, isGenerateVersion);
    }
    TreeModel masterTree = getMasterTree();
    masterTree.set(Keys.NEW_TABLE_ID, tableId);
    return masterTree;
  }

  private int insertTable(Connection masterConnection, int schemaId, int generation, String sourceTableName)
  {
    int tableId = 0;
    StringBuilder insertSql = new StringBuilder();

    insertSql.append("insert into ");
    insertSql.append(MASTER_SCHEMA_CONFIGURATION);
    insertSql.append(".");
    insertSql.append(MASTER_TABLE_TABLES);
    insertSql.append("\n");
    insertSql.append("(\n");
    insertSql.append("  schema_id,\n");
    insertSql.append("  generation,\n");
    insertSql.append("  source_table_name\n");
    insertSql.append(")\n");
    insertSql.append("values(?, ?, ?)");

    PreparedStatement insertStatement = Jdbc.createPreparedStatement(masterConnection, insertSql, schemaId, generation, sourceTableName);
    try
    {
      tableId = Jdbc.executeInsert(masterConnection, insertStatement);
    }
    finally
    {
      Jdbc.close(insertStatement);
    }
    return tableId;
  }

  public final boolean isEmpty(String name)
  {
    return name == null || name.isEmpty();
  }

  public ConnectionDescriptor lookupExistingConnectionName(String connectionName)
  {
    ConnectionDescriptor connectionDescriptor = getConnectionDescriptor(connectionName);
    if (connectionDescriptor == null)
    {
      throw new RuntimeException("Connection " + connectionName + " does not exist.");
    }
    return connectionDescriptor;
  }

  public Connection openDatabase(ConnectionDescriptor connectionDescriptor)
  {
    String url = connectionDescriptor.getUrl();
    String userName = connectionDescriptor.getUserName();
    String password = connectionDescriptor.getPassword();
    Connection connection = Jdbc.open(url, userName, password);
    Jdbc.setReadOnly(connection, true);
    return connection;
  }

  public Connection openDatabase(String connectionName)
  {
    ConnectionDescriptor connectionDescriptor = lookupExistingConnectionName(connectionName);
    Connection connection = openDatabase(connectionDescriptor);
    return connection;
  }

  private int realizeSchemaId(final int connectionId, final String schemaName)
  {
    Callable<Integer> transaction = new SchemaTransaction(connectionId, schemaName);
    int schemaId = Jdbc.executeTransaction(masterConnection, transaction);
    return schemaId;
  }

  private int realizeTableId(int schemaId, String tableName, boolean isGenerateVersion)
  {
    Callable<Integer> transaction = new TableTransaction(schemaId, tableName, isGenerateVersion);
    int tableId = Jdbc.executeTransaction(masterConnection, transaction);
    return tableId;
  }

  public QueryResult runQuery(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig)
  {
    RunQueryResultSetProcessor runQueryResultSetProcessor = new RunQueryResultSetProcessor();
    QueryResult queryResult = runQuery(queryDescriptor, pagingLoadConfig, runQueryResultSetProcessor);
    return queryResult;
  }

  public <C> C runQuery(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig, ResultSetProcessor<C> resultSetProcessor)
  {
    Connection connection;
    TableReference tableReference = queryDescriptor.getTables().get(0);
    if (tableReference.isInternal())
    {
      connection = masterConnection;
    }
    else
    {
      String connectionName = tableReference.getConnectionName();
      connection = openDatabase(connectionName);
    }

    try
    {
      String querySql = getQuerySql(queryDescriptor, pagingLoadConfig);

      PreparedStatement preparedStatement = Jdbc.prepareStatement(connection, querySql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      try
      {
        ResultSet resultSet = Jdbc.executeQuery(preparedStatement);
        C result = resultSetProcessor.process(queryDescriptor, pagingLoadConfig, resultSet);
        return result;
      }
      finally
      {
        Jdbc.close(preparedStatement);
      }
    }
    finally
    {
      if (!tableReference.isInternal())
      {
        Jdbc.close(connection);
      }
    }
  }

  private void saveDatabaseConnection(String connectionName, String url, String userName, String password, String description)
  {
    StringBuilder s = new StringBuilder();

    s.append("insert into ");
    s.append(MASTER_SCHEMA_CONFIGURATION);
    s.append(".");
    s.append(MASTER_TABLE_CONNECTIONS);
    s.append("(\n");
    s.append("  connection_name,\n");
    s.append("  url,\n");
    s.append("  user_name,\n");
    s.append("  password,\n");
    s.append("  description\n");
    s.append(")\n");
    s.append("values(?, ?, ?, ?, ?)");

    PreparedStatement preparedStatement = Jdbc.createPreparedStatement(masterConnection, s, connectionName, url, userName, password, description);
    try
    {
      Jdbc.execute(preparedStatement);
    }
    finally
    {
      if (preparedStatement != null)
      {
        Jdbc.close(preparedStatement);
      }
    }
  }

  public void saveResultSet(ResultSet resultSet, int tableId)
  {
    String tableName = getTableName(tableId);
    List<ColumnReference> columnReferences = new LinkedList<ColumnReference>();
    String createSql = getCreateSql(tableName, resultSet, columnReferences);
    PreparedStatement createPreparedStatement = Jdbc.createPreparedStatement(masterConnection, createSql);
    try
    {
      Jdbc.execute(createPreparedStatement);
    }
    finally
    {
      Jdbc.close(createPreparedStatement);
    }

    int rowCount = 0;
    String insertSql = getInsertSql(tableId, columnReferences);
    PreparedStatement insertPreparedStatement = Jdbc.createPreparedStatement(masterConnection, insertSql);
    try
    {
      ParameterMetaData parameterMetaData = Jdbc.getParameterMetaData(insertPreparedStatement);
      while (Jdbc.next(resultSet))
      {
        rowCount++;
        int parameterColumnNumber = 1;
        for (ColumnReference columnReference : columnReferences)
        {
          int resultSetColumnNumber = columnReference.getNumber();
          Object value = Jdbc.getObject(resultSet, resultSetColumnNumber);
          if (value == null)
          {
            int parameterType = Jdbc.getParameterType(parameterMetaData, parameterColumnNumber);
            Jdbc.setNull(insertPreparedStatement, parameterColumnNumber, parameterType);
          }
          else
          {
            Jdbc.setObject(insertPreparedStatement, parameterColumnNumber, value);
          }
          parameterColumnNumber++;
        }
        Jdbc.execute(insertPreparedStatement);
      }
    }
    finally
    {
      Jdbc.close(insertPreparedStatement);
    }

    int columnCount = columnReferences.size();
    setRowColumnCount(tableId, rowCount, columnCount);
  }

  private void setRowColumnCount(int tableId, int rowCount, int columnCount)
  {
    StringBuilder s = new StringBuilder();
    s.append("update ");
    s.append(MASTER_SCHEMA_CONFIGURATION);
    s.append(".");
    s.append(MASTER_TABLE_TABLES);
    s.append("\n");
    s.append("set\n");
    s.append("  row_count = ?,\n");
    s.append("  column_count = ?,\n");
    s.append("  import_time = current timestamp\n");
    s.append("where id = ?");

    PreparedStatement updateStatement = Jdbc.createPreparedStatement(masterConnection, s, rowCount, columnCount, tableId);
    try
    {
      Jdbc.execute(updateStatement);
    }
    finally
    {
      Jdbc.close(updateStatement);
    }
  }

  /**
   * Sort the children of a TreeModel into order based on the value of the
   * property with the supplied key name.
   * <p/>
   * Note that, as an optimization, this method exploits knowledge of the
   * implementation of BaseTreeModel.getChildren(), which returns a reference to
   * the actual child list. If this implementation happens to change (i.e. to
   * return a copy of the list), the effect will be that the default ordering
   * (by Table Type) will prevail. It will then be necessary to store the sorted
   * children back into the parent.
   * 
   * @param key
   */
  @SuppressWarnings({
      "unchecked",
      "rawtypes"
  })
  public void sort(TreeModel child, final String key)
  {
    Collections.sort(child.getChildren(), new Comparator()
    {
      @Override
      public int compare(Object o1, Object o2)
      {
        String n1 = ((ModelData)o1).get(key);
        String n2 = ((ModelData)o2).get(key);
        return n1.compareTo(n2);
      }
    });
  }

  public static class ColumnReference
  {
    private String name;
    private int number;

    public ColumnReference(String name, int number)
    {
      this.name = name;
      this.number = number;
    }

    public String getName()
    {
      return name;
    }

    public int getNumber()
    {
      return number;
    }

  }

  public static class ConnectionDescriptor
  {
    private int connectionId;
    private String password;
    private String url;
    private String userName;

    public ConnectionDescriptor(int connectionId, String url, String userName, String password)
    {
      this.connectionId = connectionId;
      this.url = url;
      this.userName = userName;
      this.password = password;
    }

    public int getConnectionId()
    {
      return connectionId;
    }

    public String getPassword()
    {
      return password;
    }

    public String getUrl()
    {
      return url;
    }

    public String getUserName()
    {
      return userName;
    }

  }

  private static class DataWarehouseHolder
  {
    public static final DataWarehouse INSTANCE = new DataWarehouse();
  }

  public class GetPageResultSetProcessor implements ResultSetProcessor<PageResult>
  {
    @Override
    public PageResult process(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig, ResultSet resultSet)
    {
      PageResult pageResult = getPage(pagingLoadConfig, resultSet);
      return pageResult;
    }
  }

  public interface ResultSetProcessor<C>
  {
    public C process(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig, ResultSet resultSet);
  }

  public class RunQueryResultSetProcessor implements ResultSetProcessor<QueryResult>
  {
    @Override
    public QueryResult process(QueryDescriptor queryDescriptor, PagingLoadConfig pagingLoadConfig, ResultSet resultSet)
    {
      if (isPivot(queryDescriptor))
      {
        resultSet = new PivotResultSet(resultSet, queryDescriptor);
      }
      ArrayList<ColumnDescriptor> columnDescriptors = getColumnDescriptors(resultSet);
      PageResult pageResult = getPage(pagingLoadConfig, resultSet);
      QueryResult queryResult = new QueryResult(columnDescriptors, pageResult);
      return queryResult;
    }

    private boolean isPivot(QueryDescriptor queryDescriptor)
    {
      List<ModelData> columns = queryDescriptor.getColumns();
      if (columns != null)
      {
        for (ModelData column : columns)
        {
          PivotColumn pivotColumn = column.get(Keys.PIVOT_COLUMN);
          if (pivotColumn == PivotColumn.YES)
          {
            return true;
          }
        }
      }
      return false;
    }
  }

  private final class SchemaTransaction implements Callable<Integer>
  {
    private final int connectionId;
    private final String schemaName;

    private SchemaTransaction(int connectionId, String schemaName)
    {
      this.schemaName = schemaName;
      this.connectionId = connectionId;
    }

    @Override
    public Integer call() throws Exception
    {
      Integer schemaId = getSchemaId(connectionId, schemaName);
      if (schemaId == null)
      {
        schemaId = createSchema();
      }
      return schemaId;
    }

    private Integer createSchema()
    {
      Integer schemaId = null;
      StringBuilder insertSql = new StringBuilder();

      insertSql.append("insert into ");
      insertSql.append(MASTER_SCHEMA_CONFIGURATION);
      insertSql.append(".");
      insertSql.append(MASTER_TABLE_SCHEMAS);
      insertSql.append("\n");
      insertSql.append("(\n");
      insertSql.append("  connection_id,\n");
      insertSql.append("  schema_name\n");
      insertSql.append(")\n");
      insertSql.append("values(?, ?)");

      PreparedStatement insertStatement = Jdbc.createPreparedStatement(masterConnection, insertSql, connectionId, schemaName);
      try
      {
        schemaId = Jdbc.executeInsert(masterConnection, insertStatement);
      }
      finally
      {
        Jdbc.close(insertStatement);
      }
      return schemaId;
    }
  }

  public class TableTransaction implements Callable<Integer>
  {
    private boolean isGenerateVersion;
    private int schemaId;
    private String sourceTableName;

    public TableTransaction(int schemaId, String sourceTableName, boolean isGenerateVersion)
    {
      this.schemaId = schemaId;
      this.sourceTableName = sourceTableName;
      this.isGenerateVersion = isGenerateVersion;
    }

    @Override
    public Integer call() throws Exception
    {
      int tableId;

      StringBuilder s = new StringBuilder();

      s.append("select\n");
      s.append("  min(generation),\n");
      s.append("  max(generation)\n");
      s.append("from ");
      s.append(MASTER_SCHEMA_CONFIGURATION);
      s.append(".");
      s.append(MASTER_TABLE_TABLES);
      s.append("\n");
      s.append("where schema_id = ?\n");
      s.append("and source_table_name = ?");

      PreparedStatement selectStatement = Jdbc.createPreparedStatement(masterConnection, s, schemaId, sourceTableName);
      try
      {
        ResultSet resultSet = Jdbc.executeQuery(selectStatement);
        if (Jdbc.next(resultSet) && !Jdbc.isNull(resultSet, 1))
        {
          int minimumGeneration = Jdbc.getInt(resultSet, 1);
          int maximumGeneration = Jdbc.getInt(resultSet, 2);
          if (isGenerateVersion)
          {
            tableId = insertTable(masterConnection, schemaId, maximumGeneration + 1, sourceTableName);
          }
          else
          {
            if (minimumGeneration == 0)
            {
              tableId = getTableId(schemaId, sourceTableName, 0);
              String tableName = getTableName(tableId);
              dropTable(tableName);
            }
            else
            {
              tableId = insertTable(masterConnection, schemaId, 0, sourceTableName);
            }
          }
        }
        else
        {
          if (isGenerateVersion)
          {
            tableId = insertTable(masterConnection, schemaId, 1, sourceTableName);
          }
          else
          {
            tableId = insertTable(masterConnection, schemaId, 0, sourceTableName);
          }
        }
      }
      finally
      {
        Jdbc.close(selectStatement);
      }

      return tableId;
    }

  }

}
