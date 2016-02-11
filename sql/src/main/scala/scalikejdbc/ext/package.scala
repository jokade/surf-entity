//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Provides utilitiy methods that need access to private memebers of scalikejdbc.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package scalikejdbc

import surfice.entity.UnsupportedQueryFilter

package object ext {
  private val validColumnNames = """[a-zA-Z][a-zA-Z0-9_\.]*""".r.pattern

  def column(name: String) : SQLSyntax =
    if( validColumnNames.matcher(name).matches() ) new SQLSyntax(name)
    else throw UnsupportedQueryFilter(s"Invalid property name: $name")
}
