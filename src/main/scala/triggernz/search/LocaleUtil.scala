package triggernz.search

import java.util.Locale

object LocaleUtil {
  lazy val allLocales: Set[String] =
    (Set.newBuilder[String]
      ++= Locale.getAvailableLocales().toIterator
      .map(_.toString.toLowerCase))
      .result()

  def isValid(locale: Locale) = {
    val normalisedLocale = locale.toString.toLowerCase.replace('-', '_')
    allLocales.contains(normalisedLocale)
  }

}
