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
import java.util.Collection;
import java.util.Map;

import com.extjs.gxt.ui.client.data.ModelData;

public class ArrayModelData implements ModelData, Serializable
{
  private String[] data;

  public ArrayModelData()
  {
  }

  public ArrayModelData(String[] data)
  {
    this.data = data;
  }

  public String[] getData()
  {
    return data;
  }

  public void setData(String[] data)
  {
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <X> X get(String property)
  {
    return (X)data[Integer.parseInt(property)];
  }

  @Override
  public Map<String, Object> getProperties()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> getPropertyNames()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public <X> X remove(String property)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public <X> X set(String property, X value)
  {
    throw new UnsupportedOperationException();
  }

}
