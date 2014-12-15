package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.schema.{Graph2Schema, GraphSchema}
import org.specs2.mutable.Specification


class Graph extends Specification with DatomicFacade {

  // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html

  "Simple hyperedge" >> {

    import molecule.examples.dayOfDatomic.dsl.graph._

    implicit val conn = load(GraphSchema.tx, "Graph")

    val List(r1, r2) = Role.name insert List("Role1", "Role2") ids

    // Each Group has 2 Roles
    val List(g1, g2) = Group.name.roles insert List(
      ("Group1", Set(r1, r2)),
      ("Group2", Set(r1, r2))) ids

    // User with Roles in Groups
    User.name.RoleInGroup.name.group.role insert List(
      ("User1", "U1G1R2", g1, r2),
      ("User1", "U1G2R1", g2, r1))

    // (or we could have made a nested insert)
    // (User.name.RoleInGroup * RoleInGroup.name.group.role) insert(
    //   "User1", List(("U1G1R2", g1, r2), ("U1G2R1", g2, r1)))


    // User 1 ..........................

    // User 1's Roles
    User.name_("User1").RoleInGroup.Role.name.get === List("Role1", "Role2")

    // User 1's Groups
    User.name_("User1").RoleInGroup.Group.name.get === List("Group2", "Group1")

    // User 1 Roles in Group 2
    User.name_("User1").RoleInGroup.Group.name_("Group2")._Role.name.get === List("Role1")
    /* Query in Neo4J:
    MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->({ name: 'Group2' }),
      (hyperEdge)-[:hasRole]->(role)
    RETURN role.name
    */

    // User 1's Groups having Role 1
    User.name_("User1").RoleInGroup.Role.name_("Role1")._Group.name.get === List("Group2")


    // Role 1 ............................

    // Users having Role 1
    User.name.RoleInGroup.Role.name_("Role1").get === List("User1")

    // Groups with Role 1
    RoleInGroup.Role.name_("Role1")._Group.name.get === List("Group2")

    // Users in Group 2 having Role 1
    User.name.RoleInGroup.Group.name_("Group2")._Role.name_("Role1").get === List("User1")

    // Groups where User 1 has Role 1
    User.name_("User1").RoleInGroup.Group.name._Role.name_("Role1").get === List("Group2")


    // Group 2 ...........................

    // Users in Group 2
    User.name.RoleInGroup.Group.name_("Group2").get === List("User1")

    // Roles of Group 2
    RoleInGroup.Group.name_("Group2")._Role.name.get === List("Role1")

    // Users with Role 1 in Group 2
    User.name.RoleInGroup.Role.name_("Role1")._Group.name_("Group2").get === List("User1")

    // Roles of User 1 in Group 2
    User.name_("User1").RoleInGroup.Group.name_("Group2")._Role.name.get === List("Role1")


    // All groups and the roles a user has, sorted by the name of the role
    User.name_("User1").RoleInGroup.Role.name._Group.name.get.sorted === List(
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
    val List(g1, g2, g3) = Group.name.roles insert List(
      ("Group1", Set(r1, r2, r5)),
      ("Group2", Set(r2, r3, r4)),
      ("Group3", Set(r3, r4, r5, r6))
    ) ids

    // Users with various Roles in various Groups
    User.name.RoleInGroup.name.group.roles insert List(
      ("User1", "U1G1R12", g1, Set(r1, r2)),
      ("User1", "U1G2R23", g2, Set(r2, r3)),
      ("User1", "U1G3R34", g3, Set(r3, r4)),
      ("User2", "U2G1R25", g1, Set(r2, r5)),
      ("User2", "U2G2R34", g2, Set(r3, r4)),
      ("User2", "U2G3R56", g3, Set(r5, r6)))

    // Users and their Roles in Groups
    User.name.RoleInGroup.Roles.name._Group.name.get.sorted === List(
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
      ("User2", "Role6", "Group3"))

//    User.name_.apply("User1" and "User2").RoleInGroup.Group.name._Roles.name(count).debug

    import datomic._
    import scala.collection.JavaConversions._

//    Peer.q( s"""
//          [:find ?e (count ?g)
//           :where
//             [?a :user/name "User1"]
//             [?a :user/roleInGroup ?c]
//             [?c :roleInGroup/group ?d]
//             [?d :group/name ?e]
//             [?c :roleInGroup/roles ?f]
//             [?f :role/name ?g]
//
//             [?a_1 :user/name "User1"]
//             [?a_1 :user/roleInGroup ?c_1]
//             [?c_1 :roleInGroup/group ?d_1]
//             [?d_1 :group/name ?e_1]
//             [?c_1 :roleInGroup/roles ?f_1]
//             [?f_1 :role/name ?g_1]
//
//             [(= ?e ?e_1)]
//             [(= ?g ?g_1)]]
//           """, conn.db).toList.mkString("\n") === 4
//
//    Peer.q( s"""
//          [:find ?g1 ?e1 (count ?e1)
//           :where
//             [?a1 :user/name "User1"]
//             [?a1 :user/roleInGroup ?c1]
//             [?c1 :roleInGroup/roles ?d1]
//             [?d1 :role/name ?e1]
//             [?c1 :roleInGroup/group ?f1]
//             [?f1 :group/name ?g1]
//
//             [?a2 :user/name "User2"]
//             [?a2 :user/roleInGroup ?c2]
//             [?c2 :roleInGroup/roles ?d2]
//             [?d2 :role/name ?e2]
//             [?c2 :roleInGroup/group ?f2]
//             [?f2 :group/name ?g2]
//
//             [(= ?e1 ?e2)]
//             [(= ?g1 ?g2)]]
//           """, conn.db).toList.mkString("\n") === 3
//
//    Peer.q( s"""
//          [:find ?g ?e (count ?e)
//           :where
//             [?a :user/name "User1"]
//             [?a :user/roleInGroup ?c]
//             [?c :roleInGroup/roles ?d]
//             [?d :role/name ?e]
//             [?c :roleInGroup/group ?f]
//             [?f :group/name ?g]
//
//             [?xa :user/name "User2"]
//             [?xa :user/roleInGroup ?xc]
//             [?xc :roleInGroup/roles ?xd]
//             [?xd :role/name ?xe]
//             [?xc :roleInGroup/group ?xf]
//             [?xf :group/name ?xg]
//
//             [(= ?d ?xd)]
//             [(= ?f ?xf)]
//
//             ]
//           """, conn.db).toList.mkString("\n") === 2

    ///*
    //
    //             [?xa :user/roleInGroup ?xc]
    //             [?xc :roleInGroup/roles ?xd]
    //             [?xd :role/name ?xe]
    //             [?xc :roleInGroup/group ?xf]
    //             [?xf :group/name ?xg]
    // */
    //
    //    Peer.q( s"""
    //          [:find ?b ?f
    //           :where
    //             [?a :user/name ?b]
    //             [?a :user/roleInGroup ?c]
    //             [?c :roleInGroup/group ?d]
    //             [?d :group/roles ?e]
    //             [?e :role/name ?f]
    //             ]
    //           """, conn.db).toList.mkString("\n") === 1
    //
    //    Peer.q( s"""
    //          [:find ?b ?e ?g
    //           :where
    //             [?a :user/name ?b]
    //             [?a :user/roleInGroup ?c]
    //             [?c :roleInGroup/roles ?d]
    //             [?d :role/name ?e]
    //             [?c :roleInGroup/group ?f]
    //             [?f :group/name ?g]]
    //           """, conn.db) === 77



//        User.name("User1")(User.name("User2")).debug
//
//        User.name.apply("User1").name.apply("User2").RoleInGroup.apply(Role.name).Group.name(count).debug
//
//        User.name.apply(?).name.apply(?).RoleInGroup.Roles.name._Group.name(count) apply ("User1" and "User2")
//
//
//        User.name("User1").RoleInGroup.Roles.name._Group.name(count)
//
//        User.name("User1").name("User2").RoleInGroup.apply(Role.name).Group.name(count).debug
//        RoleInGroup(Group.name).Roles.name(count).debug
//
//        User.name("User1").name("User2").RoleInGroup(Role.name).Group.name(count).debug

    // Common Roles


    // Common Groups


    //    Group.name.roles


    // Groups where User1 and User2 share at least one common role


    /* Query in Neo4J:
    MATCH (u1)-[:hasRoleInGroup]->(hyperEdge1)-[:hasGroup]->(group),(hyperEdge1)-[:hasRole]->(role),
      (u2)-[:hasRoleInGroup]->(hyperEdge2)-[:hasGroup]->(group),(hyperEdge2)-[:hasRole]->(role)
    WHERE u1.name = 'User1' AND u2.name = 'User2'
    RETURN group.name, count(role)
    ORDER BY group.name ASC


    MATCH
      ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->(group),
      (hyperEdge)-[:hasRole]->(role)

    MATCH
      (u1)-[:hasRoleInGroup]->(hyperEdge1)-[:hasGroup]->(group), (hyperEdge1)-[:hasRole]->(role),
      (u2)-[:hasRoleInGroup]->(hyperEdge2)-[:hasGroup]->(group), (hyperEdge2)-[:hasRole]->(role)
    WHERE u1.name = 'User1' AND u2.name = 'User2'
    RETURN group.name, count(role)
    ORDER BY group.name ASC
    */

    ok
  }
}

/*


    //    val List(u1, u2) = User.name.RoleInGroup(RoleInGroup.name.group.roles) insert List(
    //    User.name.RoleInGroup.apply(RoleInGroup.name.group.roles).insert.apply(List())

    (User.name.RoleInGroup * RoleInGroup.name.group.roles) insert List(
      ("User1", List(
        ("U1G1R12", g1, Set(r1, r2)),
        ("U1G2R23", g2, Set(r2, r3)),
        ("U1G3R34", g3, Set(r3, r4)))),
      ("User2", List(
        ("U2G1R25", g1, Set(r2, r5)),
        ("U2G2R34", g2, Set(r3, r4)),
        ("U2G3R56", g3, Set(r5, r6)))))


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



//    User.name_("User1").RoleInGroupx(RoleInGroup.Groupx.name_("Group2")).Rolex.name.debug
    /*
[:find ?g
 :where
   [?a :user/name "User1"]
   [?a :user/roleInGroupx ?c]
   [?c :roleInGroup/groupx ?d]
   [?d :group/name "Group2"]
   [?c :roleInGroup/rolex ?f]
   [?f :role/name ?g]]

Warning:scalac: ##20: Dsl2Model
1          user
------------------------------------------------
2          List(
  1          Bond(roleInGroup,groupx,group)
  2          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
3          Group(
  1          Bond(user,roleInGroupx,roleInGroup)
  2          Bond(roleInGroup,groupx,group)
  3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
4          roleInGroup
------------------------------------------------
5          roleInGroup
------------------------------------------------
6          user
======================================================
Warning:scalac: ##30: Dsl2Model
1          List(
  1          Atom(user,name_,String,1,Eq(List(User1)),None,List())
  2          Group(
    1          Bond(user,roleInGroupx,roleInGroup)
    2          Bond(roleInGroup,groupx,group)
    3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
  3          Bond(roleInGroup,rolex,role)
  4          Atom(role,name,String,1,VarValue,None,List()))
======================================================

     */

//    User.name_("User1").RoleInGroup(RoleInGroup.Group.name_("Group2")).Role.name.debug
    /*
    [:find ?g
     :where
       [?a :user/name "User1"]
       [?a :user/roleInGroup ?c]
       [?c :roleInGroup/group ?d]
       [?d :group/name "Group2"]
       [?c :roleInGroup/role ?f]
       [?f :role/name ?g]]

Warning:scalac: ##20: Dsl2Model
1          user
------------------------------------------------
2          List(
  1          Bond(roleInGroup,group,group)
  2          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
3          Group(
  1          Bond(user,roleInGroup,roleInGroup)
  2          Bond(roleInGroup,group,group)
  3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
4          roleInGroup
------------------------------------------------
5          roleInGroup
------------------------------------------------
6          user
======================================================

Warning:scalac: ##30: Dsl2Model
1          List(
  1          Atom(user,name_,String,1,Eq(List(User1)),None,List())
  2          Group(
    1          Bond(user,roleInGroup,roleInGroup)
    2          Bond(roleInGroup,group,group)
    3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
  3          Bond(roleInGroup,role,role)
  4          Atom(role,name,String,1,VarValue,None,List()))
======================================================
     */

//    User.name_("User1").RoleInGroupx(Group.name_("Group2")).Rolex.name.debug
    /*
    [:find ?g
     :where
       [?a :user/name "User1"]
       [?a :user/roleInGroupx ?c]
       [?c :roleInGroup/group ?d]
       [?d :group/name "Group2"]
       [?c :roleInGroup/rolex ?f]
       [?f :role/name ?g]]

       Warning:scalac: ##20: Dsl2Model
       1          user
       ------------------------------------------------
       2          List(
         1          Bond(roleInGroup,group,group)
         2          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
       ------------------------------------------------
       3          Group(
         1          Bond(user,roleInGroupx,roleInGroup)
         2          Bond(roleInGroup,group,group)
         3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
       ------------------------------------------------
       4          roleInGroup
       ------------------------------------------------
       5          group
       ------------------------------------------------
       6          user
       ======================================================
       Warning:scalac: ##30: Dsl2Model
       1          List(
         1          Atom(user,name_,String,1,Eq(List(User1)),None,List())
         2          Group(
           1          Bond(user,roleInGroupx,roleInGroup)
           2          Bond(roleInGroup,group,group)
           3          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
         3          Bond(roleInGroup,rolex,role)
         4          Atom(role,name,String,1,VarValue,None,List()))
       ======================================================

     */

//    User.name_("User1").RoleInGroup(Group.name_("Group2")).Role.name.debug
    /*
    [:find ?e
     :where
       [?a :user/name "User1"]
       [?a :user/roleInGroup ?c]
       [?a :group/name "Group2"]
       [?c :roleInGroup/role ?d]
       [?d :role/name ?e]]

Warning:scalac: ##20: Dsl2Model
1          user
------------------------------------------------
2          List(
  1          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
3          Group(
  1          Bond(user,roleInGroup,roleInGroup)
  2          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
------------------------------------------------
4          roleInGroup
------------------------------------------------
5          group
------------------------------------------------
6          user
======================================================

Warning:scalac: ##30: Dsl2Model
1          List(
  1          Atom(user,name_,String,1,Eq(List(User1)),None,List())
  2          Group(
    1          Bond(user,roleInGroup,roleInGroup)
    2          Atom(group,name_,String,1,Eq(List(Group2)),None,List()))
  3          Bond(roleInGroup,role,role)
  4          Atom(role,name,String,1,VarValue,None,List()))
======================================================
     */

*/