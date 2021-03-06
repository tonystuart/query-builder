// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import com.extjs.gxt.ui.client.data.ModelData;

public class StoreAdapter<U extends ModelData> implements StoreVisitor<U>
{
  public boolean visitConditional(U modelData)
  {
    visit(modelData);
    return true;
  }

  public void visit(U modelData)
  {
  }
}
