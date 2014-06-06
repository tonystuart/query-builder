// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.server;

import java.sql.SQLException;

public class SqlRuntimeException extends RuntimeException
{

  public SqlRuntimeException(SQLException e)
  {
    super(e);
  }

  public SqlRuntimeException(String message)
  {
    super(message);
  }

}
