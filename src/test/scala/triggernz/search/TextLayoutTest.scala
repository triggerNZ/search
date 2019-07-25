package triggernz.search

import utest._
import TextLayout._
object TextLayoutTest extends TestSuite {
  def tests = Tests {
    'org - {
      layoutOrganization(Data.organizations.head).mkString("\n") ==>
        """Id: 101
          |URL: http://initech.zendesk.com/api/v2/organizations/101.json
          |External Id: 9270ed79-35eb-4a38-a46f-35725197ea8d
          |Name: Enthaze
          |Details: MegaCorp
          |Domain Names: kage.com, ecratic.com, endipin.com, zentix.com
          |Tags: Fulton, West, Rodriguez, Farley
          |Created At: Sat, 21 May 2016 11:10:28 -1000
          |Shared Tickets: N""".stripMargin
    }

    'user - {
      'withOrg - {
        val result = layoutUser(Data.users.head, Some(Data.organizations(18)))
        val expected =
          """Id: 1
            |URL: http://initech.zendesk.com/api/v2/users/1.json
            |External Id: 74341f74-9c79-49d5-9611-87ef9b6eb75f
            |Name: Francisca Rasmussen
            |Alias: Miss Coffey
            |Created At: Fri, 15 Apr 2016 05:19:46 -1000
            |Last Login At: Sun, 4 Aug 2013 01:03:27 -1000
            |Organization:
            |    Id: 119
            |    URL: http://initech.zendesk.com/api/v2/organizations/119.json
            |    External Id: 2386db7c-5056-49c9-8dc4-46775e464cb7
            |    Name: Multron
            |    Details: Non profit
            |    Domain Names: bleeko.com, pulze.com, xoggle.com, sultraxin.com
            |    Tags: Erickson, Mccoy, Wiggins, Brooks
            |    Created At: Mon, 29 Feb 2016 03:45:12 -1100
            |    Shared Tickets: N
            |Timezone: Sri Lanka
            |Role: Admin
            |Active: Y
            |Suspended: Y
            |Shared: N
            |Phone: 8335-422-718
            |Email: coffeyrasmussen@flotonic.com
            |Signature: Don't Worry Be Happy!
            |Locale: en-au
            |Timezone: Sri Lanka
            |Tags: Springville, Sutton, Hartsville/Hartley, Diaperville""".stripMargin
        result.mkString("\n") ==> expected
      }
      'noOrg - {
        val result = layoutUser(Data.users.head, None)
        val expected =
          """Id: 1
            |URL: http://initech.zendesk.com/api/v2/users/1.json
            |External Id: 74341f74-9c79-49d5-9611-87ef9b6eb75f
            |Name: Francisca Rasmussen
            |Alias: Miss Coffey
            |Created At: Fri, 15 Apr 2016 05:19:46 -1000
            |Last Login At: Sun, 4 Aug 2013 01:03:27 -1000
            |Organization:
            |Timezone: Sri Lanka
            |Role: Admin
            |Active: Y
            |Suspended: Y
            |Shared: N
            |Phone: 8335-422-718
            |Email: coffeyrasmussen@flotonic.com
            |Signature: Don't Worry Be Happy!
            |Locale: en-au
            |Timezone: Sri Lanka
            |Tags: Springville, Sutton, Hartsville/Hartley, Diaperville""".stripMargin
        result.zip(expected.split("\n")).foreach {
          case (r, e) => r ==> e
        }
      }

    }

    'ticket - {
      'withEverything - {
        val result = layoutTicket(
          Data.tickets.head,
          Some(Data.organizations(15)),
          Some(Data.users(23)),
          Some(Data.organizations(9)),
          Data.users(37),
          Some(Data.organizations(13))
        )

        val expected =
          """Id: 436bf9b0-1147-4c0a-8439-6f79833bff5b
            |URL: http://initech.zendesk.com/api/v2/tickets/436bf9b0-1147-4c0a-8439-6f79833bff5b.json
            |External Id: 9210cdc9-4bee-485f-a078-35396cd74063
            |Type: Incident
            |Priority: High
            |Status: Pending
            |Created At: Thu, 28 Apr 2016 11:19:34 -1000
            |Subject: A Catastrophe in Korea (North)
            |Description: Nostrud ad sit velit cupidatat laboris ipsum nisi amet laboris ex exercitation amet et proident. Ipsum fugiat aute dolore tempor nostrud velit ipsum.
            |Submitter:
            |    Id: 38
            |    URL: http://initech.zendesk.com/api/v2/users/38.json
            |    External Id: 72c7ba23-e070-4583-b701-04a038a28b02
            |    Name: Elma Castro
            |    Alias: Mr Georgette
            |    Created At: Sun, 31 Jan 2016 02:46:05 -1100
            |    Last Login At: Thu, 20 Dec 2012 01:48:00 -1100
            |    Organization:
            |        Id: 114
            |        URL: http://initech.zendesk.com/api/v2/organizations/114.json
            |        External Id: 49c97d6a-f1ec-422e-aabe-8a429e81e656
            |        Name: Isotronic
            |        Details: Artisân
            |        Domain Names: gynk.com, goko.com, zilidium.com, accruex.com
            |        Tags: Burton, Dunn, Morton, Maddox
            |        Created At: Tue, 24 May 2016 04:27:35 -1000
            |        Shared Tickets: Y
            |    Timezone: Gibraltar
            |    Role: Agent
            |    Active: N
            |    Suspended: Y
            |    Shared: Y
            |    Phone: 8364-062-708
            |    Email: georgettecastro@flotonic.com
            |    Signature: Don't Worry Be Happy!
            |    Locale: en-au
            |    Timezone: Gibraltar
            |    Tags: Colton, Williamson, Marshall, Charco
            |Assignee:
            |    Id: 24
            |    URL: http://initech.zendesk.com/api/v2/users/24.json
            |    External Id: c01c2b7a-30cd-41d1-98e7-2cdd42d55d84
            |    Name: Harris Côpeland
            |    Alias: Miss Gates
            |    Created At: Wed, 2 Mar 2016 03:35:41 -1100
            |    Last Login At: Sat, 11 May 2013 10:41:04 -1000
            |    Organization:
            |        Id: 110
            |        URL: http://initech.zendesk.com/api/v2/organizations/110.json
            |        External Id: 197f93c0-1729-4c82-9bb0-143e978f06ce
            |        Name: Kindaloo
            |        Details: Non profit
            |        Domain Names: translink.com, netropic.com, earthplex.com, zilencio.com
            |        Tags: Chen, Melton, Stafford, Landry
            |        Created At: Tue, 15 Mar 2016 03:08:47 -1100
            |        Shared Tickets: Y
            |    Timezone: Cameroon
            |    Role: Agent
            |    Active: N
            |    Suspended: N
            |    Shared: N
            |    Phone: 9855-882-406
            |    Email: gatescopeland@flotonic.com
            |    Signature: Don't Worry Be Happy!
            |    Locale: zh-cn
            |    Timezone: Cameroon
            |    Tags: Kieler, Swartzville, Salvo, Guthrie
            |Organization:
            |    Id: 116
            |    URL: http://initech.zendesk.com/api/v2/organizations/116.json
            |    External Id: dbc692fc-e1ae-47d8-a1d7-263d07710fe1
            |    Name: Zentry
            |    Details: Artisan
            |    Domain Names: datagene.com, exoteric.com, beadzza.com, digiprint.com
            |    Tags: Schneider, Hoover, Wilcox, Hewitt
            |    Created At: Wed, 13 Jan 2016 09:34:07 -1100
            |    Shared Tickets: N
            |Tags: Ohio, Pennsylvania, American Samoa, Northern Mariana Islands
            |Has Incidents: N
            |Due At: Sun, 31 Jul 2016 02:37:50 -1000
            |Via: Web""".stripMargin

        result.zip(expected.split("\n")).foreach {
          case (r, e) => r ==> e
        }
      }

      'minimal - {
       val result = layoutTicket(
          Data.tickets.head,
          None,
          None,
          None,
          Data.users(37),
          None)

         val expected =
            """Id: 436bf9b0-1147-4c0a-8439-6f79833bff5b
              |URL: http://initech.zendesk.com/api/v2/tickets/436bf9b0-1147-4c0a-8439-6f79833bff5b.json
              |External Id: 9210cdc9-4bee-485f-a078-35396cd74063
              |Type: Incident
              |Priority: High
              |Status: Pending
              |Created At: Thu, 28 Apr 2016 11:19:34 -1000
              |Subject: A Catastrophe in Korea (North)
              |Description: Nostrud ad sit velit cupidatat laboris ipsum nisi amet laboris ex exercitation amet et proident. Ipsum fugiat aute dolore tempor nostrud velit ipsum.
              |Submitter:
              |    Id: 38
              |    URL: http://initech.zendesk.com/api/v2/users/38.json
              |    External Id: 72c7ba23-e070-4583-b701-04a038a28b02
              |    Name: Elma Castro
              |    Alias: Mr Georgette
              |    Created At: Sun, 31 Jan 2016 02:46:05 -1100
              |    Last Login At: Thu, 20 Dec 2012 01:48:00 -1100
              |    Organization:
              |    Timezone: Gibraltar
              |    Role: Agent
              |    Active: N
              |    Suspended: Y
              |    Shared: Y
              |    Phone: 8364-062-708
              |    Email: georgettecastro@flotonic.com
              |    Signature: Don't Worry Be Happy!
              |    Locale: en-au
              |    Timezone: Gibraltar
              |    Tags: Colton, Williamson, Marshall, Charco
              |Assignee:
              |Organization:
              |Tags: Ohio, Pennsylvania, American Samoa, Northern Mariana Islands
              |Has Incidents: N
              |Due At: Sun, 31 Jul 2016 02:37:50 -1000
              |Via: Web""".stripMargin

        expected.split("\n").zip(result) foreach {
          case (e, r) => r ==> e
        }

      }
    }
  }
}
