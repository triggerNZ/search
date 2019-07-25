package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.util.Locale

import japgolly.univeq.UnivEq

import JavaUnivEqs._

case class User(id: UserId,
                url: URL,
                externalId: ExternalId,
                name: String,
                alias: Option[String],
                createdAt: ZonedDateTime,
                active: ActiveStatus,
                verified: Option[VerificationStatus],
                shared: ShareStatus,
                locale: Option[Locale],
                timezone: Option[Timezone],
                lastLoginAt: ZonedDateTime,
                email: Option[Email],
                phone: PhoneNumber,
                signature: String,
                organizationId: Option[OrganizationId],
                tags: Vector[Tag],
                suspended: SuspendStatus,
                role: Role)

object User {
  implicit def univEq: UnivEq[User] = UnivEq.derive
}

case class UserId(value: Long) extends AnyVal
object UserId {
  implicit def univEq: UnivEq[UserId] = UnivEq.derive
}

sealed trait ActiveStatus
object ActiveStatus {
  case object Active extends ActiveStatus
  case object Inactive extends ActiveStatus

  implicit def univEq: UnivEq[ActiveStatus] = UnivEq.derive

  def searchString(active: ActiveStatus) = active match {
    case ActiveStatus.Active => "Y"
    case ActiveStatus.Inactive => "N"
  }
}

sealed trait VerificationStatus
object VerificationStatus {
  case object Verified extends VerificationStatus
  case object Unverified extends VerificationStatus

  implicit def univEq: UnivEq[VerificationStatus] = UnivEq.derive

  def searchString(vs: VerificationStatus) = vs match {
    case VerificationStatus.Verified => "Y"
    case VerificationStatus.Unverified => "N"
  }
}

sealed trait ShareStatus
object ShareStatus {
  case object Shared  extends ShareStatus
  case object Private extends ShareStatus

  def searchString(ss: ShareStatus) = ss match {
    case ShareStatus.Shared => "Y"
    case ShareStatus.Private => "N"
  }

  implicit def univEq: UnivEq[ShareStatus] = UnivEq.derive
}

sealed trait SuspendStatus
object SuspendStatus {
  case object Suspended extends SuspendStatus
  case object NotSuspended extends SuspendStatus

  def searchString(ss: SuspendStatus) = ss match {
    case SuspendStatus.Suspended => "Y"
    case SuspendStatus.NotSuspended => "N"
  }

  implicit def univEq: UnivEq[SuspendStatus] = UnivEq.derive
}

case class PhoneNumber(value: String) extends AnyVal
object PhoneNumber {
  implicit def univEq: UnivEq[PhoneNumber] = UnivEq.derive
}

sealed trait Role
object Role {
  case object Admin extends Role
  case object EndUser extends Role
  case object Agent extends Role

  implicit def univEq: UnivEq[Role] = UnivEq.derive

  def searchString(r: Role) = r match {
    case Role.Agent => "Agent"
    case Role.Admin => "Admin"
    case Role.EndUser => "End User"
  }
}

case class Timezone(name: String) extends AnyVal
object Timezone {
  implicit def univEq: UnivEq[Timezone] = UnivEq.derive
}