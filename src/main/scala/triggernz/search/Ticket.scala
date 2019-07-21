package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

case class Ticket(id: TicketId,
                  url: URL,
                  externalId: ExternalId,
                  createdAt: ZonedDateTime,
                  ticketType: Option[TicketType],
                  subject: String,
                  description: Option[String],
                  priority: Priority,
                  status: Status,
                  submitterId: UserId,
                  assigneeId: Option[UserId],
                  organizationId: Option[OrganizationId],
                  tags: Vector[Tag],
                  hasIncidents: Boolean,
                  dueAt: Option[ZonedDateTime],
                  via: Channel
                 )

case class TicketId(value: UUID) extends AnyVal

sealed trait TicketType
object TicketType {
  case object Incident extends TicketType
  case object Task extends TicketType
  case object Problem extends TicketType
  case object Question extends TicketType
}

sealed trait Priority
object Priority {
  case object Urgent extends Priority
  case object High extends Priority
  case object Normal extends Priority
  case object Low extends Priority
}

sealed trait Status
object Status {
  case object Pending extends Status
  case object Open extends Status
  case object Hold extends Status
  case object Closed extends Status
  case object Solved extends Status
}

sealed trait Channel
object Channel {
  case object Web extends Channel
  case object Chat extends Channel
  case object Voice extends Channel
}