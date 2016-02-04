//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Base trait for all messages that represent a list of entities

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

trait ListResult[EntityType] {
  def page: Int
  def pageSize: Int
  def list: Iterable[EntityType]
}
