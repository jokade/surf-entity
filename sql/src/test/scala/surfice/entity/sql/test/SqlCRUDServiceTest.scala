//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: sql / test
// Description: Tests for SqlCRUDService

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.sql.test

import scalikejdbc._
import surf.{ServiceRefFactory, ServiceRef}
import surfice.entity.ListResult
import surfice.entity.exceptions.{InvalidEntityException, InvalidIdException}
import surfice.entity.sql.{SqlQueryFilter, SqlCRUDService}
import surfice.entity.sql.test.SqlCRUDServiceTestFixture.{TestService, Data}
import surfice.entity.test.{WriteEntityServiceBehaviour, ReadEntityServiceBehaviour}

import scala.concurrent.ExecutionContext

trait SqlCRUDServiceTestFixture {
  implicit val ec: ExecutionContext = concurrent.ExecutionContext.global

  // returns a valid, but not-existent Id
  def notExistent: Int = 123456

  val sut: ServiceRef = ServiceRefFactory.Sync.serviceOf(new TestService)

  // returns a Map with all existing entities
  def entities: Map[Int, Data] = SqlCRUDServiceTestFixture.entities

  // returns an invalid IdType
  def invalidId: Any = "hello"

  // returns a Map with all entities actually existing in the underlying data source
  def dsEntities: Map[Int,Data] = SqlCRUDServiceTestFixture.updEntities.map(p => (p.id,p) ).toMap
  // returns an updated version of the specified entity
  def updateForEntity(id: Any): Data = id match {
    case 1 => Data(1,"update")
  }
  // returns a valid, but not-existent entity
  def notExistentEntity: (Int,Data) = (999,Data(999,"not existent"))
  def newEntity(): Data = Data(2,"new")

}

object SqlCRUDServiceTestFixture {
  val entities = Map(
    1 -> Data(1,"hello"),
    2 -> Data(2,"world"),
    3 -> Data(3,"test"),
    4 -> Data(4,"another string")
  )
  lazy val db = {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:test","","")
    DB autoCommit { implicit session =>
      sql"create table data (id int not null primary key, value varchar)".execute().apply()
      sql"insert into data(id,value) values(1,'hello')".update().apply()
      sql"insert into data(id,value) values(2,'world')".update().apply()
      sql"insert into data(id,value) values(3,'test')".update().apply()
      sql"insert into data(id,value) values(4,'another string')".update().apply()
      sql"create table upddata (id int auto_increment not null primary key, value varchar)".execute().apply()
      sql"insert into upddata(id,value) values(1,'hello')".update().apply()
    }
    DB
  }

  def updEntities = db readOnly { implicit session =>
    sql"select id,value from upddata order by id".map(rs => Data(rs.int("id"), rs.string("value"))).list().apply()
  }

  case class Data(id: Int, value: String)
  case class DataList(page: Int, pageSize: Int, list: Iterable[Data]) extends ListResult[Data]

  class TestService extends SqlCRUDService[Int,Data] {
    override def checkId(id: Any): Int = id match {
      case id: Int => id
      case id: Long => id.toInt
      case _ => throw InvalidIdException(id)
    }
    override def checkEntity(entity: Any): Data = entity match {
      case d: Data => d
      case _ => throw InvalidEntityException(entity)
    }

    override def wrapList(page: Int, pageSize: Int, list: Iterable[Data]): ListResult[Data] = DataList(page,pageSize,list)

    override def readOnly[A](execution: (DBSession) => A): A = db.readOnly(execution)
    override def autoCommit[A](execution: (DBSession) => A): A = db.autoCommit(execution)

    override def mapSingle(rs: WrappedResultSet): Data = Data(
      rs.int("id"),
      rs.string("value")
    )

    override def sqlRead(id: Int): SQL[Nothing, NoExtractor] = sql"select id,value from data where id=$id"
    override def sqlList(offset: Int, limit: Int, where: SqlQueryFilter): SQL[Nothing, NoExtractor] =
      sql"select id,value from data ${where.sql} limit $limit offset $offset"

    override def sqlUpdate(id: Int, entity: Data): SQL[Nothing, NoExtractor] = {
      sql"update upddata set value=${entity.value} where id=$id"
    }

    override def sqlCreate(entity: Data): SQL[Nothing, NoExtractor] =
      sql"insert into upddata(value) values(${entity.value})"
  }
}


object SqlCRUDServiceTest1 extends ReadEntityServiceBehaviour[Int,Data] with SqlCRUDServiceTestFixture

object SqlCRUDServiceTest2 extends WriteEntityServiceBehaviour[Int,Data] with SqlCRUDServiceTestFixture