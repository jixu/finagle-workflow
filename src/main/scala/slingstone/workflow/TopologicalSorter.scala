package slingstone.workflow

import scala.collection.mutable

/**
 * http://en.wikipedia.org/wiki/Topological_sorting#Algorithms, using the one based on depth-first search
 * @author xuji
 */
object TopologicalSorter {
  def apply(graph: Map[String, List[String]]): Option[List[String]] = {
    assert(!graph.isEmpty) // at lease one edge in the graph
    var sorted: List[String] = List()
    val unmarked: mutable.Set[String] = mutable.Set()
    unmarked ++= graph.keys
    val marked: mutable.Map[String, Int] = mutable.Map() // value 0 means temp mark, value 1 means permanent mark

    def visit(node: String) {
      marked.get(node) match {
        case Some(v) => // marked
          if (v == 0) throw new Exception("Not a DAG")
        case None => // unmarked
          marked += ((node, 0))
          unmarked -= node
          graph.get(node) match {
            case Some(seq) => seq foreach visit
            case None => // no edge from this node, should be the last node
          }
          marked(node) = 1
          sorted = node :: sorted
      }
    }

    try {
      while (!unmarked.isEmpty) {
        visit(unmarked.head)
      }
      Some(sorted)
    } catch {
      case _: Exception => None
    }
  }
}
