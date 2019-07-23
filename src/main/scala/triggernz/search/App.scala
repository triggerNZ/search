package triggernz.search

import cats.Id
import Decoders._


object App {
  def main(args: Array[String]) = {
    Repl(Stores.orgStores, Stores.userStores, Stores.ticketStores, Tables.organizations, Tables.users, Tables.tickets).start()
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

  val users: TextTable[User] = TextTable(
    Column[User]("Id", 4, _.id.value.toString),
    Column[User]("Name", 15 , _.name),
    Column[User]("Alias", 15, _.alias.getOrElse("")),
    Column[User]("Tags", 40, _.tags.map(_.value).mkString(", ")),
  )

  val tickets: TextTable[Ticket] = TextTable(
    Column[Ticket]("Id", 38, _.id.value.toString),
    Column[Ticket]("Subject", 40, _.subject),
    Column[Ticket]("Tags", 45, _.tags.map(_.value).mkString(", ")),
    Column[Ticket]("Priority", 8, _.priority.toString),
    Column[Ticket]("Type", 8, _.ticketType.toString)
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
    val url = IndexGen((o: Organization) => Vector(o.url.toString))

    val all: Vector[IndexGen[Id, Organization, String]] = Vector(
      id, name, tags, domainNames, url
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
    val subjectWords = IndexGen((t: Ticket) => t.subject.split("[., ]+").toVector.map(_.toLowerCase))
    val tags = IndexGen((t: Ticket) => t.tags.map(_.value.toLowerCase))
    val priority = IndexGen((t: Ticket) => Vector(t.priority.toString.toLowerCase))
    val ticketType = IndexGen((t: Ticket) => t.ticketType.map(_.toString.toLowerCase).toVector)

    val all : Vector[IndexGen[Id, Ticket, String]] =
      Vector(id, subject, subjectWords, tags, priority, ticketType)
  }

}

object Stores {
  import Data._

  lazy val orgIdStore = VectorStore(organizations, Queries.Organizations.id)

  val orgStores: Map[String, Store[Id, String, Organization]] =
    Map(
      "id" -> orgIdStore,
      "name" -> VectorStore(organizations, Queries.Organizations.name),
      "tags" -> VectorStore(organizations, Queries.Organizations.tags),
      "domainNames" -> VectorStore(organizations, Queries.Organizations.domainNames),
      "all" -> VectorStore.build(organizations, Queries.Organizations.all)
    )

  val userStores: Map[String, Store[Id, String, User]] =
    Map(
      "id" -> VectorStore(users, Queries.Users.id),
      "name" -> VectorStore(users, Queries.Users.name, Queries.Users.alias),
      "tags" -> VectorStore(users, Queries.Users.tags),
      "orgName" -> VectorStore(users, Queries.Users.orgName(orgIdStore)),
      "all" -> VectorStore.build(users, Queries.Users.all)
    )

  val ticketStores: Map[String, Store[Id, String, Ticket]] =
    Map(
      "id" -> VectorStore(tickets, Queries.Tickets.id),
      "subject" -> VectorStore(tickets, Queries.Tickets.subject),
      "subjectWords" -> VectorStore(tickets, Queries.Tickets.subjectWords),
      "tags" -> VectorStore(tickets, Queries.Tickets.tags),
      "priority" -> VectorStore(tickets, Queries.Tickets.priority),
      "type" -> VectorStore(tickets, Queries.Tickets.ticketType),
      "all" -> VectorStore.build(tickets, Queries.Tickets.all)
    )
}