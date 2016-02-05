//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql / test
// Description: Tests for SqlQueryFilter.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql.test

import scalikejdbc._
import surfice.entity.QueryFilter
import surfice.entity.sql.SqlQueryFilter
import utest._

object SqlQueryFilterTest extends TestSuite {
  val tests = TestSuite {
    'eq-{
      query(QueryFilter.eq("id",1)){ res =>
        assert( res.size == 1, res(0).id == 1 )
      }
      query(QueryFilter.eq("sval","test")) { res =>
        assert( res.size == 2, res(0).id == 3, res(1).id == 4 )
      }
      query(QueryFilter.eq("bval",true)) { res =>
        assert( res.size == 2, res(0).id == 1, res(1).id == 3 )
      }
    }
    'and-{
      query( QueryFilter.eq("sval","test") and QueryFilter.eq("bval",false) ){ res =>
        assert( res.size == 1, res(0).id == 4 )
      }
    }
    'or-{
      query( QueryFilter.eq("sval","test") or QueryFilter.eq("bval",true) ){ res =>
        assert( res.size == 3, res(0).id == 1, res(1).id ==3, res(2).id == 4 )
      }
    }
  }

  def query(qf: QueryFilter)(body: List[Data]=>Any) = db readOnly{ implicit session =>
    val where = SqlQueryFilter(qf).sql
    val res = sql"select id,sval,ival,bval from filtertest $where order by id".map( rs => Data(
      rs.int("id"),
      rs.string("sval"),
      rs.int("ival"),
      rs.boolean("bval")
    )).list.apply
    body(res)
  }

  lazy val db = {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:test","","")
    DB autoCommit { implicit session =>
      sql"create table filtertest (id int not null primary key, sval varchar, ival int, bval boolean)".execute().apply()
      sql"insert into filtertest(id,sval,ival,bval) values(1,'hello',42,true)".update().apply()
      sql"insert into filtertest(id,sval,ival,bval) values(2,'world',42,false)".update().apply()
      sql"insert into filtertest(id,sval,ival,bval) values(3,'test',-5,true)".update().apply()
      sql"insert into filtertest(id,sval,ival,bval) values(4,'test',999,false)".update().apply()
    }
    DB
  }

  val data = Map(
    1 -> Data(1,"hello",42,true),
    2 -> Data(2,"world",42,false),
    3 -> Data(3,"test",-5,true),
    4 -> Data(4,"test",999,false)
  )

  case class Data(id: Int, sval: String, ival: Int, bval: Boolean)
}
