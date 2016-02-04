//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared / test
// Description: Test cases for ReadEntityService

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.test

import surf.{ServiceRefFactory, ServiceRef}
import surf.dsl._
import surfice.entity.exceptions.InvalidIdException
import surfice.entity.messages.{ListEntities, ReadEntity}
import surfice.entity.{ListResult, ReadEntityService}
import utest._

import scala.concurrent.ExecutionContext

trait ReadEntityServiceBehaviour[IdType,EntityType] extends TestBase {
  implicit def ec: ExecutionContext
  def sut: ServiceRef
  // returns a Map with all existing entities
  def entities: Map[IdType,EntityType]
  // returns an invalid IdType
  def invalidId: Any
  // returns a valid, but not-existent Id
  def notExistent: IdType

  assert( entities.size > 3 )
  val tests = TestSuite {
    'read-{
      'exists-{
        val e = entities.head
        (ReadEntity(e._1) >> sut).future.map {
          case Some(res) => assert(e._2 == res)
        }
      }
      'notExistent-{
        (ReadEntity(notExistent) >> sut).future.map {
          case None =>
        }
      }
      'invalid-{
        expectFailure( (ReadEntity(invalidId) >> sut).future )
      }
    }
    'list-{
      'default-{
        'a-{
          (ListEntities >> sut).future map {
            case res: ListResult[EntityType] =>
              assert(res.page == 1,
                     res.pageSize == 0,
                     res.list.size == entities.size)
              res.list.zip(entities.values).foreach( p => assert(p._1 == p._2))
          }
        }
        'b-{
          (ListEntities() >> sut).future map {
            case res: ListResult[EntityType] =>
              assert(res.page == 1,
                res.pageSize == 0,
                res.list.size == entities.size)
              res.list.zip(entities.values).foreach( p => assert(p._1 == p._2))
          }
        }
      }
      'page1_pageSize2-{
        (ListEntities(1,2) >> sut).future map {
          case res: ListResult[EntityType] =>
            assert(
              res.page == 1,
              res.pageSize == 2,
              res.list.size == 2
            )
            entities.values.take(2).zip(res.list).foreach( p => assert( p._1 == p._2 ) )
        }
      }
      'page2_pageSize2-{
         (ListEntities(2,2) >> sut).future map {
          case res: ListResult[EntityType] =>
            assert(
              res.page == 2,
              res.pageSize == 2,
              res.list.size == 2
            )
            entities.values.drop(2).take(2).zip(res.list).foreach( p => assert( p._1 == p._2 ) )
        }
      }
      'page1_pageSize10000-{
        (ListEntities(1,10000) >> sut).future map {
          case res: ListResult[EntityType] =>
            assert(
              res.page == 1,
              res.pageSize == 10000,
              res.list.size == entities.size
            )
            entities.values.zip(res.list).foreach( p => assert( p._1 == p._2 ) )
        }
      }
    }
  }
}

object ReadEntityServiceTest extends ReadEntityServiceBehaviour[Int,String] {
  override implicit val ec = concurrent.ExecutionContext.global
  override val sut: ServiceRef = ServiceRefFactory.Sync.serviceOf(new TestService)

  override def notExistent = 999

  override def entities: Map[Int,String] = Map(
    1 -> "hello",
    42 -> "world",
    123 -> "another value",
    333 -> "xyz"
  )

  override def invalidId: Any = "test"

  class TestService extends ReadEntityService[Int,String] {

    override def checkId(id: Any): Int = id match {
      case id: Int => id
      case _ => throw InvalidIdException(id)
    }

    override def readEntity(id: Int): Option[String] = entities.get(id)

    override def listEntities(page: Int, pageSize: Int): ListResult[String] =
      if(page<1 || pageSize<1) LR(1,0,entities.values)
      else {
        val offset = (page-1)*pageSize
        LR(page,pageSize,entities.values.drop(offset).take(pageSize))
      }

    case class LR(page: Int, pageSize: Int, list: Iterable[String]) extends ListResult[String]
  }
}
