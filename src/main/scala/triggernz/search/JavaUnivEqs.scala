package triggernz.search

import java.util.{Locale, UUID}

import japgolly.univeq.UnivEq

object JavaUnivEqs {
  implicit def urlUnivEq: UnivEq[java.net.URL] =
    UnivEq.force

  implicit def uuidUnivEq: UnivEq[UUID] =
    UnivEq.force

  implicit def localeUnivEq: UnivEq[Locale] =
    UnivEq.force

}
