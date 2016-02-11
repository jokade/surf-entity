//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base trait for services that support creating/ updating a specific type of data entities.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.Service.Processor
import surfice.entity.messages.{CreateEntity, UpdateEntity}

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
