//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base class for services that support reading/ listing a specific type of data entities.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

import surf.Service.Processor
import messages._

trait ReadEntityService[IdType,EntityType] extends EntityService[IdType,EntityType] {

  def readEntity(id: IdType): Option[EntityType]
  def listEntities(page: Int, pageSize: Int, filter: QueryFilter = QueryFilter.All): ListResult[EntityType]

  abstract override def process: Processor = super.process.orElse{
    case ReadEntity(id)                     if isRequest => request ! readEntity(checkId(id))
    case ListEntities                       if isRequest => request ! listEntities(1,0)
    case ListEntities(page,pagesize,filter) if isRequest => request ! listEntities(page,pagesize,filter)
  }
}


