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
}

sealed trait VerificationStatus
object VerificationStatus {
  case object Verified extends VerificationStatus
  case object Unverified extends VerificationStatus

  implicit def univEq: UnivEq[VerificationStatus] = UnivEq.derive
}

sealed trait ShareStatus
object ShareStatus {
  case object Shared  extends ShareStatus
  case object Private extends ShareStatus

  implicit def univEq: UnivEq[ShareStatus] = UnivEq.derive
}

sealed trait SuspendStatus
object SuspendStatus {
  case object Suspended extends SuspendStatus
  case object NotSuspended extends SuspendStatus

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
}

case class Timezone(name: String) extends AnyVal
object Timezone {
  implicit def univEq: UnivEq[Timezone] = UnivEq.derive
}