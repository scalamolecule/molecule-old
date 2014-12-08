package molecule
package examples.dayOfDatomic.tutorial
import molecule.examples.dayOfDatomic.schema.{Graph2Schema, GraphSchema}
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec


class Graph extends DayOfAtomicSpec {

  // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html

  "Simple hyperedge" >> {

    import molecule.examples.dayOfDatomic.dsl.graph._

    implicit val conn = load(GraphSchema.tx, "Graph")

    val List(r1, r2) = Role.name insert List("Role1", "Role2") ids

    // Each Group has 2 Roles
    val List(g1, g2) = Group.name.roles insert List(("Group1", Set(r1, r2)), ("Group2", Set(r1, r2))) ids

    // User with Roles in Groups
    User.name.RoleInGroup(RoleInGroup.name.group.role) insert("User1", List(("U1G1R2", g1, r2), ("U1G2R1", g2, r1)))


    // User 1 ..........................

    // User 1's Roles
    User.name_("User1").RoleInGroup.Role.name.get === List("Role1", "Role2")

    // User 1's Groups
    User.name_("User1").RoleInGroup.Group.name.get === List("Group2", "Group1")

    // User 1's Roles in Group 2
    User.name_("User1").RoleInGroup(RoleInGroup.Group.name_("Group2")).Role.name.get === List("Role1")
    /* Query in Neo4J:
    MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->({ name: 'Group2' }),
      (hyperEdge)-[:hasRole]->(role)
    RETURN role.name
    */

    // User 1's Groups having Role 1
    User.name_("User1").RoleInGroup(RoleInGroup.Role.name_("Role1")).Group.name.get === List("Group2")


    // Role 1 ............................

    // Users having Role 1
    User.name.RoleInGroup.Role.name_("Role1").get === List("User1")

    // Groups with Role 1
    RoleInGroup.apply(RoleInGroup.Role.name_("Role1")).Group.name.get === List("Group2")

    // Users in Group 2 having Role 1
    User.name.RoleInGroup(RoleInGroup.Group.name_("Group2")).Role.name_("Role1").get === List("User1")

    // Groups where User 1 has Role 1
    User.name_("User1").RoleInGroup(RoleInGroup.Group.name).Role.name_("Role1").get === List("Group2")


    // Group 2 ...........................

    // Users in Group 2
    User.name.RoleInGroup.Group.name_("Group2").get === List("User1")

    // Roles of Group 2
    RoleInGroup(RoleInGroup.Group.name_("Group2")).Role.name.get === List("Role1")

    // Users with Role 1 in Group 2
    User.name.RoleInGroup(RoleInGroup.Role.name_("Role1")).Group.name_("Group2").get === List("User1")

    // Roles of User 1 in Group 2
    User.name_("User1").RoleInGroup(RoleInGroup.Group.name_("Group2")).Role.name.get === List("Role1")


    // All groups and the roles a user has, sorted by the name of the role
    User.name_("User1").RoleInGroup(RoleInGroup.Role.name).Group.name.get.sortBy(_._1) === List(
      ("Role1", "Group2"),
      ("Role2", "Group1")
    )
    /* Query in Neo4J:
    MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->(group),
      (hyperEdge)-[:hasRole]->(role)
    RETURN role.name, group.name
    ORDER BY role.name ASC
    */
  }


  "Advanced hyperedge" >> {

    import molecule.examples.dayOfDatomic.dsl.graph2._

    // Load graph 2 where RoleInGroup references multiple Roles
    implicit val conn = load(Graph2Schema.tx, "Graph")

    val List(r1, r2, r3, r4, r5, r6) = Role.name insert List("Role1", "Role2", "Role3", "Role4", "Role5", "Role6") ids
    val List(g1, g2, g3) = Group.name insert List("Group1", "Group2", "Group3") ids

    // Users with various Roles in various Groups
    User.name.RoleInGroup(RoleInGroup.name.group.roles) insert List(
      ("User1", List(
        ("U1G1R12", g1, Set(r1, r2)),
        ("U1G2R23", g2, Set(r2, r3)),
        ("U1G3R34", g3, Set(r3, r4)))),
      ("User2", List(
        ("U2G1R25", g1, Set(r2, r5)),
        ("U2G2R34", g2, Set(r3, r4)),
        ("U2G3R56", g3, Set(r5, r6))))
    )




    // Groups where User1 and User2 share at least one common role


    /* Query in Neo4J:
    MATCH (u1)-[:hasRoleInGroup]->(hyperEdge1)-[:hasGroup]->(group),(hyperEdge1)-[:hasRole]->(role),
      (u2)-[:hasRoleInGroup]->(hyperEdge2)-[:hasGroup]->(group),(hyperEdge2)-[:hasRole]->(role)
    WHERE u1.name = 'User1' AND u2.name = 'User2'
    RETURN group.name, count(role)
    ORDER BY group.name ASC
    */

    ok
  }
}

/*

Playing around with graph notation...


RoleInGroup -- User1
      |
      |
    Group2 -- Role.name?


    User
     :
RoleInGroup —— Group
     |
    Role



   User
    |
RoleInGroup
 |      |
 |    Group
Role


User
|
RoleGroup
—— Role
—— Group



User
|
RoleInGroup —— Role
            —— Group


   User
    |
RoleInGroup
 |      |
Role  Group



              User
            /
RoleInGroup
 |      |
Role  Group



User.name_("User1")
 |
Role.name
 In
Group.name_("Group2")




 With
Tasks



  User.name_("User1")
   |
 -----
|Role |.name
| In  |
|Group|.name_("Group2")
 -----


User
 |
Role  —— Role
 In
Group —— Group




   User1
    |
RoleInGroup
 |       |
Role?  Group2



            User
             |
            RoleInGroup —— Group
             |
            Role


     User
      |
RoleInGroup —— Group2
      |
    Role.name?


    import datomic._
    Peer.q( s"""
           [:find ?g
                  :where
                    [?a :user/name "User1"]
                    [?a :user/roleInGroup ?c]
                    [?c :roleInGroup/group ?d]
                    [?d :group/name "Group2"]
                    [?c :roleInGroup/role ?f]
                    [?f :role/name ?g]]
           """, conn.db) === 77

    Peer.q( s"""
           [:find ?g
                  :where
                    [?a :user/name "User1"]
                    [?a :user/roleInGroup ?c]
                    [?c :roleInGroup/group ?d]
                    [?d :group/name "Group2"]
                    [?d :roleInGroup/role ?f]
                    [?f :role/name ?g]]
           """, conn.db) === 77

*/