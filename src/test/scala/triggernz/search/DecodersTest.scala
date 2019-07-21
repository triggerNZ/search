package triggernz.search

import utest._
import Decoders._
import io.circe.Json
object DecodersTest extends TestSuite {
  override def tests = Tests {
    'decoders - {
      'organization - {
        'decodeExampleFile - {
          val result = TestUtil.decodeJsonResource[Vector[Organization]]("organizations.json")
          result match {
            case Right(results) => assert(results.length == 25)
            case _              => assert(false)
          }
        }
      }

      'users - {
        'decodeExampleFile - {
          val result = TestUtil.decodeJsonResource[Vector[User]]("users.json")
          result match {
            case Right(results) => assert(results.length == 75)
            case _              => assert(false)
          }
        }
      }
    }
  }

}
