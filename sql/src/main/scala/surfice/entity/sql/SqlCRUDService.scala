//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Common base class for EntityServiceS backed by an SQL database (using scalikejdbc)

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import surfice.entity.{WriteEntityService, QueryFilter, ListResult, ReadEntityService}
import scalikejdbc._

abstract class SqlCRUDService[IdType,EntityType]
  extends SqlService[IdType,EntityType]
  with ReadEntityService[IdType,EntityType]
  with WriteEntityService[IdType,EntityType] {

  def sqlRead(id: IdType) : SQL[Nothing,NoExtractor]
  def sqlCreate(entity: EntityType) : SQL[Nothing,NoExtractor]
  def sqlUpdate(id: IdType, entity: EntityType) : SQL[Nothing,NoExtractor]
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

  override def createEntity(entity: EntityType): IdType = autoCommit{ implicit session =>
    val key = sqlCreate(entity).updateAndReturnGeneratedKey().apply
    checkId(key)
  }

  override def updateEntity(id: IdType, entity: EntityType): Unit = autoCommit{ implicit session =>
    sqlUpdate(id,entity).update().apply()
  }

  def calcOffsetLimit(page: Int, pageSize: Int): (Int,Int) =
    if(page<1||pageSize<1) (0,Int.MaxValue)
    else ((page-1)*pageSize,pageSize)
}
