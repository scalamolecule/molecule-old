import sbt._


trait SettingsDatomic {
  val notAvailable = "<not available>"

  // todo: make configurable
  // Replace with path to your Datomic downloads directory
  val datomicDistributionsDir = "/Users/mg/lib/datomic"

  if (!new File(datomicDistributionsDir).isDirectory)
    throw new IllegalArgumentException(
      "Please set your datomic downloads directory path in project.SettingsDatomic")


  val testDatomicDir = new File(datomicDistributionsDir)

  // Force specific free version with `sbt compile -Ddatomic.free=0.9.9999`
  val datomicFreeVersion = sys.props.get("datomic.free").getOrElse(
    // Use last published version
    "0.9.5697"
  )

  if (!testDatomicDir.listFiles().exists(_.getName == s"datomic-free-$datomicFreeVersion"))
    throw new IllegalArgumentException(
      s"Please download datomic-free-$datomicFreeVersion to `$datomicDistributionsDir` " +
        s"and run `bin/maven-install`.\n" +
        s""
    )

  // Force Datomic to use a specific protocol (and db) by adding a protocol flag:
  // `sbt <cmd> -Dprotocol=free` where <cmd> can be `compile`, `publish` etc.
  val (datomicProtocol, datomicUseFree) = sys.props.get("protocol") match {
    case Some("mem-free") => ("mem", true)
    case Some("free")     => ("free", true)
    case Some("dev")      => ("dev", false)
    case _                => ("mem", false) // default: in-mem protocol with pro db
  }

  val datomicFreeVersions     = datomicVersions("datomic-free")
  val datomicProVersions      = datomicVersions("datomic-pro")
  val datomicDevLocalVersions = datomicVersions("dev-local")

  if (datomicProtocol != "free" && datomicProVersions.isEmpty)
    throw new IllegalArgumentException(
      s"Please download Datomic starter/pro or " +
        s"switch to free version (see README_free and README_pro)")


  // Force specific pro version with `sbt compile -Ddatomic.pro=1.0.6202`
  val datomicProVersion = sys.props.get("datomic.pro").getOrElse(
    if (datomicProVersions.nonEmpty) datomicProVersions.max else notAvailable
  )

  // Force specific dev-local version with `sbt compile -Ddatomic.dev-local=0.9.225`
  val datomicDevLocalVersion = sys.props.get("datomic.dev-local").getOrElse(
    if (datomicDevLocalVersions.nonEmpty) datomicDevLocalVersions.max else notAvailable
  )

  val datomicHome = datomicDistributionsDir + "/datomic-" + (
    if (datomicUseFree) "free-" + datomicFreeVersion else "pro-" + datomicProVersion
    )
  //  val datomicHome = datomicProtocol match {
  //    case "dev" | "pro" => datomicDistributionsDir + "/datomic-pro-" + datomicProVersion
  //    case _             => datomicDistributionsDir + "/datomic-free-" + datomicFreeVersion
  //  }

  val dbType = if (datomicProtocol == "mem") if (datomicUseFree) "(free)" else "(pro)" else ""

  // print current datomic setup to console when running sbt commands from terminal
  println(
    s"""------------------------------------------------------------------------
       |  Datomic protocol : $datomicProtocol $dbType
       |  Datomic home     : $datomicHome
       |
       |  Available versions
       |  free      : $datomicFreeVersions
       |  pro       : $datomicProVersions
       |  dev-Local : $datomicDevLocalVersions
       |
       |  Current versions
       |  free      : $datomicFreeVersion
       |  pro       : $datomicProVersion
       |  dev-Local : $datomicDevLocalVersion
       |------------------------------------------------------------------------""".stripMargin
  )


  private def datomicVersions(system: String): Seq[String] = {
    val datomicPath = Path.userHome + (System.getProperty("os.name").toLowerCase match {
      case os if os.contains("win")                        => "\\.m2\\repository\\com\\datomic\\"
      case os if os.contains("mac") | os.contains("linux") => "/.m2/repository/com/datomic/"
      case osName                                          =>
        throw new RuntimeException(s"Unknown operating system $osName")
    })
    val dir         = new File(datomicPath + system)
    if (!dir.isDirectory || dir.listFiles() == null) {
      val cmd     = system match {
        case "datomic-pro" => "bin/maven-install"
        case "dev-local"   => "./install"
        case _             => "<no cmd>"
      }
      val distDir = new File(datomicDistributionsDir)
      if (distDir.listFiles() == null
        || !distDir.listFiles.filter(_.isDirectory).exists(_.getName.contains(system))
      ) {
        // Need to download Datomic distribution
        throw new RuntimeException(
          s"Couldn't find any $system Datomic distribution in $datomicDistributionsDir" +
            s"\nPlease download a $system distribution." +
            s"\nThen run `$cmd` in the distribution directory to install libraries to local .m2 repository.")
      }
      // Need to install Datomic distribution
      throw new RuntimeException(
        s"Please run `$cmd` in the $system distribution in $distDir to install " +
          s"the system libraries to local .m2 repository.")
    }
    // Get list of Datomic system version names
    dir.listFiles.filter(_.isDirectory).map(_.getName).toList.sorted
  }
}
