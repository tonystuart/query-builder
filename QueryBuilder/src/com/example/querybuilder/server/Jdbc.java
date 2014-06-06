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
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Jdbc
{
  private static Log log = LogFactory.getLog(Jdbc.class);

  static
  {
    loadAllJdbcDrivers();
  }

  public static void absolute(ResultSet resultSet, int row)
  {
    try
    {
      resultSet.absolute(row);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void close(Connection connection)
  {
    try
    {
      connection.close();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void close(PreparedStatement preparedStatement)
  {
    try
    {
      preparedStatement.close();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void close(ResultSet resultSet)
  {
    try
    {
      resultSet.close();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static PreparedStatement createPreparedStatement(Connection connection, String sql, Object... parameters)
  {
    PreparedStatement preparedStatement = Jdbc.prepareStatement(connection, sql);
    initializeParameters(preparedStatement, parameters);
    return preparedStatement;
  }

  public static PreparedStatement createPreparedStatement(Connection connection, StringBuilder s, Object... parameters)
  {
    return createPreparedStatement(connection, s.toString(), parameters);
  }

  public static boolean execute(PreparedStatement preparedStatement, Object... parameters)
  {
    try
    {
      initializeParameters(preparedStatement, parameters);
      return preparedStatement.execute();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int executeInsert(Connection connection, PreparedStatement preparedStatement, Object... parameters)
  {
    Integer id = null;
    Jdbc.execute(preparedStatement, parameters);
    String selectSql = "values(cast(identity_val_local() as integer))";
    PreparedStatement queryStatement = Jdbc.createPreparedStatement(connection, selectSql);
    try
    {
      ResultSet resultSet = Jdbc.executeQuery(queryStatement);
      if (Jdbc.next(resultSet))
      {
        id = Jdbc.getInt(resultSet, 1);
      }
    }
    finally
    {
      Jdbc.close(queryStatement);
    }
    return id;
  }

  public static ResultSet executeQuery(PreparedStatement preparedStatement, Object... parameters)
  {
    try
    {
      initializeParameters(preparedStatement, parameters);
      return preparedStatement.executeQuery();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static <V> V executeTransaction(Connection connection, Callable<V> callable)
  {
    try
    {
      try
      {
        connection.setAutoCommit(false);
        V value = callable.call();
        return value;
      }
      finally
      {
        connection.setAutoCommit(true);
      }
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static int getColumnCount(ResultSetMetaData metaData)
  {
    try
    {
      return metaData.getColumnCount();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static String getColumnName(ResultSetMetaData resultSetMetaData, int columnNumber)
  {
    try
    {
      return resultSetMetaData.getColumnName(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static ResultSet getColumns(DatabaseMetaData databaseMetaData, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
  {
    try
    {
      return databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getColumnType(ResultSetMetaData resultSetMetaData, int columnNumber)
  {
    try
    {
      return resultSetMetaData.getColumnType(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getInt(ResultSet resultSet, int columnNumber)
  {
    try
    {
      return resultSet.getInt(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static DatabaseMetaData getMetaData(Connection connection)
  {
    try
    {
      return connection.getMetaData();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static ResultSetMetaData getMetaData(ResultSet resultSet)
  {
    try
    {
      return resultSet.getMetaData();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static Object getObject(ResultSet resultSet, int columnNumber)
  {
    try
    {
      return resultSet.getObject(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static ParameterMetaData getParameterMetaData(PreparedStatement preparedStatement)
  {
    try
    {
      return preparedStatement.getParameterMetaData();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getParameterType(ParameterMetaData parameterMetaData, int parameterNumber)
  {
    try
    {
      return parameterMetaData.getParameterType(parameterNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getPrecision(ResultSetMetaData resultSetMetaData, int columnNumber)
  {
    try
    {
      return resultSetMetaData.getPrecision(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getRow(ResultSet resultSet)
  {
    try
    {
      int row = resultSet.getRow();
      return row;
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static int getScale(ResultSetMetaData resultSetMetaData, int columnNumber)
  {
    try
    {
      return resultSetMetaData.getScale(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static ResultSet getSchemas(DatabaseMetaData databaseMetaData)
  {
    try
    {
      return databaseMetaData.getSchemas();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static String getString(ResultSet resultSet, int columnNumber)
  {
    try
    {
      return resultSet.getString(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static ResultSet getTables(DatabaseMetaData databaseMetaData, String catalog, String schemaPattern, String tableNamePattern, String[] types)
  {
    try
    {
      return databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static Date getTimestamp(ResultSet resultSet, int columnNumber)
  {
    try
    {
      return resultSet.getTimestamp(columnNumber);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void initializeParameters(PreparedStatement preparedStatement, Object... parameters)
  {
    for (int columnOffset = 0, columnNumber = 1; columnOffset < parameters.length; columnOffset++, columnNumber++)
    {
      Object value = parameters[columnOffset];
      try
      {
        if (value == null)
        {
          int parameterType = preparedStatement.getParameterMetaData().getParameterType(columnNumber);
          preparedStatement.setNull(columnNumber, parameterType);
        }
        else
        {
          preparedStatement.setObject(columnNumber, value);
        }
      }
      catch (SQLException e)
      {
        throw new SqlRuntimeException(e.toString() + "\n" + "columnNumber=" + columnNumber + ", value=" + value);
      }
    }
  }

  public static boolean isNull(ResultSet resultSet, int columnNumber)
  {
    try
    {
      return resultSet.getObject(columnNumber) == null;
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void last(ResultSet resultSet)
  {
    try
    {
      resultSet.last();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void loadAllJdbcDrivers()
  {
    // In alphabetical order... See http://wiki.netbeans.org/DatabasesAndDrivers
    loadJdbcDriver("com.ibm.db2.jcc.DB2Driver");
    loadJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    loadJdbcDriver("com.mysql.jdbc.Driver");
    loadJdbcDriver("oracle.jdbc.driver.OracleDriver");
    loadJdbcDriver("org.apache.derby.jdbc.ClientDriver");
  }

  public static void loadJdbcDriver(String className)
  {
    try
    {
      Class.forName(className).newInstance();
      log.info("JDBC driver " + className + " is available");
    }
    catch (Exception e)
    {
      log.info("JDBC driver " + className + " is unavailable");
    }
  }

  public static boolean next(ResultSet resultSet)
  {
    try
    {
      return resultSet.next();
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static Connection open(String url)
  {
    return open(url, null, null);
  }

  public static Connection open(String url, String user, String password)
  {
    try
    {
      return DriverManager.getConnection(url, user, password);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static PreparedStatement prepareStatement(Connection connection, String sql)
  {
    try
    {
      return connection.prepareStatement(sql);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e.toString() + "\n" + sql);
    }
  }

  public static PreparedStatement prepareStatement(Connection connection, String sql, int resultSetType, int resultSetConcurrency)
  {
    try
    {
      return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e.toString() + "\n" + sql);
    }
  }

  public static void setNull(PreparedStatement preparedStatement, int parameterNumber, int parameterType)
  {
    try
    {
      preparedStatement.setNull(parameterNumber, parameterType);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void setObject(PreparedStatement preparedStatement, int parameterNumber, Object value)
  {
    try
    {
      preparedStatement.setObject(parameterNumber, value);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

  public static void setReadOnly(Connection connection, boolean isReadOnly)
  {
    try
    {
      connection.setReadOnly(isReadOnly);
    }
    catch (SQLException e)
    {
      throw new SqlRuntimeException(e);
    }
  }

}
