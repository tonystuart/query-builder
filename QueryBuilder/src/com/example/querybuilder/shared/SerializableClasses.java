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

public class SerializableClasses implements Serializable
{
  // Add references to Serializable classes that are not part of the service method signature (e.g. passed within ModelData)
  public ArrayModelData arrayModelData;
  public Condition condition;
  public SortDirection direction;
  public JoinType joinType;
  public NodeType nodeType;
  public SummaryOperation summaryOperation;
  public PivotColumn pivotColumn;

  public SerializableClasses()
  {
  }

}
