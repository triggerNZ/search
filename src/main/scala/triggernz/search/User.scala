package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.util.Locale


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

case class UserId(value: Long) extends AnyVal

sealed trait ActiveStatus
object ActiveStatus {
  case object Active extends ActiveStatus
  case object Inactive extends ActiveStatus
}

sealed trait VerificationStatus
object VerificationStatus {
  case object Verified extends VerificationStatus
  case object Unverified extends VerificationStatus
}

sealed trait ShareStatus
object ShareStatus {
  case object Shared  extends ShareStatus
  case object Private extends ShareStatus
}

sealed trait SuspendStatus
object SuspendStatus {
  case object Suspended extends SuspendStatus
  case object NotSuspended extends SuspendStatus
}

//TODO: Validate and move to its own file
case class PhoneNumber(value: String) extends AnyVal

sealed trait Role
object Role {
  case object Admin extends Role
  case object EndUser extends Role
  case object Agent extends Role
}

// TODO: use one of the java timezone classes
case class Timezone(name: String) extends AnyVal