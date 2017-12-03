name := "sparkGraphsample"
organization := "geoheil"
version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-feature",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Ywarn-unused"
)

//The default SBT testing java options are too small to support running many of the tests
// due to the need to launch Spark in local mode.
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")
parallelExecution in Test := false

val spark = "2.2.0"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % spark % "provided",
  "org.apache.spark" %% "spark-sql" % spark % "provided",
  "graphframes" % "graphframes" % "0.5.0-spark2.1-s_2.11",
  "org.apache.spark" %% "spark-graphx" % spark % "provided",
  //  "org.apache.spark" %% "spark-hive" % spark % "provided",
  //  "org.apache.spark" %% "spark-mllib" % spark % "provided",
  //  "org.apache.spark" %% "spark-streaming" % spark % "provided",

  //  typesafe configuration
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",

  // testing
  "com.holdenkarau" %% "spark-testing-base" % s"${spark}_0.8.0" % "test"
)

fork := true
fullClasspath in reStart := (fullClasspath in Compile).value
run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass.in(Compile, run), runner.in(Compile, run)).evaluated

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.deduplicate
}

//assemblyShadeRules in assembly := Seq(
//  ShadeRule.rename("com.google.**" -> "shadedguava.@1").inAll
//)

//test in assembly := {}

initialCommands in console :=
  """
    |import org.apache.spark.sql.SparkSession
    |import org.slf4j.LoggerFactory
    |import at.geoheil.graphSample.utils.ConfigurationUtils
    |import at.geoheil.graphSample.utils.GraphDummyConfigurationClass
    |import org.graphframes.lib.AggregateMessages
    |
    |val logger = LoggerFactory.getLogger(this.getClass)
    |val c = ConfigurationUtils.loadConfiguration[GraphDummyConfigurationClass]
    |
    |val spark = ConfigurationUtils.createSparkSession("console")
    |
    |import spark.implicits._
    |import org.graphframes._
    |import at.geoheil.graphSample.utils.{ IO, SparkBaseRunner }
    |import org.apache.spark.sql.DataFrame
    |import org.apache.spark.sql.functions._
    |import org.graphframes.GraphFrame
  """.stripMargin

mainClass := Some("at.geoheil.graphSample.GraphJob")