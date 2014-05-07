package slingstone.workflow

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author jixu
 */
@RunWith(classOf[JUnitRunner])
class TopologicalSorterSuite extends FunSuite {
  test("single link") {
    val graph = Map(
      "a" -> List("b"),
      "b" -> List("c"),
      "c" -> List("d")
    )
    val expected = List("a", "b", "c", "d")
    TopologicalSorter(graph) match {
      case Some(l) => assert(l === expected, "sorted sequence is wrong")
      case None => assert(false, "Cannot be sort")
    }
  }
}
