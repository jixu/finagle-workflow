package slingstone

import com.typesafe.config.Config
import slingstone.config.BucketConfig

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import slingstone.workflow.{TopologicalSorter, Item}
import slingstone.service.GeneralService
import scala.collection.mutable

/**
 * @author jixu
 */
object BucketConfigLoader {
  val START_MODULE_KEY = "F8CBdTwj7fpZp::bhnDB]snK5J+/n}"

  def apply(config: Config, servicePool: Map[String, GeneralService]): BucketConfig = {
    val startId = config.getString("start_id")
    val endId = config.getString("end_id")
    val name = config.getString("name")
    val itemConfigs = config.getConfigList("workflow")
    val items = itemConfigs map { item =>
      val serviceName = item.getString("service_name")
      val input = Try(item.getStringList("input")) match {
        case Success(v) => v.toSeq 
        case Failure(e) => Seq() // Seq(START_MODULE_KEY) // no input means it directly receives the Http request
      }
      val service = servicePool.get(serviceName) match {
        case Some(s) => s
        case None => throw new Exception("Service not found: " + serviceName)
      }
      (item.getString("id"), Item(service, input))
    }

    val itemMap = items.toMap
    val plan = genPlan(itemMap)

    val startIdValue = itemMap(startId)
    BucketConfig(name, itemMap - startId + (startId -> Item(startIdValue.serviceInstance, Seq(START_MODULE_KEY))), plan)
  }

  def genPlan(itemMap: Map[String, Item]): Seq[String] = {
    // reverse graph
    val graph: mutable.Map[String, List[String]] = mutable.Map()
    itemMap foreach { case (name, item) =>
      item.inputRefNames foreach { ref =>
        graph.get(ref) match {
          case Some(l) => graph(ref) = name :: l
          case None => graph += ((ref, List(name)))
        }
      }
    }

    TopologicalSorter(graph.toMap) match {
      case Some(list) => list.toSeq
      case None => throw new Exception("Not valid config which cannot be topological sorted")
    }
  }
}
