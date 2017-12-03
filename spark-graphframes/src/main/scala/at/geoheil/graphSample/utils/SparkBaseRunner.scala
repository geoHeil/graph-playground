package at.geoheil.graphSample.utils

import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

trait SparkBaseRunner extends App {

  @transient lazy val logger = LoggerFactory.getLogger(this.getClass)
  val c = ConfigurationUtils.loadConfiguration[GraphDummyConfigurationClass]

  def createSparkSession(appName: String): SparkSession = {
    ConfigurationUtils.createSparkSession(appName)
  }

}
