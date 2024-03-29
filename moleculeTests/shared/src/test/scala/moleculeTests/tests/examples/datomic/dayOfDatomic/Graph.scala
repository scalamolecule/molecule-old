package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out4._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._

// See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html

object Graph extends AsyncTestSuite {

  lazy val tests = Tests {

    "Simple hyperedge" - graph { implicit conn =>
      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.Graph._
      for {
        List(r1, r2) <- Role.name insert List("Role1", "Role2") map(_.eids)

        // Each Group has 2 Roles
        List(g1, g2) <- Group.name.roles insert List(
          ("Group1", Set(r1, r2)),
          ("Group2", Set(r1, r2))) map(_.eids)

        // User with Roles in Groups
        _ <- User.name.RoleInGroup.name.group.role insert List(
          ("User1", "U1G1R2", g1, r2),
          ("User1", "U1G2R1", g2, r1))

        // (or we could have made a nested insert)
        // (User.name.RoleInGroup * RoleInGroup.name.group.role) insert(
        //   "User1", List(("U1G1R2", g1, r2), ("U1G2R1", g2, r1)))


        // User 1 ..........................

        // User 1's Roles
        _ <- User.name_("User1").RoleInGroup.Role.name.get.map(_ ==> List("Role2", "Role1"))

        // User 1's Groups
        _ <- User.name_("User1").RoleInGroup.Group.name.get.map(_ ==> List("Group2", "Group1"))

        // User 1 Roles in Group 2
        _ <- User.name_("User1")
          .RoleInGroup.Group.name_("Group2")
          ._RoleInGroup.Role.name.get.map(_ ==> List("Role1"))
        /* Query in Neo4J:
        MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->({ name: 'Group2' }),
          (hyperEdge)-[:hasRole]->(role)
        RETURN role.name
        */

        // User 1's Groups having Role 1
        _ <- User.name_("User1")
          .RoleInGroup.Role.name_("Role1")
          ._RoleInGroup.Group.name.get.map(_ ==> List("Group2"))


        // Role 1 ............................

        // Users having Role 1
        _ <- User.name.RoleInGroup.Role.name_("Role1").get.map(_ ==> List("User1"))

        // Groups with Role 1
        _ <- RoleInGroup.Role.name_("Role1")
          ._RoleInGroup.Group.name.get.map(_ ==> List("Group2"))

        // Users in Group 2 having Role 1
        _ <- User.name
          .RoleInGroup.Group.name_("Group2")
          ._RoleInGroup.Role.name_("Role1").get.map(_ ==> List("User1"))

        // Groups where User 1 has Role 1
        _ <- User.name_("User1")
          .RoleInGroup.Group.name
          ._RoleInGroup.Role.name_("Role1").get.map(_ ==> List("Group2"))


        // Group 2 ...........................

        // Users in Group 2
        _ <- User.name.RoleInGroup.Group.name_("Group2").get.map(_ ==> List("User1"))

        // Roles of Group 2
        _ <- RoleInGroup.Group.name_("Group2")
          ._RoleInGroup.Role.name.get.map(_ ==> List("Role1"))

        // Users with Role 1 in Group 2
        _ <- User.name
          .RoleInGroup.Role.name_("Role1")
          ._RoleInGroup.Group.name_("Group2").get.map(_ ==> List("User1"))

        // Roles of User 1 in Group 2
        _ <- User.name_("User1")
          .RoleInGroup.Group.name_("Group2")
          ._RoleInGroup.Role.name.get.map(_ ==> List("Role1"))


        // All groups and the roles a user has, sorted by the name of the role
        _ <- User.name_("User1")
          .RoleInGroup.Role.name.a1
          ._RoleInGroup.Group.name.a2.get.map(_ ==> List(
          ("Role1", "Group2"),
          ("Role2", "Group1")
        ))
        /* Query in Neo4J:
        MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->(group),
          (hyperEdge)-[:hasRole]->(role)
        RETURN role.name, group.name
        ORDER BY role.name ASC
        */
      } yield ()
    }


    // Load graph 2 where RoleInGroup references multiple Roles
    "Advanced hyperedge" - graph2 { implicit conn =>
      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.Graph2._
      for {
        List(r1, r2, r3, r4, r5, r6) <-
          Role.name insert List("Role1", "Role2", "Role3", "Role4", "Role5", "Role6") map(_.eids)
        List(g1, g2, g3) <- Group.name.roles insert List(
          ("Group1", Set(r1, r2, r5)),
          ("Group2", Set(r2, r3, r4)),
          ("Group3", Set(r3, r4, r5, r6))
        ) map(_.eids)

        // Users with various Roles in various Groups
        _ <- User.name.RoleInGroup.name.group.roles insert List(
          ("User1", "U1G1R12", g1, Set(r1, r2)),
          ("User1", "U1G2R23", g2, Set(r2, r3)),
          ("User1", "U1G3R34", g3, Set(r3, r4)),
          ("User2", "U2G1R25", g1, Set(r2, r5)),
          ("User2", "U2G2R34", g2, Set(r3, r4)),
          ("User2", "U2G3R56", g3, Set(r5, r6)))

        // Users and their Roles in Groups
        _ <- User.name.a1
          .RoleInGroup.Roles.name.a2
          ._RoleInGroup.Group.name.a3.get.map(_ ==> List(
          ("User1", "Role1", "Group1"),
          ("User1", "Role2", "Group1"), // Sharing Role2 in Group 1
          ("User1", "Role2", "Group2"),
          ("User1", "Role3", "Group2"), // Sharing Role3 in Group 2
          ("User1", "Role3", "Group3"),
          ("User1", "Role4", "Group3"),

          ("User2", "Role2", "Group1"), // Sharing Role2 in Group 1
          ("User2", "Role3", "Group2"), // Sharing Role3 in Group 2
          ("User2", "Role4", "Group2"),
          ("User2", "Role5", "Group1"),
          ("User2", "Role5", "Group3"),
          ("User2", "Role6", "Group3")))

        // Groups where User1 and User2 share a role

        // Since we unify on both Group name and Role name we can use the AND notation:
        _ <- User.name_("User1" and "User2").RoleInGroup.Group.name.a1._RoleInGroup.Roles.name.a2.get.map(_ ==> List(
          ("Group1", "Role2"),
          ("Group2", "Role3")))

        // .. or we could use the full SelfJoin notation
        _ <- User.name_("User1").RoleInGroup.Group.name.a1._RoleInGroup.Roles.name.a2._RoleInGroup._User.Self
          .name_("User2").RoleInGroup.Group.name_(unify)._RoleInGroup.Roles.name_(unify).get.map(_ ==> List(
          ("Group1", "Role2"),
          ("Group2", "Role3")))


        // If we are only interest in how many roles they have in common we can do this:

        _ <- User.name_("User1" and "User2").RoleInGroup.Group.name._RoleInGroup.roles(count).get.map(_ ==> List(
          ("Group1", 1),
          ("Group2", 1)))
        // .. or
        _ <- User.name_("User1").RoleInGroup.Group.name.a1._RoleInGroup.roles(count)._User.Self
          .name_("User2").RoleInGroup.Group.name_(unify)._RoleInGroup.roles_(unify).get.map(_ ==> List(
          ("Group1", 1),
          ("Group2", 1)))

        // The last full SelfJoin notation is quite similar to Neo4J's notation:

        /* Query in Neo4J:
        MATCH
          (u1)-[:hasRoleInGroup]->(hyperEdge1)-[:hasGroup]->(group), (hyperEdge1)-[:hasRole]->(role),
          (u2)-[:hasRoleInGroup]->(hyperEdge2)-[:hasGroup]->(group), (hyperEdge2)-[:hasRole]->(role)
        WHERE u1.name = 'User1' AND u2.name = 'User2'
        RETURN group.name, count(role)
        ORDER BY group.name ASC
        */
      } yield ()
    }
  }
}