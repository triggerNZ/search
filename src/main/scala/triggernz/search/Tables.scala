package triggernz.search

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
    Column[Ticket]("Type", 8, _.ticketType.map(_.toString).getOrElse(""))
  )
}