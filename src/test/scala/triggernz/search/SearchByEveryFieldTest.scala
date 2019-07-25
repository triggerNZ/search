package triggernz.search

import io.circe.{Decoder, Json}
import utest._
import Decoders._

object SearchByEveryFieldTest extends TestSuite {
  override def tests = Tests {
    'organizations -
      testDataset(
        Resources.decodeJsonResource[List[Json]]("organizations.json").getOrElse(sys.error("decoding error")),
        Stores.orgStores)

    'users -
      testDataset(
        Resources.decodeJsonResource[List[Json]]("users.json").getOrElse(sys.error("decoding error")),
        Stores.userStores)

    'tickets -
      testDataset(
        Resources.decodeJsonResource[List[Json]]("tickets.json").getOrElse(sys.error("decoding error")),
        Stores.ticketStores)

  }

  def leaves(js: Json): Vector[String] =
    js.fold(
      Vector(""),
      b => Vector(YesNo(b)),
      num => Vector(num.toString),
      str => Vector(str),
      arr => arr.flatMap(leaves),
      obj => obj.values.flatMap(leaves).toVector
    )

  def testDataset[T: Decoder](allJsons: List[Json], stores: Map[String, Store[String, T]]) = {
    // For every leaf field in the json, the field should locate the value in at least one index
    allJsons.foreach { js =>
      val leafValues = leaves(js)
      val actualValue = js.as[T].getOrElse(sys.error("decode error"))
      leafValues.foreach { leaf =>
        val foundStores = stores.exists {
          case (_, store) =>
            store.lookupAndRetrieveMany(Vector(leaf, leaf.toLowerCase)).contains(actualValue)
        }
        assert(foundStores)
      }
    }
  }
}
