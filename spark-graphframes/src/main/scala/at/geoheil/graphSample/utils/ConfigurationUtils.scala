package at.geoheil.graphSample.utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import pureconfig._

object ConfigurationUtils {

  def loadConfiguration[T <: Product](implicit reader: ConfigReader[T]): T = {
    loadConfig[T] match {
      case Right(s) => s
      case Left(l) => throw new ConfigurationInvalidException(s"Failed to start. There is a problem with the configuration: $l")
    }
  }

  def createSparkSession(appName: String): SparkSession = {
    SparkSession
      .builder()
      .config(createConf(appName))
      //  .enableHiveSupport()
      .getOrCreate()
  }

  def createConf(appName: String): SparkConf = {
    new SparkConf()
      .setAppName(appName)
      .setIfMissing("spark.master", "local[*]")
      .setIfMissing("spark.speculation", "true")
      .setIfMissing("spark.driver.memory", "12G")
      .setIfMissing("spark.default.parallelism", "12")
      .setIfMissing("spark.driver.maxResultSize", "1G")
      .setIfMissing("spark.driver.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.executor.extraJavaOptions", "-XX:+UseG1GC")
      .setIfMissing("spark.kryoserializer.buffer.max", "1G")
      .setIfMissing("spark.kryo.unsafe", "true")
      .setIfMissing("spark.kryo.referenceTracking", "false")
      .setIfMissing("spark.memory.offHeap.enabled", "true")
      .setIfMissing("spark.memory.offHeap.size", "1g")
  }

}
