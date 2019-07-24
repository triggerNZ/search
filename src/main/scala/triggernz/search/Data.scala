package triggernz.search
import Decoders._
object Data {
  lazy val organizations: Vector[Organization] =
    Resources.decodeJsonResource[Vector[Organization]]("organizations.json")
      .getOrElse(sys.error("Failed loading organizations"))


  lazy val users: Vector[User] =
    Resources.decodeJsonResource[Vector[User]]("users.json")
      .getOrElse(sys.error("Failed loading users"))

  lazy val tickets: Vector[Ticket] =
    Resources.decodeJsonResource[Vector[Ticket]]("tickets.json")
      .getOrElse(sys.error("Failed loading users"))
}
