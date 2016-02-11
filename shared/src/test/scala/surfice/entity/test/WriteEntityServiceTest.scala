//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared / test
// Description: Test cases for WriteEntityService

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.test

import surf.dsl._
import surf.{ServiceRefFactory, ServiceRef}
import surfice.entity.messages.{CreateEntity, UpdateEntity}
import surfice.entity.{WriteEntityService, ReadEntityService}
import surfice.entity.exceptions.{EntityNotFoundException, InvalidEntityException, InvalidIdException}

import scala.concurrent.ExecutionContext
import utest._

trait WriteEntityServiceBehaviour[IdType,EntityType] extends TestBase {
  implicit def ec: ExecutionContext
  def sut: ServiceRef
  // returns a Map with all entities actually existing in the underlying data source
  def dsEntities: Map[IdType,EntityType]
  // returns an updated version of the specified entity
  def updateForEntity(id: Any): EntityType
  // returns a valid, but not-existent entity
  def notExistentEntity: (IdType,EntityType)
  // returns a new entity instance
  def newEntity(): EntityType

  val tests = TestSuite {
    'update-{
      'exists-{
        val cur = dsEntities.head
        val upd = updateForEntity(cur._1)
        (UpdateEntity(cur._1,upd) >> sut).future map {
          case _ =>
            assert( dsEntities(cur._1) == upd )
        }
      }
//      'notExistent-{
//        val (id,e) = notExistentEntity
//        expectFailure( (UpdateEntity(id,e) >> sut).future )
//      }
    }
    'create-{
      val e = newEntity()
      (CreateEntity(e) >> sut).future map {
        case id =>
          assert( dsEntities(id.asInstanceOf[IdType]) == e )
      }
    }
  }
}

object WriteEntityServiceTest extends WriteEntityServiceBehaviour[Int,String] {
  private var _entities = Map(1 -> "hello")

  override implicit val ec = concurrent.ExecutionContext.global
  override val sut: ServiceRef = ServiceRefFactory.Sync.serviceOf(new TestService)
  override def dsEntities = _entities
  def notExistentEntity = (999,"xyz")
  def newEntity() = "new"

  override def updateForEntity(id: Any): String = id match {
    case 1 => "world"
  }

  class TestService extends WriteEntityService[Int,String] {

    override def checkId(id: Any): Int = id match {
      case id: Int => id
      case _ => throw InvalidIdException(id)
    }

    override def checkEntity(entity: Any): String = entity match {
      case s: String => s
      case _ => throw InvalidEntityException(entity)
    }


    override def createEntity(entity: String): Int = _entities.synchronized{
      val id = _entities.size + 1
      _entities += (id -> entity)
      id
    }

    override def updateEntity(id: Int, entity: String): Unit = _entities.synchronized{
      if(_entities.contains(id))
        _entities += id -> entity
      else
        throw EntityNotFoundException(id)
    }
  }
}
