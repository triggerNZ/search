package triggernz.search

import java.util.Locale

import utest._

object LocaleUtilTest extends TestSuite {
  override def tests = Tests {
    'enAuIsValid - {
      LocaleUtil.isValid(new Locale("en-au")) ==> true
    }
  }

}
