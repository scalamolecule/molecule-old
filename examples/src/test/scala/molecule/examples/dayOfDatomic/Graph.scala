package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.schema.{Graph2Schema, GraphSchema}
import org.specs2.mutable.Specification


class Graph extends Specification with DatomicFacade {

  // See http://docs.neo4j.org/chunked/stable/cypher-cookbook-hyperedges.html

  "Simple hyperedge" >> {

    import molecule.examples.dayOfDatomic.dsl.graph._

    implicit val conn = load(GraphSchema)

    val List(r1, r2) = Role.name insert List("Role1", "Role2") eids

    // Each Group has 2 Roles
    val List(g1, g2) = Group.name.roles insert List(
      ("Group1", Set(r1, r2)),
      ("Group2", Set(r1, r2))) eids

    // User with Roles in Groups
    User.name.RoleInGroup.name.group.role insert List(
      ("User1", "U1G1R2", g1, r2),
      ("User1", "U1G2R1", g2, r1))

    // (or we could have made a nested insert)
    // (User.name.RoleInGroup * RoleInGroup.name.group.role) insert(
    //   "User1", List(("U1G1R2", g1, r2), ("U1G2R1", g2, r1)))


    // User 1 ..........................

    // User 1's Roles
    User.name_("User1").RoleInGroup.Role.name.get === List("Role2", "Role1")

    // User 1's Groups
    User.name_("User1").RoleInGroup.Group.name.get === List("Group2", "Group1")

    // User 1 Roles in Group 2
    User.name_("User1")
      .RoleInGroup.Group.name_("Group2")
      ._RoleInGroup.Role.name.get === List("Role1")
    /* Query in Neo4J:
    MATCH ({ name: 'User1' })-[:hasRoleInGroup]->(hyperEdge)-[:hasGroup]->({ name: 'Group2' }),
      (hyperEdge)-[:hasRole]->(role)
    RETURN role.name
    */

    // User 1's Groups having Role 1
    User.name_("User1")
      .RoleInGroup.Role.name_("Role1")
      ._RoleInGroup.Group.name.get === List("Group2")


    // Role 1 ............................

    // Users having Role 1
    User.name.RoleInGroup.Role.name_("Role1").get === List("User1")

    // Groups with Role 1
    RoleInGroup.Role.name_("Role1")
      ._RoleInGroup.Group.name.get === List("Group2")

    // Users in Group 2 having Role 1
    User.name
      .RoleInGroup.Group.name_("Group2")
      ._RoleInGroup.Role.name_("Role1").get === List("User1")

    // Groups where User 1 has Role 1
    User.name_("User1")
      .RoleInGroup.Group.name
      ._RoleInGroup.Role.name_("Role1").get === List("Group2")


    // Group 2 ...........................

    // Users in Group 2
    User.name.RoleInGroup.Group.name_("Group2").get === List("User1")

    // Roles of Group 2
    RoleInGroup.Group.name_("Group2")
      ._RoleInGroup.Role.name.get === List("Role1")

    // Users with Role 1 in Group 2
    User.name
      .RoleInGroup.Role.name_("Role1")
      ._RoleInGroup.Group.name_("Group2").get === List("User1")

    // Roles of User 1 in Group 2
    User.name_("User1")
      .RoleInGroup.Group.name_("Group2")
      ._RoleInGroup.Role.name.get === List("Role1")


    // All groups and the roles a user has, sorted by the name of the role
    User.name_("User1")
      .RoleInGroup.Role.name
      ._RoleInGroup.Group.name.get.sorted === List(
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
    implicit val conn = load(Graph2Schema, "Graph2")

    val List(r1, r2, r3, r4, r5, r6) = Role.name insert List("Role1", "Role2", "Role3", "Role4", "Role5", "Role6") eids
    val List(g1, g2, g3) = Group.name.roles insert List(
      ("Group1", Set(r1, r2, r5)),
      ("Group2", Set(r2, r3, r4)),
      ("Group3", Set(r3, r4, r5, r6))
    ) eids

    // Users with various Roles in various Groups
    User.name.RoleInGroup.name.group.roles insert List(
      ("User1", "U1G1R12", g1, Set(r1, r2)),
      ("User1", "U1G2R23", g2, Set(r2, r3)),
      ("User1", "U1G3R34", g3, Set(r3, r4)),
      ("User2", "U2G1R25", g1, Set(r2, r5)),
      ("User2", "U2G2R34", g2, Set(r3, r4)),
      ("User2", "U2G3R56", g3, Set(r5, r6)))

    // Users and their Roles in Groups
    User.name
      .RoleInGroup.Roles.name
      ._RoleInGroup.Group.name.get.sorted === List(
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

    // Common groups based on shared roles of User1 and User2, counting shared roles (a self-join)
    User.name_("User1" and "User2")
      .RoleInGroup.Group.name
      ._RoleInGroup.Roles.name(count).get === List(
      ("Group1", 1),
      ("Group2", 1))

    /* Query in Neo4J:
    MATCH
      (u1)-[:hasRoleInGroup]->(hyperEdge1)-[:hasGroup]->(group), (hyperEdge1)-[:hasRole]->(role),
      (u2)-[:hasRoleInGroup]->(hyperEdge2)-[:hasGroup]->(group), (hyperEdge2)-[:hasRole]->(role)
    WHERE u1.name = 'User1' AND u2.name = 'User2'
    RETURN group.name, count(role)
    ORDER BY group.name ASC
    */


    //    User.name_("User1" and "User2")
    //      .RoleInGroup.Group.name
    //      ._RoleInGroup.Roles.name(count).debug
    /*
    * Model(List(
      Atom(user,name_,String,1,And(List(User1, User2)),None,List()),
      Bond(user,roleInGroup,roleInGroup,2),
      Bond(roleInGroup,group,group,1),
      Atom(group,name,String,1,VarValue,None,List()),
      ReBond(roleInGroup,,,false,),
      Bond(roleInGroup,roles,role,2),
      Atom(role,name,String,1,Fn(count,None),None,List())))

    Query(
      Find(List(
        Var(e),
        AggrExpr(count,List(),Var(g)))),
      Where(List(
        DataClause(ImplDS,Var(a),KW(user,name,),Val(User1),Empty,NoBinding),
        DataClause(ImplDS,Var(a),KW(user,roleInGroup,roleInGroup),Var(c),Empty,NoBinding),
        DataClause(ImplDS,Var(c),KW(roleInGroup,group,group),Var(d),Empty,NoBinding),
        DataClause(ImplDS,Var(d),KW(group,name,),Var(e),Empty,NoBinding),
        DataClause(ImplDS,Var(c),KW(roleInGroup,roles,role),Var(f),Empty,NoBinding),
        DataClause(ImplDS,Var(f),KW(role,name,),Var(g),Empty,NoBinding),
        DataClause(ImplDS,Var(a_1),KW(user,name,),Val(User2),Empty,NoBinding),
        DataClause(ImplDS,Var(a_1),KW(user,roleInGroup,roleInGroup),Var(c_1),Empty,NoBinding),
        DataClause(ImplDS,Var(c_1),KW(roleInGroup,group,group),Var(d_1),Empty,NoBinding),
        DataClause(ImplDS,Var(d_1),KW(group,name,),Var(e),Empty,NoBinding),
        DataClause(ImplDS,Var(c_1),KW(roleInGroup,roles,role),Var(f_1),Empty,NoBinding),
        DataClause(ImplDS,Var(f_1),KW(role,name,),Var(g),Empty,NoBinding))))

    [:find  ?e (count ?g)
     :where [?a :user/name "User1"]
            [?a :user/roleInGroup ?c]
            [?c :roleInGroup/group ?d]
            [?d :group/name ?e]
            [?c :roleInGroup/roles ?f]
            [?f :role/name ?g]
            [?a_1 :user/name "User2"]
            [?a_1 :user/roleInGroup ?c_1]
            [?c_1 :roleInGroup/group ?d_1]
            [?d_1 :group/name ?e]
            [?c_1 :roleInGroup/roles ?f_1]
            [?f_1 :role/name ?g]]

    RULES: none

    INPUTS: none

    OUTPUTS:
    1  ["Group1" 1]
    2  ["Group2" 1]*/
  }
}