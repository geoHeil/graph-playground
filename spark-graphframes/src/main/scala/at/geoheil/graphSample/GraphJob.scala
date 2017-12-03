package at.geoheil.graphSample

import at.geoheil.graphSample.utils.{ IO, SparkBaseRunner }
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.graphframes.GraphFrame
import org.graphframes.lib.AggregateMessages

class GraphJob extends SparkBaseRunner {

  val spark = createSparkSession(this.getClass.getName)

  import spark.implicits._

  val v = IO.readCsv(spark, c.verticesInput)
    .withColumnRenamed("person:ID", "id")
    .withColumnRenamed("known_terrorist:int", "fraud")
    .drop(":LABEL")
  val e = IO.readCsv(spark, c.edgesInput)
    .withColumnRenamed(":START_ID", "src")
    .withColumnRenamed(":END_ID", "dst")
    .withColumnRenamed(":TYPE", "relationship")

  val g = GraphFrame(v, e)

  // Display the vertex and edge DataFrames
  g.vertices.show
  g.edges.show

  // Query: Get in-degree of each vertex.
  g.inDegrees.show
  g.outDegrees.show

  // calculate simple fraudulence
  val friends: DataFrame = g.find("(a)-[e]->(b)")
  friends.show
  friends.groupBy('a).agg(mean($"b.fraud").as("fraud"))
    .withColumn("id", $"a.id")
    .withColumn("name", $"a.name")
    .withColumn("fraud_src", $"a.fraud")
    .drop("a")
    .show

  // combine relationships over multiple levels
  // as type of connection is not considered - remove it
  val f1: DataFrame = g.find("(a)-[e1]->(b)")
    .withColumn("level", lit("f1"))
    .withColumnRenamed("a", "src")
    .withColumnRenamed("b", "dst")
    .select("src", "dst", "level")
  val f2: DataFrame = g.find("(a)-[e1]->(b);(b)-[e2]->(c)").withColumn("level", lit("f2"))
    .withColumnRenamed("a", "src")
    .withColumnRenamed("c", "dst")
    .drop("b")
    .select(f1.columns.map(col _): _*)
  val f3: DataFrame = g.find("(a)-[e1]->(b);(b)-[e2]->(c);(c)-[e3]->(d)").withColumn("level", lit("f3"))
    .withColumnRenamed("a", "src")
    .withColumnRenamed("d", "dst")
    .drop("b", "c")
    .select(f1.columns.map(col _): _*)

  val friendsMultipleLevels = f1
    .union(f2)
    .union(f3)

  val fFraud = friendsMultipleLevels.groupBy('src, 'level).agg(avg($"dst.fraud") as "fraudulence")
  fFraud
    .groupBy("src")
    .pivot("level")
    .agg(max('fraudulence)) // type of aggregation not really relevant here ... as only a single value can show up
    .withColumn("id", $"src.id")
    .withColumn("name", $"src.name")
    .withColumn("fraud_src", $"src.fraud")
    .drop("src")
    .show

  // all the other permutations can be created similarly

  // some additional graph algorithms for community detection (also available in neo4j)
  val r = g.labelPropagation.maxIter(2).run()
  r.show
  spark.sparkContext.setCheckpointDir("dummyCheckpoint")
  val r1 = g.connectedComponents.run()
  r1.show
  val r2 = g.stronglyConnectedComponents.maxIter(10).run()
  r2.show
  val r3 = g.labelPropagation.maxIter(5).run()
  r3.select("id", "label").show()

  // BSP operator for iterative processing
  val AM = AggregateMessages

  val msgToSrc = AM.dst("fraud")
  val msgToDst = AM.src("fraud")
  val agg = g.aggregateMessages
    .sendToSrc(msgToSrc) // send destination user's age to source
    .sendToDst(msgToDst) // send source user's age to destination
    .agg(avg(AM.msg).as("fraud_score")) // sum up ages, stored in AM.msg column
  agg.show()

}
