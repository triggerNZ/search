package triggernz.search

import cats.Id

import Decoders._

object App {
  def main(args: Array[String]) = {
    println(Tables.organizations.table(Data.organizations))
  }
}

object Tables {
  import TextTable.Column
  val organizations: TextTable[Organization] =
    TextTable(
      Column[Organization]("Id", 4, _.id.value.toString),
      Column[Organization]("Name", 10, _.name),
      Column[Organization]("URL", 60, _.url.toString),
      Column[Organization]("Domain Names", 55, _.domainNames.map(_.value).mkString(", ")),
      Column[Organization]("Tags", 40, _.tags.map(_.value).mkString(", ")),
      Column[Organization]("Details", 11, _.details),
    )
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
    val id =  IndexGen((o: Organization) => Vector(o.id.value.toString))
    val name = IndexGen((o: Organization) => Vector(o.name))
    val tags = IndexGen((o: Organization) => o.tags.map(_.value))
    val domainNames = IndexGen((o: Organization) => o.domainNames.map(_.value))

    val all: Vector[IndexGen[Id, Organization, String]] = Vector(
      id, name, tags, domainNames
    )
  }

  object Users {
    val id = IndexGen((u: User) => Vector(u.id.value.toString))
    val name = IndexGen((u: User) => Vector(u.name))
    val alias = IndexGen((u: User) => u.alias.toVector)
    val tags = IndexGen((u: User) => u.tags.map(_.value))

    def orgName(orgStore: Store[Id, String, Organization]) =
      IndexGen.Join[Id, User, Organization, String](
      (u: User) => u.organizationId.map(_.value.toString).toVector,
      (o: Organization) => Vector(o.name),
      orgStore
    )

    val all: Vector[IndexGen[Id, User, String]] =
      Vector(id, name, alias, tags)
  }

  object Tickets {
    val id = IndexGen((t: Ticket) => Vector(t.id.value.toString))
    val subject = IndexGen((t: Ticket) => Vector(t.subject))
    val tags = IndexGen((t: Ticket) => t.tags.map(_.value))

    val all : Vector[IndexGen[Id, Ticket, String]] =
      Vector(id, subject, tags)
  }

}