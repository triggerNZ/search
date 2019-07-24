package triggernz.search

import cats.Id

object Queries {
  object Organizations {
    val id =  IndexGen((o: Organization) => Vector(o.id))
    val idString =  IndexGen((o: Organization) => Vector(o.id.value.toString))

    val name = IndexGen((o: Organization) => Vector(o.name))
    val tags = IndexGen((o: Organization) => o.tags.map(_.value))
    val domainNames = IndexGen((o: Organization) => o.domainNames.map(_.value))
    val url = IndexGen((o: Organization) => Vector(o.url.toString))

    val all: Vector[IndexGen[Id, Organization, String]] = Vector(
      idString, name, tags, domainNames, url
    )
  }

  object Users {
    val id = IndexGen((u: User) => Vector(u.id))
    val idString = IndexGen((u: User) => Vector(u.id.value.toString))
    val name = IndexGen((u: User) => Vector(u.name))
    val alias = IndexGen((u: User) => u.alias.toVector)
    val tags = IndexGen((u: User) => u.tags.map(_.value))

    def orgName(orgStore: Store[Id, OrganizationId, Organization]) =
      IndexGen.Join(
        (u: User) => u.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    val all: Vector[IndexGen[Id, User, String]] =
      Vector(idString, name, alias, tags)
  }

  object Tickets {
    val id = IndexGen((t: Ticket) => Vector(t.id.value.toString))
    val subject = IndexGen((t: Ticket) => Vector(t.subject))
    val subjectWords = IndexGen((t: Ticket) => t.subject.split("[., ]+").toVector.map(_.toLowerCase))
    val tags = IndexGen((t: Ticket) => t.tags.map(_.value))
    val priority = IndexGen((t: Ticket) => Vector(t.priority.toString))
    val ticketType = IndexGen((t: Ticket) => t.ticketType.map(_.toString).toVector)

    def assigneeName(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => t.assigneeId.toVector,
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )
    def submitterName(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => Vector(t.submitterId),
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )

    def orgName(orgStore: Store[Id, OrganizationId, Organization]) =
      IndexGen.Join(
        (t: Ticket) => t.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    def userName(userStore: Store[Id, UserId, User]) =
      Vector(submitterName(userStore), assigneeName(userStore))

    val all : Vector[IndexGen[Id, Ticket, String]] =
      Vector(id, subject, subjectWords, tags, priority, ticketType)
  }

}