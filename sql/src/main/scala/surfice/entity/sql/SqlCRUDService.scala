//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description:

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import surfice.entity.{QueryFilter, ListResult, ReadEntityService}
import scalikejdbc._

abstract class SqlCRUDService[IdType,EntityType]
  extends SqlService[IdType,EntityType]
  with ReadEntityService[IdType,EntityType] {

  def sqlRead(id: IdType) : SQL[Nothing,NoExtractor]
  def sqlList(offset: Int, limit: Int, where: SqlQueryFilter) : SQL[Nothing,NoExtractor]

  override def readEntity(id: IdType): Option[EntityType] = readOnly{ implicit session =>
    sqlRead(id).map(mapSingle _).single().apply()
  }

  override def listEntities(page: Int, pageSize: Int, filter: QueryFilter): ListResult[EntityType] = readOnly{ implicit session =>
    if(page>1&&pageSize<1) wrapList(page,pageSize,Nil)
    else {
      val (offset, limit) = calcOffsetLimit(page, pageSize)
      val list = sqlList(offset, limit, SqlQueryFilter(filter)).map(mapSingle _).list.apply
      wrapList(page, pageSize, list)
    }
  }

  def calcOffsetLimit(page: Int, pageSize: Int): (Int,Int) =
    if(page<1||pageSize<1) (0,Int.MaxValue)
    else ((page-1)*pageSize,pageSize)
}
