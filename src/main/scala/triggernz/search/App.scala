package triggernz.search

import cats.Id

import Decoders._

object App {

}

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

//TODO: Flesh out
object Queries {
  object Organizations {
    def id(o: Organization) = Vector(o.id.value.toString)
    def name(o: Organization) = Vector(o.name)
    def tags(o: Organization) = o.tags.map(_.value)
    def domainNames(o: Organization): Vector[String] = o.domainNames.map(_.value)
    val all: Vector[Organization => Vector[String]] = Vector(
      id, name, tags, domainNames
    )
  }

  object Users {
    def id(u: User) = Vector(u.id.value.toString)
    def name(u: User) = Vector(u.name)
    def alias(u: User) = u.alias.toVector
    def tags(u: User) = u.tags.map(_.value)

    val all: Vector[User => Vector[String]] =
      Vector(id, name, alias, tags)
  }

  object Tickets {
    def id(t: Ticket) = Vector(t.id.value.toString)
    def subject(t: Ticket) = Vector(t.subject)
    def tags(t: Ticket) = t.tags.map(_.value)

    val all : Vector[Ticket => Vector[String]] =
      Vector(id, subject, tags)
  }

}