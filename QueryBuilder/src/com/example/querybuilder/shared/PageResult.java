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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public final class PageResult implements PagingLoadResult<ModelData>, Serializable
{
  private int offset = 0;
  private int totalLength = 1;
  private List<ModelData> data;

  public PageResult()
  {
  }

  public void setData(List<ModelData> data)
  {
    this.data = data;
  }

  @Override
  public List<ModelData> getData()
  {
    return data;
  }

  @Override
  public int getOffset()
  {
    return offset;
  }

  @Override
  public int getTotalLength()
  {
    return totalLength;
  }

  @Override
  public void setOffset(int offset)
  {
    this.offset = offset;
  }

  @Override
  public void setTotalLength(int totalLength)
  {
    this.totalLength = totalLength;
  }
}
