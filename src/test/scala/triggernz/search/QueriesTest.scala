package triggernz.search

import utest._

import Data._

object QueriesTest extends TestSuite {
  override def tests = Tests {
    'organizations - {
      'id - {
        val store = VectorStore(organizations, Queries.Organizations.id)
        store.lookupAndRetrieveMany(Vector("101", "102", "103")) ==> organizations.take(3)
      }

      'name - {
        val store = VectorStore(organizations, Queries.Organizations.name)
        store.lookupAndRetrieveMany(Vector("Enthaze", "Qualitern")) ==>
          Vector(organizations(0), organizations(5))
      }

      'tags - {
        val store = VectorStore(organizations, Queries.Organizations.tags)
        store.lookupAndRetrieveMany(Vector("Fulton", "Frank")) ==>
          Vector(organizations(0), organizations(24))
      }
    }

    'users - {
      'nameOrAlias - {
        val store = VectorStore(users, Queries.Users.name, Queries.Users.alias)

        store.lookupAndRetrieveMany(Vector("Francisca Rasmussen")) ==> Vector(users(0))
        store.lookupAndRetrieveMany(Vector("Miss Coffey")) ==> Vector(users(0))
        store.lookupAndRetrieveMany(Vector("Francisca Rasmussen", "Miss Coffey")) ==> Vector(users(0))
      }

      'organizationName {
        val orgStore = VectorStore(organizations, Queries.Organizations.id)
        val store = VectorStore(users, Queries.Users.orgName(orgStore))
        store.lookupAndRetrieveMany(Vector("Terrasys")).length ==> 5
      }
    }

    'tickets - {
      'subjectOrTags - {
        val store = VectorStore(tickets, Queries.Tickets.subject, Queries.Tickets.tags)

        store.lookupAndRetrieveMany(Vector("A Catastrophe in Hungary")) ==> Vector(tickets(2))
        val result = store.lookupAndRetrieveMany(Vector("hawaii")).length ==> 14

      }
    }
  }

}
