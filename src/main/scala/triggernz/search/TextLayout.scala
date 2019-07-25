package triggernz.search

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TextLayout {
  def layoutOrganization(org: Organization): List[String] =
    List(
      s"Id: ${org.id.value}",
      s"URL: ${org.url.toString}",
      s"External Id: ${org.externalId.value}",
      s"Name: ${org.name}",
      s"Details: ${org.details}",
      s"Domain Names: ${org.domainNames.map(_.value).mkString(", ")}",
      s"Tags: ${org.tags.map(_.value).mkString(", ")}",
      s"Created At: ${date(org.createdAt)}",
      s"Shared Tickets: ${sharedTickets(org.sharedTickets)}",
    )

  def layoutUser(u: User, org: Option[Organization]): List[String] = List(
    s"Id: ${u.id.value}",
    s"URL: ${u.url.toString}",
    s"External Id: ${u.externalId.value}",
    s"Name: ${u.name}",
    s"Alias: ${u.alias.getOrElse("")}",
    s"Created At: ${date(u.createdAt)}",
    s"Last Login At: ${date(u.lastLoginAt)}",
    s"Organization:"
  ) ++ org.toList.flatMap(o => indent(layoutOrganization(o))) ++ List(
    s"Timezone: ${u.timezone.map(_.name).getOrElse("")}",
    s"Role: ${role(u.role)}",
    s"Active: ${active(u.active)}",
    s"Suspended: ${suspended(u.suspended)}",
    s"Shared: ${shared(u.shared)}",
    s"Phone: ${u.phone.value}",
    s"Email: ${u.email.map(_.toString).getOrElse("")}",
    s"Signature: ${u.signature}",
    s"Locale: ${u.locale.map(_.toString).getOrElse("")}",
    s"Timezone: ${u.timezone.map(_.name).getOrElse("")}",
    s"Tags: ${u.tags.map(_.value).mkString(", ")}"
  )

  def layoutTicket(t: Ticket,
                   org: Option[Organization],
                   assignee: Option[User],
                   assigneeOrg: Option[Organization],
                   submitter: User,
                   submitterOrg: Option[Organization]): List[String] = {
    List(
      s"Id: ${t.id.value}",
      s"URL: ${t.url}",
      s"External Id: ${t.externalId.value}",
      s"Type: ${t.ticketType.map(_.toString).getOrElse("")}",
      s"Priority: ${t.priority}",
      s"Status: ${t.status}",
      s"Created At: ${date(t.createdAt)}",
      s"Subject: ${t.subject}",
      s"Description: ${t.description.getOrElse("")}",
      s"Submitter:"
    ) ++ indent(layoutUser(submitter, submitterOrg)) ++ List(
      "Assignee:"
    ) ++ assignee.toList.flatMap(a => indent(layoutUser(a, assigneeOrg))) ++ List(
      "Organization:"
    ) ++ org.toList.flatMap(o => indent(layoutOrganization(o))) ++ List(
      s"Tags: ${t.tags.map(_.value).mkString(", ")}",
      s"Has Incidents: ${if (t.hasIncidents) "Y" else "N"}",
      s"Due At: ${t.dueAt.map(date).getOrElse("")}",
      s"Via: ${t.via}"
    )
  }

  private def date(dt: ZonedDateTime) =
    dt.format(DateTimeFormatter.RFC_1123_DATE_TIME)

  private def active(a: ActiveStatus): String = a match {
    case ActiveStatus.Active => "Y"
    case ActiveStatus.Inactive => "N"
  }

  private def sharedTickets(s: SharedTickets): String = s match {
    case SharedTickets.Enabled => "Y"
    case SharedTickets.Disabled => "N"
  }

  private def shared(s: ShareStatus): String = s match {
    case ShareStatus.Shared => "Y"
    case ShareStatus.Private => "N"
  }

  private def suspended(s: SuspendStatus) = s match {
    case SuspendStatus.Suspended => "Y"
    case SuspendStatus.NotSuspended => "N"
  }

  private def role(r: Role) = r match {
    case Role.Agent => "Agent"
    case Role.Admin => "Admin"
    case Role.EndUser => "End User"
  }


  private def indent(strs: List[String]): List[String] =
    strs.map("    " + _)
}