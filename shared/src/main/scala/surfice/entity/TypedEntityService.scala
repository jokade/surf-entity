//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: common
// Description: Typed service façade for R/W entity services.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.{ServiceRefRegistry, ServiceRef}

/**
 * Typed service façade for R/W entity services.
 *
 * @param ref ServiceRef to be used for all requests
 * @tparam IdType
 * @tparam EntityType
 */
class TypedEntityService[IdType,EntityType](val ref: ServiceRef)
  extends TypedReadEntityService[IdType,EntityType]
  with TypedWriteEntityService[IdType,EntityType]

object TypedEntityService {
  def apply[IdType,EntityType](ref: ServiceRef) = new TypedEntityService[IdType,EntityType](ref)
  def atPath[IdType,EntityType](path: String)(implicit registry: ServiceRefRegistry) =
    new TypedEntityService[IdType,EntityType]( registry.serviceAt(path) )
}
