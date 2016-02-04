//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Mixin trait for SqlServiceS that use a scalikejdbc NamedDB

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import scalikejdbc.{NamedDB, DBSession}

trait NamedDBProvider {
  this: SqlService[_,_] =>

  val dbname: Symbol

  final def readOnly[A](execution: (DBSession)=>A): A =  NamedDB(dbname).readOnly(execution)
  final def autoCommit[A](execution: (DBSession)=>A): A = NamedDB(dbname).autoCommit(execution)
}
