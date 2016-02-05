//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Defines the message types for SuRF entity services.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

object messages {

  case class ReadEntity[IdType](id: IdType)

  case class ListEntities(page: Int = 1,
                          pageSize: Int = 0,
                          filter: QueryFilter = QueryFilter.All)
}
