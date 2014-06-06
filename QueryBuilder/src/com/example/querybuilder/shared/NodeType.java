// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.shared;

public enum NodeType
{
  COLUMN, //
  CONNECTION, //
  CONNECTION_ERROR, //
  CONNECTION_ROOT, //
  EXTERNAL_TABLE, // The order of EXTERNAL_TABLE, EXTERNAL_VIEW and INTERNAL_TABLE determines the sequence in table tree
  EXTERNAL_VIEW, //
  INTERNAL_TABLE, //
  SCHEMA, //
}
