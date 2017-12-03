package at.geoheil.graphSample.utils

case class ConfigurationInvalidException(
  private val message: String = "",
  private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

sealed case class GraphDummyConfigurationClass(
  levelsOfNetwork: Int,
  edgesInput: String,
  verticesInput: String) {
  require(levelsOfNetwork > 0, "At least a single level of social network needs to be considered")
}