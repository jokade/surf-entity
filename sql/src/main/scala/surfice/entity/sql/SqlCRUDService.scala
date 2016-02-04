//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description:

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import surfice.entity.{ListResult, ReadEntityService}
import scalikejdbc._

abstract class SqlCRUDService[IdType,EntityType] extends ReadEntityService[IdType,EntityType] {

  def sqlRead(id: IdType) : SQL[Nothing,NoExtractor]
  def sqlList(offset: Int, limit: Int) : SQL[Nothing,NoExtractor]

  def readOnly[A](execution: (DBSession)=>A): A
  def mapSingle(rs: WrappedResultSet) : EntityType
  def wrapList(page: Int, pageSize: Int, list: Iterable[EntityType]): ListResult[EntityType]

  override def readEntity(id: IdType): Option[EntityType] = readOnly{ implicit session =>
    sqlRead(id).map(mapSingle _).single().apply()
  }

  override def listEntities(page: Int, pageSize: Int): ListResult[EntityType] = readOnly{ implicit session =>
    val (offset,limit) = calcOffsetLimit(page,pageSize)
    val list = sqlList(offset,limit).map(mapSingle _).list.apply
    wrapList(page,pageSize,list)
  }

  def calcOffsetLimit(page: Int, pageSize: Int): (Int,Int) =
    if(page<1||pageSize<1) (0,Int.MaxValue)
    else ((page-1)*pageSize,pageSize)
}
