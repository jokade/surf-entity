//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Defines all exceptions thrown by SuRF entitiy services.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

object exceptions {

  case class InvalidIdException(id: Any) extends RuntimeException(s"Invalid entity id '$id' of type ${id.getClass}")
}
