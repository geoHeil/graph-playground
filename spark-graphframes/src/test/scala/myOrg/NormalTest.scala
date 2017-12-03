package myOrg

import org.scalatest.{ FlatSpec, Matchers }

class NormalTest extends FlatSpec with Matchers {

  val input = Seq(1, 2, 3)
  val expected = 6
  "A normal test" should "just work without spark and test business logic" in {
    assert(input.sum === 6)
  }
}
