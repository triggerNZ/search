package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID


case class Organization(id: OrganizationId,
                        url: URL,
                        externalId: ExternalId,
                        name: String,
                        domainNames: Vector[DomainName],
                        details: String,
                        createdAt: ZonedDateTime,
                        sharedTickets: SharedTickets,
                        tags: Vector[Tag])

case class OrganizationId(value: Long) extends AnyVal
case class ExternalId(value: UUID) extends AnyVal
case class DomainName(value: String) extends AnyVal

sealed trait SharedTickets
object SharedTickets {
  case object Enabled extends SharedTickets
  case object Disabled extends SharedTickets
}

case class Tag(value: String) extends AnyVal