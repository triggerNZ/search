package triggernz.search

import java.net.URL
import java.time.ZonedDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.util.{Locale, UUID}

import io.circe.Decoder

import scala.util.Try

import cats.instances.either._
import cats.syntax.option._


object Decoders {
  implicit val decodeOrganizationId: Decoder[OrganizationId] =
    Decoder.decodeLong.map(OrganizationId)

  implicit val decodeUrl: Decoder[URL] =
    Decoder.decodeString.emapTry { s =>
      Try(new URL(s))
    }

  private val decodeUuid: Decoder[UUID] =
    Decoder.decodeString.emapTry { s =>
      Try(UUID.fromString(s))
    }

  implicit val decodeExternalId: Decoder[ExternalId] =
    decodeUuid.map(ExternalId)

  implicit val decodeDomainName: Decoder[DomainName] =
    Decoder.decodeString.map(DomainName)

  implicit val decodeSharedTickets: Decoder[SharedTickets] =
    Decoder.decodeBoolean.map {
      case false => SharedTickets.Disabled
      case true => SharedTickets.Enabled
    }

  implicit val decodeTag: Decoder[Tag] =
    Decoder.decodeString.map(Tag)

  implicit val decodeZonedDateTime: Decoder[ZonedDateTime] =
    Decoder.decodeZonedDateTimeWithFormatter {
      new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .appendLiteral(' ')
        .appendOffsetId()
        .toFormatter()
    }

  implicit val decodeOrganization: Decoder[Organization] =
    Decoder.forProduct9(
      "_id",
      "url",
      "external_id",
      "name",
      "domain_names",
      "details",
      "created_at",
      "shared_tickets",
      "tags")(Organization.apply)

  implicit val decodeUserId: Decoder[UserId] =
    Decoder.decodeLong.map(UserId)

  implicit val decodeActiveStatus: Decoder[ActiveStatus] =
    Decoder.decodeBoolean.map {
      case true => ActiveStatus.Active
      case false => ActiveStatus.Inactive
    }

  implicit val decodeVerificationStatus: Decoder[VerificationStatus] =
    Decoder.decodeBoolean.map {
      case true => VerificationStatus.Verified
      case false => VerificationStatus.Unverified
    }

  implicit val decodeShareStatus: Decoder[ShareStatus] =
    Decoder.decodeBoolean.map {
      case true => ShareStatus.Shared
      case false => ShareStatus.Private
    }

  implicit val decodeLocale: Decoder[Locale] =
    filter(
      Decoder.decodeString.map(new Locale(_)),
      locale => LocaleUtil.isValid(locale),
      locale => s"${locale} is not a valid locale")

  implicit val decodeEmail: Decoder[Email] =
    Decoder.decodeString.emap { s =>
      Email.fromString(s).liftTo[Either[String, ?]](s"'${s}' is not a valid email")
    }

  implicit val decodePhoneNumber: Decoder[PhoneNumber] =
    Decoder.decodeString.map(PhoneNumber)

  implicit val decodeSuspendStatus: Decoder[SuspendStatus] =
    Decoder.decodeBoolean.map {
      case true  => SuspendStatus.Suspended
      case false => SuspendStatus.NotSuspended
    }

  implicit val decodeRole: Decoder[Role] =
    Decoder.decodeString.emap {
      case "admin" => Right(Role.Admin)
      case "end-user" => Right(Role.EndUser)
      case "agent" => Right(Role.Agent)
      case other => Left(s"Role '${other}' is not supported")
    }

  implicit val decodeTimezone: Decoder[Timezone] =
    Decoder.decodeString.map(Timezone)

  implicit val decodeTicketId: Decoder[TicketId] =
    decodeUuid.map(TicketId)

  implicit val decodeTicketType: Decoder[TicketType] =
    Decoder.decodeString.emap {
      case "task" => Right(TicketType.Task)
      case "incident" => Right(TicketType.Incident)
      case "question" => Right(TicketType.Question)
      case "problem" => Right(TicketType.Problem)
      case other      => Left(s"'${other}' is not a valid ticket type")
    }

  implicit val decodePriority: Decoder[Priority] =
    Decoder.decodeString.emap {
      case "urgent" => Right(Priority.Urgent)
      case "high" => Right(Priority.High)
      case "normal" => Right(Priority.Normal)
      case "low" => Right(Priority.Low)
      case other      => Left(s"'${other}' is not a valid priority")
    }

  implicit val decodeStatus: Decoder[Status] =
    Decoder.decodeString.emap {
      case "pending" => Right(Status.Pending)
      case "open" => Right(Status.Open)
      case "closed" => Right(Status.Closed)
      case "hold" => Right(Status.Hold)
      case "solved" => Right(Status.Solved)
      case other      => Left(s"'${other}' is not a valid status")
    }

  implicit val decodeChannel: Decoder[Channel] =
    Decoder.decodeString.emap {
      case "web" => Right(Channel.Web)
      case "chat" => Right(Channel.Chat)
      case "voice" => Right(Channel.Voice)
      case other      => Left(s"'${other}' is not a valid channel")
    }

  implicit val decodeUser: Decoder[User] = Decoder.forProduct19(
    "_id",
    "url",
    "external_id",
    "name",
    "alias",
    "created_at",
    "active",
    "verified",
    "shared",
    "locale",
    "timezone",
    "last_login_at",
    "email",
    "phone",
    "signature",
    "organization_id",
    "tags",
    "suspended",
    "role")(User.apply)

  implicit val decodeTicket: Decoder[Ticket] = Decoder.forProduct16(
    "_id",
    "url",
    "external_id",
    "created_at",
    "type",
    "subject",
    "description",
    "priority",
    "status",
    "submitter_id",
    "assignee_id",
    "organization_id",
    "tags",
    "has_incidents",
    "due_at",
    "via"
  )(Ticket.apply)

  private def filter[A](d: Decoder[A], p: A => Boolean, errorMessage: A => String) =
    d.emap { a =>
      if (p(a))
        Right(a)
      else
        Left(errorMessage(a))
    }
}
