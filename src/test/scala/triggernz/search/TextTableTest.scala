package triggernz.search

import triggernz.search.TextTable.Column
import utest._

object TextTableTest extends TestSuite {
  case class Demo(apple: String, banana: Int)
  val table = TextTable[Demo](Vector(
    Column[Demo]("apple", 8, _.apple),
    Column[Demo]("banana", 6, _.banana.toString)))

  override def tests = Tests {

    'header - {
      val expectedHeader =
        """+--------+------+
        !|apple   |banana|
        !+--------+------+""".stripMargin('!')


      table.header ==> expectedHeader
    }

    'row - {
      table.row(Demo("red", 321)) ==> "|red     |321   |"
    }

    'table - {
      val expectedTable =
        """+--------+------+
        !|apple   |banana|
        !+--------+------+
        !|Too long|3     |
        !|Normal  |4444  |
        !|red     |321   |
        !+--------+------+""".stripMargin('!')

      table.table(
        Vector(
          Demo("Too long a string", 3),
          Demo("Normal", 4444),
          Demo("red", 321)
        )) ==> expectedTable

    }

    'width - {
      table.width ==> 17
    }

    'widthOfActualTablesIsReadable {
      import Tables._
      List(organizations , users, tickets).foreach { table =>
        // < 180 means we can fit more. > 200 is too wide
        assert(table.width > 180 && table.width < 200)
      }
    }
  }
}
