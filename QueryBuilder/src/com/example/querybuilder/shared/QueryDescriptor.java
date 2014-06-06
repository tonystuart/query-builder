// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;

public class QueryDescriptor implements Serializable
{
  private List<ModelData> columns;
  private String filterCriteria;
  private List<ModelData> joins;
  private List<TableReference> tables;

  public QueryDescriptor()
  {
  }

  public QueryDescriptor(List<TableReference> tables, List<ModelData> columns, List<ModelData> joins, String filterCriteria)
  {
    this.tables = tables;
    this.columns = columns;
    this.joins = joins;
    this.filterCriteria = filterCriteria;
  }

  public QueryDescriptor(TableReference tableReference)
  {
    this.tables = Arrays.asList(tableReference);
  }

  public List<ModelData> getColumns()
  {
    return columns;
  }

  public String getFilterCriteria()
  {
    return filterCriteria;
  }

  public List<ModelData> getJoins()
  {
    return joins;
  }

  public List<TableReference> getTables()
  {
    return tables;
  }

  public void setColumns(List<ModelData> columns)
  {
    this.columns = columns;
  }

  public void setFilterCriteria(String filterCriteria)
  {
    this.filterCriteria = filterCriteria;
  }

  public void setJoins(List<ModelData> joins)
  {
    this.joins = joins;
  }

  public void setTables(List<TableReference> tables)
  {
    this.tables = tables;
  }

}
