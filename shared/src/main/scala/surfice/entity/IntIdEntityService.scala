//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: Mixin trait for EntityServiceS with IdType=Int

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surfice.entity.exceptions.InvalidIdException

trait IntIdEntityService {
  this: EntityService[_,_] =>

  final def checkId(id: Any): Int = id match {
    case id: Int => id
    case _ => throw InvalidIdException(id)
  }

}
