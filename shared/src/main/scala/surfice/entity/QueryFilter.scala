//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared
// Description: Traits for entity query filters (a.k.a WHERE caluses in SQL)

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity

sealed trait QueryFilter

object QueryFilter {
  object All extends QueryFilter

  def eq[T](property: String, value: T): PropertyEqFilter[T] = PropertyEqFilter(property,value)

  implicit class RichQueryFilter(val qf: QueryFilter) extends AnyVal {
    def and(right: QueryFilter) = AndFilter(qf,right)
    def or(right: QueryFilter) = OrFilter(qf,right)
  }
}

sealed trait BinOpFilter extends QueryFilter {
  def left: QueryFilter
  def right: QueryFilter
}

case class AndFilter(left: QueryFilter, right: QueryFilter) extends BinOpFilter

case class OrFilter(left: QueryFilter, right: QueryFilter) extends BinOpFilter

protected[entity] sealed trait ValueHolder[ValueType] extends QueryFilter {
  def value: ValueType
}

sealed trait PropertyFilter[T] extends ValueHolder[T] {
  /// The name of the property to which this filter applies
  def name: String
}

case class PropertyEqFilter[T](name: String, value: T) extends PropertyFilter[T]

case class UnsupportedQueryFilter(msg: String, qf: Option[QueryFilter] = None) extends RuntimeException(msg) {
  def this(msg: String, qf: QueryFilter) = this(msg,Some(qf))
}
