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
import java.util.List;

public class QueryResult implements Serializable
{
  private List<ColumnDescriptor> columnDescriptors;
  private PageResult pageResult;

  public QueryResult()
  {
  }

  public QueryResult(List<ColumnDescriptor> columnDescriptors, PageResult pageResult)
  {
    this.columnDescriptors = columnDescriptors;
    this.pageResult = pageResult;
  }

  public List<ColumnDescriptor> getColumnDescriptors()
  {
    return columnDescriptors;
  }

  public void setColumnDescriptors(List<ColumnDescriptor> columnDescriptors)
  {
    this.columnDescriptors = columnDescriptors;
  }

  public PageResult getPageResult()
  {
    return pageResult;
  }

  public void setPageResult(PageResult pageResult)
  {
    this.pageResult = pageResult;
  }

}
