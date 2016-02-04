//     Project: surfice-entity (https://github.com/jokade/surfice-entity)
//      Module: shared / test
// Description: Base class for all tests.

// Copyright (c) 2016. Distributed under the MIT License (see included LICENSE file).
package surfice.entity.test

import utest._

import scala.concurrent.{ExecutionContext, Future}

abstract class TestBase extends TestSuite {

  def expectFailure(f: Future[_])(implicit ec: ExecutionContext): Future[Any] =
    f.map( _ => throw ExpectedFailure).recoverWith{
      case ExpectedFailure => Future.failed(ExpectedFailure)
      case ex:Throwable => Future.successful(ex)
    }

  case object ExpectedFailure extends RuntimeException
}
