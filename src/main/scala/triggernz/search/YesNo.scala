package triggernz.search

object YesNo {
  def apply(b: Boolean): String = b match {
    case true => "Y"
    case false => "N"
  }
}
