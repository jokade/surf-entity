//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Base trait for EntityServices backed by an SQL database (using scalikejdbc).

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import scalikejdbc.{WrappedResultSet, DBSession}
import surfice.entity.{ListResult, EntityService}

trait SqlService[IdType,EntityType] extends EntityService[IdType,EntityType] {

  def readOnly[A](execution: (DBSession)=>A): A
  def autoCommit[A](execution: (DBSession)=>A): A

  def mapSingle(rs: WrappedResultSet) : EntityType
  def wrapList(page: Int, pageSize: Int, list: Iterable[EntityType]): ListResult[EntityType]
}
