//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base trait for services that support creating/ updating a specific type of data entities.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.Service.Processor
import surf.{Request, ServiceRef}
import surfice.entity.messages.{CreateEntity, UpdateEntity}

import scala.concurrent.ExecutionContext

trait WriteEntityService[IdType,EntityType] extends EntityService[IdType,EntityType] {

  def checkEntity(entity: Any): EntityType

  def createEntity(entity: EntityType): IdType

  def updateEntity(id: IdType, entity: EntityType): Unit

  abstract override def process: Processor = super.process.orElse{
    case CreateEntity(entity) =>
      val res = createEntity(checkEntity(entity))
      if(isRequest) request ! res
    case UpdateEntity(id,entity) =>
      val res = updateEntity(checkId(id),checkEntity(entity))
      if(isRequest) request ! res
  }
}

trait TypedWriteEntityService[IdType,EntityType] {
  def ref: ServiceRef
  @inline final def createEntity(entity: EntityType)(implicit req: Request): Request =
    req.withInput(CreateEntity(entity))
  @inline final def createEntityReq(entity: EntityType)(implicit ec: ExecutionContext): Request =
    Request(CreateEntity(entity)) >> ref
  @inline final def updateEntity(id: IdType, entity: EntityType)(implicit req: Request): Request =
    req.withInput(UpdateEntity(id,entity)) >> ref
  @inline final def updateEntityReq(id: IdType, entity: EntityType)(implicit ec: ExecutionContext): Request =
    Request(UpdateEntity(id,entity)) >> ref
}
