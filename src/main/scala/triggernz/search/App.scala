package triggernz.search

import cats.Id


object App {
  def main(args: Array[String]) = {
    Repl(Stores.orgStores, Stores.userStores, Stores.ticketStores, Tables.organizations, Tables.users, Tables.tickets).start()
  }
}

object Stores {
  import Data._

  lazy val orgIdStore = VectorStore(organizations, Queries.Organizations.id)
  lazy val userIdStore =  VectorStore(users, Queries.Users.id)
  lazy val ticketIdStore = VectorStore(tickets, Queries.Tickets.id)

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
      "id" -> userIdStore,
      "name" -> VectorStore(users, Queries.Users.name, Queries.Users.alias),
      "tags" -> VectorStore(users, Queries.Users.tags),
      "orgName" -> VectorStore(users, Queries.Users.orgName(orgIdStore)),
      "all" -> VectorStore.build(users, Queries.Users.all)
    )

  val ticketStores: Map[String, Store[Id, String, Ticket]] =
    Map(
      "id" -> ticketIdStore,
      "subject" -> VectorStore(tickets, Queries.Tickets.subject),
      "subjectWords" -> VectorStore(tickets, Queries.Tickets.subjectWords),
      "tags" -> VectorStore(tickets, Queries.Tickets.tags),
      "priority" -> VectorStore(tickets, Queries.Tickets.priority),
      "type" -> VectorStore(tickets, Queries.Tickets.ticketType),
      "all" -> VectorStore.build(tickets, Queries.Tickets.all)
    )
}