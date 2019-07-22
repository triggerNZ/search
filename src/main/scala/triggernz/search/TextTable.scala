package triggernz.search

import triggernz.search.TextTable._

case class TextTable[T](columns: Vector[Column[T]]) {
  private def fit(str: String, width: Int): String =
    if (str.length > width)
      str.substring(0, width)
    else
      str + (" " * (width - str.length))

  val border: String = "+" + columns.map("-" * _.width).mkString("+") + "+"

  val header: String = {
    val titles = "|" + columns.map(col => fit(col.name, col.width)).mkString("|") + "|"
    List(border, titles, border).mkString("\n")
  }

  def row(t: T): String =
    "|" + columns.map(col => fit(col.value(t), col.width)).mkString("|") + "|"

  def table(ts: Vector[T]) = (header +: ts.map(row) :+ border).mkString("\n")
}

object TextTable {
  def apply[T](columns: Column[T]*): TextTable[T] =
    TextTable(columns.toVector)

  case class Column[T](name: String, width: Int, value: T => String)
}
