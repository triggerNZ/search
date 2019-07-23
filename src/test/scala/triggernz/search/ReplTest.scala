package triggernz.search

import utest._

object ReplTest extends TestSuite {
  override def tests = Tests {
    'partialQuery - {
      'parse - {
        'full - {
          PartialQuery.parse("organizations.byName:SuperOrg") ==>
            Right(PartialQuery.DatasetIndexAndQuery("organizations", "byName", "SuperOrg"))

          PartialQuery.parse("users.byName:Tin Pavlinic") ==>
            Right(PartialQuery.DatasetIndexAndQuery("users", "byName", "Tin Pavlinic"))

          PartialQuery.parse("users.byName:") ==>
            Right(PartialQuery.DatasetIndexAndQuery("users", "byName", ""))
        }

        'datasetAndIdx - {
          PartialQuery.parse("organizations.byName") ==>
            Right(PartialQuery.DatasetAndIndex("organizations", "byName"))
          PartialQuery.parse("organizations.") ==>
            Right(PartialQuery.DatasetAndIndex("organizations", ""))
        }

        'justDataset - {
          PartialQuery.parse("organizations") ==>
            Right(PartialQuery.JustDataset("organizations"))

          PartialQuery.parse("org") ==>
            Right(PartialQuery.JustDataset("org"))

          PartialQuery.parse("") ==>
            Right(PartialQuery.JustDataset(""))
        }
      }
    }
  }

}
