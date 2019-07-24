package triggernz.search

import cats.Id


object App {
  def main(args: Array[String]) = {
    Repl(Stores.orgStores, Stores.userStores, Stores.ticketStores, Stores.userIdStore, Stores.orgIdStore).start()
  }
}

object Stores {
  import Data._

  lazy val orgIdStore = VectorStore(organizations, Queries.Organizations.id)
  lazy val userIdStore =  VectorStore(users, Queries.Users.id)

  val orgStores: Map[String, Store[Id, String, Organization]] =
    Map(
      "id" -> VectorStore(organizations, Queries.Organizations.idString),
      "url" -> VectorStore(organizations, Queries.Organizations.url),
      "externalId" -> VectorStore(organizations, Queries.Organizations.externalId),
      "name" -> VectorStore(organizations, Queries.Organizations.name),
      "domainNames" -> VectorStore(organizations, Queries.Organizations.domainNames),
      "createdAt" -> VectorStore(organizations, Queries.Organizations.createdAt),
      "details" -> VectorStore(organizations, Queries.Organizations.details),
      "tags" -> VectorStore(organizations, Queries.Organizations.tags),
      "sharedTickets" -> VectorStore(organizations, Queries.Organizations.sharedTickets),
      "all" -> VectorStore.build(organizations, Queries.Organizations.all)
    )

  val userStores: Map[String, Store[Id, String, User]] =
    Map(
      "id" -> VectorStore(users, Queries.Users.idString),
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
      "submitter" -> VectorStore(tickets, Queries.Tickets.submitterName(userIdStore)),
      "assignee" -> VectorStore(tickets, Queries.Tickets.assigneeName(userIdStore)),
      "user" -> VectorStore.build(tickets, Queries.Tickets.userName(userIdStore)),
      "orgName" -> VectorStore(tickets, Queries.Tickets.orgName(orgIdStore)),
      "all" -> VectorStore.build(tickets, Queries.Tickets.all)
    )
}