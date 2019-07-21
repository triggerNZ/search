package triggernz.search

import io.circe._


object TestUtil {
  // Don't handle failure. It is convenience method for tests. Failure = failing tests (probably a path typo) which is
  // what we want.
  def readResource(resourcePath: String): String =
    scala.io.Source.fromInputStream(
      getClass.getClassLoader().getResourceAsStream(resourcePath)
    ).mkString("")


  // Here however, we definitely do want to propagate errors, as we want to assert things about them.
  def decodeJsonResource[T : Decoder](resourcePath: String) = {
    val asString = readResource(resourcePath)
    parser.parse(asString).flatMap(_.as[T])
  }

}
