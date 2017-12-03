package myOrg

import com.holdenkarau.spark.testing.{ DatasetSuiteBase, SharedSparkContext }
import org.scalatest.{ FlatSpec, Matchers }

class SparkTest extends FlatSpec with Matchers with SharedSparkContext with DatasetSuiteBase {

  val input = Seq(1, 2, 3)
  val expected = 6
  "A Spark thing" should "sparkle and count correctly" in {
    import spark.implicits._
    val df = input.toDS
    assert(df.count === 3)
  }

}
