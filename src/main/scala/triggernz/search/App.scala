package triggernz.search


object App {
  def main(args: Array[String]) = {
    Repl(Stores.orgStores, Stores.userStores, Stores.ticketStores, Stores.userIdStore, Stores.orgIdStore).start()
  }
}

object Stores {
  import Data._

  lazy val orgIdStore = VectorStore(organizations, Queries.Organizations.id)
  lazy val userIdStore =  VectorStore(users, Queries.Users.id)

  val orgStores: Map[String, Store[String, Organization]] =
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
    )

  val userStores: Map[String, Store[String, User]] =
    Map(
      "id" -> VectorStore(users, Queries.Users.idString),
      "url" -> VectorStore(users, Queries.Users.url),
      "externalId" -> VectorStore(users, Queries.Users.externalId),
      "name" -> VectorStore(users, Queries.Users.name),
      "alias" -> VectorStore(users, Queries.Users.alias),
      "nameOrAlias" -> VectorStore(users, Queries.Users.name, Queries.Users.alias),
      "createdAt" -> VectorStore(users, Queries.Users.createdAt),
      "lastLoginAt" -> VectorStore(users, Queries.Users.lastLoginAt),
      "active" -> VectorStore(users, Queries.Users.active),
      "verified" -> VectorStore(users, Queries.Users.verified),
      "shared" -> VectorStore(users, Queries.Users.shared),
      "locale" -> VectorStore(users, Queries.Users.locale),
      "email" -> VectorStore(users, Queries.Users.email),
      "phone" -> VectorStore(users, Queries.Users.phone),
      "signature" -> VectorStore(users, Queries.Users.signature),
      "timezone" -> VectorStore(users, Queries.Users.timezone),
      "tags" -> VectorStore(users, Queries.Users.tags),
      "orgName" -> VectorStore(users, Queries.Users.orgName(orgIdStore)),
      "orgId" -> VectorStore(users, Queries.Users.orgId(orgIdStore)),
      "role" -> VectorStore(users, Queries.Users.role),

    )

  val ticketStores: Map[String, Store[String, Ticket]] =
    Map(
      "id" -> VectorStore(tickets, Queries.Tickets.id),
      "url" -> VectorStore(tickets, Queries.Tickets.url),
      "externalId" -> VectorStore(tickets, Queries.Tickets.externalId),
      "createdAt" -> VectorStore(tickets, Queries.Tickets.createdAt),
      "dueAt" -> VectorStore(tickets, Queries.Tickets.dueAt),
      "subject" -> VectorStore(tickets, Queries.Tickets.subject),
      "subjectWords" -> VectorStore(tickets, Queries.Tickets.subjectWords),
      "description" -> VectorStore(tickets, Queries.Tickets.subject),
      "descriptionWords" -> VectorStore(tickets, Queries.Tickets.subjectWords),
      "tags" -> VectorStore(tickets, Queries.Tickets.tags),
      "priority" -> VectorStore(tickets, Queries.Tickets.priority),
      "status" -> VectorStore(tickets, Queries.Tickets.status),
      "type" -> VectorStore(tickets, Queries.Tickets.ticketType),
      "submitterName" -> VectorStore(tickets, Queries.Tickets.submitterName(userIdStore)),
      "submitterId" -> VectorStore(tickets, Queries.Tickets.submitterId(userIdStore)),
      "assigneeName" -> VectorStore(tickets, Queries.Tickets.assigneeName(userIdStore)),
      "assigneeId" -> VectorStore(tickets, Queries.Tickets.assigneeId(userIdStore)),
      "user" -> VectorStore.build(tickets, Queries.Tickets.userName(userIdStore)),
      "orgName" -> VectorStore(tickets, Queries.Tickets.orgName(orgIdStore)),
      "orgId" -> VectorStore(tickets, Queries.Tickets.orgId(orgIdStore)),
      "hasIncidents" -> VectorStore(tickets, Queries.Tickets.hasIncidents),
      "via" -> VectorStore(tickets, Queries.Tickets.via),
    )
}