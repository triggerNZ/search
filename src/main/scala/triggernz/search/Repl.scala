package triggernz.search

import java.util

import cats.{Foldable, Id}
import cats.instances.vector._
import cats.instances.string._
import org.jline.reader._

case class Repl(
                 orgStores: Map[String, Store[String, Organization]],
                 userStores: Map[String, Store[String, User]],
                 ticketStores: Map[String, Store[String, Ticket]],
                 userIdStore: Store[UserId, User],
                 orgIdStore: Store[OrganizationId, Organization],
               ) {
  object HardcodedDatasets {
    val Tickets = "tickets"
    val Users = "users"
    val Organizations = "organizations"
    val All = List(Tickets, Users, Organizations)

  }

  def completions(partialQuery: PartialQuery): List[String] = partialQuery match {
    case PartialQuery.JustDataset(ds) =>
      HardcodedDatasets.All.filter(_.startsWith(ds))

    case PartialQuery.DatasetAndIndex(ds, idx) =>
      ds match {
        case HardcodedDatasets.Tickets =>
          ticketStores.keys.toList.filter(_.startsWith(idx)).map(ds + "." + _)
        case HardcodedDatasets.Organizations =>
          orgStores.keys.toList.filter(_.startsWith(idx)).map(ds + "." + _)
        case HardcodedDatasets.Users =>
          userStores.keys.toList.filter(_.startsWith(idx)).map(ds + "." + _)
      }

    case PartialQuery.DatasetIndexAndQuery(_, _, q) => Nil //Queries are freeform, nothing to complete
  }

  object ReplCompleter extends Completer {
    override def complete(reader: LineReader, line: ParsedLine, candidates: util.List[Candidate]): Unit = {
      PartialQuery.parse(line.line()) match {
        case Right(result) =>
          completions(result).foreach(s => candidates.add(new Candidate(s, s, null, null, null, null, false)))
        case Left(_) => ()
      }
    }
  }

  def outputResult[R](lineReader: LineReader, results: Vector[R], single: R => List[String]): Unit = {
    val separator = "\n" + ("-" * lineReader.getTerminal.getWidth)

    val wholeOutput = Foldable[Vector].intercalate(results.map(r => single(r).mkString("\n")), separator)
    println(wholeOutput)
  }

  def start(): Unit = {
    val reader =
      LineReaderBuilder.builder()
      .completer(ReplCompleter)
        .appName("search")
        .build()

    println("Welcome. Use tab completion to explore datasets or type help.")

    while(true) {
      try {
        val line = reader.readLine("> ")
        if (line == "help") {
          printHelp()
        } else {
          val parsedLine = PartialQuery.parse(line)
          parsedLine match {
            case Right(PartialQuery.DatasetIndexAndQuery(HardcodedDatasets.Users, index, query)) =>
              userStores.get(index) match {
                case None => println(s"No index named ${index} for dataset ${HardcodedDatasets.Users}")
                case Some(store) =>
                  val results = store.lookupAndRetrieve(query)
                  val usersWithOrgs = results.map { user =>
                    val organization = user.organizationId.flatMap(id => orgIdStore.lookupAndRetrieve(id).headOption)
                    (user, organization)
                  }
                  outputResult(reader, usersWithOrgs, (TextLayout.layoutUser _).tupled)

              }
            case Right(PartialQuery.DatasetIndexAndQuery(HardcodedDatasets.Organizations, index, query)) =>
              orgStores.get(index) match {
                case None => println(s"No index named ${index} for dataset ${HardcodedDatasets.Organizations}")
                case Some(store) =>
                  val results = store.lookupAndRetrieve(query)
                  outputResult(reader, results, TextLayout.layoutOrganization)
              }

            case Right(PartialQuery.DatasetIndexAndQuery(HardcodedDatasets.Tickets, index, query)) =>
              ticketStores.get(index) match {
                case None => println(s"No index named ${index} for dataset ${HardcodedDatasets.Tickets}")
                case Some(store) =>
                  val results = store.lookupAndRetrieve(query)
                  val ticketWithEverything = results.map { ticket =>
                    val organization = ticket.organizationId.flatMap(id => orgIdStore.lookupAndRetrieve(id).headOption)
                    val assignee = ticket.assigneeId.flatMap(u => userIdStore.lookupAndRetrieve(u).headOption)
                    val assigneeOrg = assignee.flatMap(_.organizationId).flatMap(id => orgIdStore.lookupAndRetrieve(id).headOption)
                    val submitter = userIdStore.lookupAndRetrieve(ticket.submitterId).headOption
                    val submitterOrg = submitter.flatMap(_.organizationId).flatMap(id => orgIdStore.lookupAndRetrieve(id).headOption)
                    (ticket, organization, assignee, assigneeOrg, submitter, submitterOrg)
                  }
                  outputResult(reader, ticketWithEverything, (TextLayout.layoutTicket _).tupled)

              }

            case _ =>
              println("Invalid query")

          }
        }
      } catch {
        case _: EndOfFileException => return
        case _: UserInterruptException => return
      }
    }
  }

  def printHelp(): Unit = {
    val message =
      s"""
        |Query format: <dataset>.<index>:<query>
        |
        |Where dataset is one of organizations, users, tickets, and the following are the indexes per each dataset.
        |
        |organizations:
        |${orgStores.keys.map(" - " + _).mkString("\n")}
        |
        |users:
        |${userStores.keys.map(" - " + _).mkString("\n")}

        |tickets:
        |${ticketStores.keys.map(" - " + _).mkString("\n")}
        |""".stripMargin

    println(message)
  }
}

object Repl {
}

sealed trait PartialQuery
object PartialQuery {
  case class JustDataset(dataset: String) extends PartialQuery
  case class DatasetAndIndex(dataset: String, index: String) extends PartialQuery
  case class DatasetIndexAndQuery(dataset: String, index: String, query: String) extends PartialQuery


  sealed trait ParseError
  object ParseError {
    case class NotAQuery(name: String) extends ParseError
  }

  val FullRegex = "(\\w*)\\.(\\w*):(.*)".r
  val DsAndQRegex = "(\\w*)\\.(\\w*)".r
  val DsRegex = "(\\w*)".r
  def parse(line: String): Either[ParseError, PartialQuery] = {
    line match {
      case FullRegex(dataset, index, query) => Right(DatasetIndexAndQuery(dataset, index, query))
      case DsAndQRegex(dataset, index) => Right(DatasetAndIndex(dataset, index))
      case DsRegex(dataset) => Right(JustDataset(dataset))
      case other => Left(ParseError.NotAQuery(other))

    }
  }
}
