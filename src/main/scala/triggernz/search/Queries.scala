package triggernz.search

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import cats.Id

object Queries {
  private def date(d: ZonedDateTime) =
    Vector(d.format(DateTimeFormatter.ISO_DATE_TIME), d.format(DateTimeFormatter.RFC_1123_DATE_TIME), d.format(Decoders.DateTimeFormat))

  object Organizations {
    val id =  IndexGen((o: Organization) => o.id)
    val idString =  IndexGen((o: Organization) => o.id.value.toString)
    val url = IndexGen((o: Organization) => o.url.toString)
    val externalId = IndexGen((o: Organization) => o.externalId.value.toString)
    val name = IndexGen((o: Organization) => o.name)
    val domainNames = IndexGen.many((o: Organization) => o.domainNames.map(_.value))
    val createdAt = IndexGen.many((o: Organization) => date(o.createdAt))
    val details = IndexGen((o: Organization) => o.details)
    val sharedTickets = IndexGen((o: Organization) => SharedTickets.searchString(o.sharedTickets))
    val tags = IndexGen.many((o: Organization) => o.tags.map(_.value))
  }

  object Users {
    val id = IndexGen((u: User) => u.id)
    val idString = IndexGen((u: User) => u.id.value.toString)
    val url = IndexGen((u: User) => u.url.toString)
    val externalId = IndexGen((u: User) => u.externalId.value.toString)
    val createdAt = IndexGen.many((u: User) => date(u.createdAt))
    val lastLoginAt = IndexGen.many((u: User) => date(u.lastLoginAt))
    val active = IndexGen((u: User) => ActiveStatus.searchString(u.active))
    val verified = IndexGen((u: User) => u.verified.map(VerificationStatus.searchString).getOrElse(""))
    val suspended = IndexGen((u: User) => SuspendStatus.searchString(u.suspended))
    val shared = IndexGen((u: User) => ShareStatus.searchString(u.shared))
    val locale = IndexGen((u: User) => u.locale.map(_.toString).getOrElse(""))
    val name = IndexGen((u: User) => u.name)
    val email = IndexGen((u: User) => u.email.map(_.toString).getOrElse(""))
    val phone = IndexGen((u: User) => u.phone.value)
    val alias = IndexGen((u: User) => u.alias.getOrElse(""))
    val signature = IndexGen((u: User) => u.signature)
    val tags = IndexGen.many((u: User) => u.tags.map(_.value))
    val role = IndexGen.many((u: User) => {
      val str = Role.searchString(u.role)
      Vector(str, str.toLowerCase(), str.toLowerCase().replace(' ', '-'))
    })

    val timezone = IndexGen((u: User) => u.timezone.map(_.name).getOrElse(""))
    def orgName(orgStore: Store[OrganizationId, Organization]) =
      IndexGen.Join(
        (u: User) => u.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    def orgId(orgStore: Store[OrganizationId, Organization]) =
      IndexGen.Join(
        (u: User) => u.organizationId.toVector,
        (o: Organization) => Vector(o.id.value.toString),
        orgStore
      )
  }

  object Tickets {
    val id = IndexGen((t: Ticket) => t.id.value.toString)
    val url = IndexGen((t: Ticket) => t.url.toString)
    val externalId = IndexGen((t: Ticket) => t.externalId.value.toString)
    val createdAt = IndexGen.many((t: Ticket) => date(t.createdAt))
    val dueAt = IndexGen.many((t: Ticket) => t.dueAt.toVector.flatMap(date))
    val ticketType = IndexGen.many { (t: Ticket) =>
      val str = t.ticketType.map(_.toString).getOrElse("")
      Vector(str, str.toLowerCase()).distinct
    }
    val subject = IndexGen((t: Ticket) => t.subject)
    val subjectWords = IndexGen.many((t: Ticket) => t.subject.split("[., ]+").toVector.map(_.toLowerCase))
    val description = IndexGen((t: Ticket) => t.description.getOrElse(""))
    val descriptionWords = IndexGen.many((t: Ticket) => t.description.toVector.flatMap(_.split("[., ]+")).map(_.toLowerCase))
    val tags = IndexGen.many((t: Ticket) => t.tags.map(_.value))
    val priority = IndexGen.many { (t: Ticket) =>
      val str = t.priority.toString
      Vector(str, str.toLowerCase())
    }
    val status = IndexGen.many {(t: Ticket) =>
      val str = t.status.toString
      Vector(str, str.toLowerCase)
    }

    def assigneeId(userStore: Store[UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => t.assigneeId.toVector,
        (u: User) => Vector(u.id.value.toString),
        userStore
      )
    def assigneeName(userStore: Store[UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => t.assigneeId.toVector,
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )

    def submitterId(userStore: Store[UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => Vector(t.submitterId),
        (u: User) => Vector(u.id.value.toString),
        userStore
      )

    def submitterName(userStore: Store[UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => Vector(t.submitterId),
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )

    def orgId(orgStore: Store[OrganizationId, Organization]) =
      IndexGen.Join(
        (t: Ticket) => t.organizationId.toVector,
        (o: Organization) => Vector(o.id.value.toString),
        orgStore
      )

    def orgName(orgStore: Store[OrganizationId, Organization]) =
      IndexGen.Join(
        (t: Ticket) => t.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    def userName(userStore: Store[UserId, User]) =
      Vector(submitterName(userStore), assigneeName(userStore))

    val hasIncidents = IndexGen((t: Ticket) => YesNo(t.hasIncidents))

    val via = IndexGen.many {(t: Ticket) =>
      val str = t.via.toString
      Vector(str, str.toLowerCase())

    }
  }

}