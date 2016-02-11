//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Mixin trait for SqlServiceS that use the scalikejdbc default DB

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import scalikejdbc.{DB, DBSession}

trait DBProvider {
  this: SqlService[_,_] =>

  final def readOnly[A](execution: (DBSession)=>A): A =  DB.readOnly(execution)
  final def autoCommit[A](execution: (DBSession)=>A): A = DB.autoCommit(execution)
}
