package triggernz.search

import utest._
import Decoders._
import io.circe.Json
object DecodersTest extends TestSuite {
  override def tests = Tests {
    'decoders - {
      'organization - {
        'decodeExampleFile - {
          val result = Resources.decodeJsonResource[Vector[Organization]]("organizations.json")
          result match {
            case Right(results) => assert(results.length == 25)
            case _              => assert(false)
          }
        }
      }

      'users - {
        'decodeExampleFile - {
          val result = Resources.decodeJsonResource[Vector[User]]("users.json")
          result match {
            case Right(results) => assert(results.length == 75)
            case _              => assert(false)
          }
        }
      }

      'tickets - {
        'decodeExampleFile - {
          val result = Resources.decodeJsonResource[Vector[Ticket]]("tickets.json")
          result match {
            case Right(results) => assert(results.length == 200)
            case _              => assert(false)
          }
        }
      }
    }
  }

}
