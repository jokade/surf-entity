//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Defines all exceptions thrown by SuRF entitiy services.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

object exceptions {

  case class InvalidIdException(id: Any) extends RuntimeException(s"Invalid entity id '$id' of type ${id.getClass}")

  case class InvalidEntityException(entity: Any) extends RuntimeException(s"Invalid entity of type ${entity.getClass}")

  case class EntityNotFoundException(id: Any, entityType: String = "") extends RuntimeException(
    if(entityType!="") s"Entity of type $entityType with id=$id not found"
    else s"Entity with id=$id not found")
}
