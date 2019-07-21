package triggernz.search

import io.circe.Json
import utest._

class EmailTest extends TestSuite {
  override def tests = Tests {
    'validation - {
      'correct - { Email.fromString("mike@gmail.com") ==> Some(Email("mike", "gmail.com")) }
      'noUser - { Email.fromString("@gmail.com") ==> None }
      'noDomain - { Email.fromString("mike@") ==> None }
      'noUserOrDomain - { Email.fromString("@") ==> None }
      'noAt - { Email.fromString("mike") ==> None }
      'justHost - { Email.fromString("gmail.com")  ==> None }
    }
  }

}
