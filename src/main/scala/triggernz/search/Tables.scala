package triggernz.search

import java.time.format.DateTimeFormatter

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
    Column[User]("Name", 20 , _.name),
    Column[User]("Alias", 20, _.alias.getOrElse("")),
    Column[User]("Email", 30, _.email.map(_.toString).getOrElse("")),
    Column[User]("Phone", 15, _.phone.value),
    Column[User]("Timezone", 10, _.timezone.map(_.name).getOrElse("")),
    Column[User]("Tags", 40, _.tags.map(_.value).mkString(", ")),
    Column[User]("Org", 4, _.organizationId.map(_.value.toString).getOrElse("")),

    Column[User]("Active", 6, _.active match {
      case ActiveStatus.Active => "Y"
      case ActiveStatus.Inactive => "N"
    }),
    Column[User]("Verified", 8, _.verified match {
      case None => "N/A"
      case Some(VerificationStatus.Verified) => "Y"
      case Some(VerificationStatus.Unverified) => "N"
    }),
    Column[User]("Suspended", 9, _.suspended match {
      case SuspendStatus.Suspended => "Y"
      case SuspendStatus.NotSuspended => "N"
    }),
    Column[User]("Role", 8, _.role match {
      case Role.EndUser => "end user"
      case Role.Admin => "admin"
      case Role.Agent => "agent"
    })
  )

  val tickets: TextTable[Ticket] = TextTable(
    Column[Ticket]("Id", 38, _.id.value.toString),
    Column[Ticket]("Created", 20, _.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
    Column[Ticket]("Assignee", 8, _.assigneeId.map(_.value.toString).getOrElse("")),
    Column[Ticket]("Submitter", 9, _.submitterId.value.toString),
    Column[Ticket]("Org", 4, _.organizationId.map(_.value.toString).getOrElse("")),
    Column[Ticket]("Subject", 40, _.subject),
    Column[Ticket]("Tags", 45, _.tags.map(_.value).mkString(", ")),
    Column[Ticket]("Priority", 8, _.priority.toString),
    Column[Ticket]("Type", 8, _.ticketType.map(_.toString).getOrElse(""))
  )
}