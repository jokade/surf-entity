//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql
// Description: A wrapper for transforming QueryFilters into SQL WHERE clauses

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql

import scalikejdbc._
import surfice.entity.{OrFilter, AndFilter, QueryFilter, PropertyEqFilter}

trait SqlQueryFilter {
  def sql: SQLSyntax
}

object SqlQueryFilter {
  import ext._

  def apply(qf: QueryFilter): SqlQueryFilter =
    if(qf == QueryFilter.All) Empty
    else new Impl(qf)


  final class Impl(qf: QueryFilter) extends SqlQueryFilter {
    def sql: SQLSyntax = sqls"where " append filterToSql(qf)

    private def filterToSql(qf: QueryFilter): SQLSyntax = qf match {
      case PropertyEqFilter(name, value: Any) => sqls"${column(name)} = $value"
      case AndFilter(left,right) => sqls"(${filterToSql(left)} and ${filterToSql(right)})"
      case OrFilter(left,right) => sqls"(${filterToSql(left)} or ${filterToSql(right)})"
      case QueryFilter.All => ???
    }
  }

  object Empty extends SqlQueryFilter {
    def sql = sqls""
  }

}
