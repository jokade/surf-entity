//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base trait for all SuRF entity services.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.Service
import surf.Service.Processor

trait EntityService[IdType,EntityType] extends Service {
  override def process = PartialFunction.empty[Any,Unit]
  def checkId(id: Any): IdType
}

