//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base trait for services that support reading/ listing a specific type of data entities.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.Service.Processor
import messages._
import surf.{Request, ServiceRef}

import scala.concurrent.ExecutionContext

/**
 * Base trait for services that support reading/ querying a specific type of data entities.
 *
 * @tparam IdType
 * @tparam EntityType
 */
trait ReadEntityService[IdType,EntityType] extends EntityService[IdType,EntityType] {

  def readEntity(id: IdType): Option[EntityType]
  def listEntities(page: Int, pageSize: Int, filter: QueryFilter = QueryFilter.All): ListResult[EntityType]

  abstract override def process: Processor = super.process.orElse{
    case ReadEntity(id)                     if isRequest => request ! readEntity(checkId(id))
    case ListEntities                       if isRequest => request ! listEntities(1,0)
    case ListEntities(page,pagesize,filter) if isRequest => request ! listEntities(page,pagesize,filter)
  }
}

trait TypedReadEntityService[IdType,EntityType] {
  def ref: ServiceRef
  @inline final def readEntity(id: IdType)(map: Option[EntityType]=>Any)(implicit req: Request): Request =
    req.withInput(ReadEntity(id)).mapOutput{case e: Option[_] => map(e.asInstanceOf[Option[EntityType]]) } >> ref
  @inline final def readEntityReq(id: IdType)(map: Option[EntityType]=>Any)(implicit ec: ExecutionContext): Request =
    Request(ReadEntity(id)).mapOutput{case e: Option[_] => map(e.asInstanceOf[Option[EntityType]]) } >> ref
  @inline final def listEntities(page: Int = 1, pageSize: Int = 0, filter: QueryFilter = QueryFilter.All)(map: ListResult[EntityType]=>Any)(implicit req: Request): Request =
    req.withInput(ListEntities(page,pageSize,filter)).mapOutput{case r:ListResult[_] => map(r.asInstanceOf[ListResult[EntityType]])} >> ref
  @inline final def listEntitiesReq(page: Int = 1, pageSize: Int = 0, filter: QueryFilter = QueryFilter.All)(map: ListResult[EntityType]=>Any)(implicit ec: ExecutionContext): Request =
    Request(ListEntities(page,pageSize,filter)).mapOutput{case r:ListResult[_] => map(r.asInstanceOf[ListResult[EntityType]])} >> ref
}

