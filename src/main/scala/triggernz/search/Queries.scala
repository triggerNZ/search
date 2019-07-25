package triggernz.search

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import cats.Id

object Queries {
  private def date(d: ZonedDateTime) =
    Vector(d.format(DateTimeFormatter.ISO_DATE_TIME), d.format(DateTimeFormatter.RFC_1123_DATE_TIME))

  object Organizations {
    val id =  IndexGen((o: Organization) => o.id)
    val idString =  IndexGen((o: Organization) => o.id.value.toString)
    val url = IndexGen((o: Organization) => o.url.toString)
    val externalId = IndexGen((o: Organization) => o.externalId.value.toString)
    val name = IndexGen((o: Organization) => o.name)
    val domainNames = IndexGen.many((o: Organization) => o.domainNames.map(_.value))
    val createdAt = IndexGen.many((o: Organization) => date(o.createdAt))
    val details = IndexGen((o: Organization) => o.details)
    val sharedTickets = IndexGen((o: Organization) => o.sharedTickets match {
      case SharedTickets.Enabled => "Y"
      case SharedTickets.Disabled => "N"
    })
    val tags = IndexGen.many((o: Organization) => o.tags.map(_.value))
  }

  object Users {
    val id = IndexGen((u: User) => u.id)
    val idString = IndexGen((u: User) => u.id.value.toString)
    val url = IndexGen((u: User) => u.url.toString)
    val externalId = IndexGen((u: User) => u.externalId.value.toString)
    val createdAt = IndexGen.many((u: User) => date(u.createdAt))
    val lastLoginAt = IndexGen.many((u: User) => date(u.lastLoginAt))
    val active = IndexGen((u: User) => u.active match {
      case ActiveStatus.Active => "Y"
      case ActiveStatus.Inactive => "N"
    })
    val verified = IndexGen((u: User) => u.verified.map {
      case VerificationStatus.Verified => "Y"
      case VerificationStatus.Unverified => "N"
    }.getOrElse(""))

    val shared = IndexGen((u: User) => u.shared match {
      case ShareStatus.Shared => "Y"
      case ShareStatus.Private => "N"
    })
    val locale = IndexGen((u: User) => u.locale.map(_.toString).getOrElse(""))
    val name = IndexGen((u: User) => u.name)
    val email = IndexGen((u: User) => u.email.map(_.toString).getOrElse(""))
    val phone = IndexGen((u: User) => u.phone.value)
    val alias = IndexGen((u: User) => u.alias.getOrElse(""))
    val signature = IndexGen((u: User) => u.signature)
    val tags = IndexGen.many((u: User) => u.tags.map(_.value))

    val timezone = IndexGen((u: User) => u.timezone.map(_.name).getOrElse(""))
    def orgName(orgStore: Store[Id, OrganizationId, Organization]) =
      IndexGen.Join(
        (u: User) => u.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    def orgId(orgStore: Store[Id, OrganizationId, Organization]) =
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
    val ticketType = IndexGen((t: Ticket) => t.ticketType.map(_.toString).getOrElse(""))
    val subject = IndexGen((t: Ticket) => t.subject)
    val subjectWords = IndexGen.many((t: Ticket) => t.subject.split("[., ]+").toVector.map(_.toLowerCase))
    val description = IndexGen((t: Ticket) => t.description.getOrElse(""))
    val descriptionWords = IndexGen.many((t: Ticket) => t.description.toVector.flatMap(_.split("[., ]+")).map(_.toLowerCase))
    val tags = IndexGen.many((t: Ticket) => t.tags.map(_.value))
    val priority = IndexGen((t: Ticket) => t.priority.toString)
    val status = IndexGen((t: Ticket) => t.status.toString)

    def assigneeId(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => t.assigneeId.toVector,
        (u: User) => Vector(u.id.value.toString),
        userStore
      )
    def assigneeName(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => t.assigneeId.toVector,
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )

    def submitterId(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => Vector(t.submitterId),
        (u: User) => Vector(u.id.value.toString),
        userStore
      )

    def submitterName(userStore: Store[Id, UserId, User]) =
      IndexGen.Join(
        (t: Ticket) => Vector(t.submitterId),
        (u: User) => Vector(u.name) ++ u.alias.toVector,
        userStore
      )

    def orgId(orgStore: Store[Id, OrganizationId, Organization]) =
      IndexGen.Join(
        (t: Ticket) => t.organizationId.toVector,
        (o: Organization) => Vector(o.id.value.toString),
        orgStore
      )

    def orgName(orgStore: Store[Id, OrganizationId, Organization]) =
      IndexGen.Join(
        (t: Ticket) => t.organizationId.toVector,
        (o: Organization) => Vector(o.name),
        orgStore
      )

    def userName(userStore: Store[Id, UserId, User]) =
      Vector(submitterName(userStore), assigneeName(userStore))

    val hasIncidents = IndexGen((t: Ticket) => t.hasIncidents match {
      case true => "Y"
      case false => "F"
    })

    val via = IndexGen((t: Ticket) => t.via.toString)
  }

}