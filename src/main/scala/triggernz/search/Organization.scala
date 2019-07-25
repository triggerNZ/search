package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.util.UUID

import japgolly.univeq.UnivEq

import JavaUnivEqs._

case class Organization(id: OrganizationId,
                        url: URL,
                        externalId: ExternalId,
                        name: String,
                        domainNames: Vector[DomainName],
                        details: String,
                        createdAt: ZonedDateTime,
                        sharedTickets: SharedTickets,
                        tags: Vector[Tag])

object Organization {
  implicit def univEq: UnivEq[Organization] = UnivEq.derive
}

case class OrganizationId(value: Long) extends AnyVal
object OrganizationId {
  implicit def univEq: UnivEq[OrganizationId] = UnivEq.derive
}

case class ExternalId(value: UUID) extends AnyVal
object ExternalId {
  implicit def univEq: UnivEq[ExternalId] = UnivEq.derive
}
case class DomainName(value: String) extends AnyVal
object DomainName {
  implicit def univEq: UnivEq[DomainName] = UnivEq.derive
}

sealed trait SharedTickets
object SharedTickets {
  case object Enabled extends SharedTickets
  case object Disabled extends SharedTickets

  implicit def univEq: UnivEq[SharedTickets] = UnivEq.derive

  def searchString(st: SharedTickets) = st match {
    case SharedTickets.Enabled => "Y"
    case SharedTickets.Disabled => "N"
  }
}

case class Tag(value: String) extends AnyVal
object Tag {
  implicit def univEq: UnivEq[Tag] = UnivEq.derive
}