package slingstone.workflow

import slingstone.service.GeneralService
import com.twitter.util.Future

/**
 * @author jixu
 */

case class Item(serviceInstance: GeneralService, inputRefNames: Seq[String]) {
  var output: Option[Future[Any]] = None
}

trait WorkflowEngine {
  def apply(itemMap: Map[String, Item], serviceRefNames: Seq[String]): Future[Any]
}

class DefaultWorkflowEngine extends WorkflowEngine {
  def apply(itemMap: Map[String, Item], serviceRefNames: Seq[String]) = {
    serviceRefNames foreach { name =>
      val item = itemMap(name)
      val inputFutures = item.inputRefNames map { inputRefName =>
        itemMap(inputRefName).output match {
          case Some(v) => v
          case None => throw new RuntimeException("Plan is not executable")
        }
      }
      val input = Future.collect(inputFutures)
      val output = input flatMap item.serviceInstance
      item.output = Some(output)
    }
    val lastServiceRefName = serviceRefNames(serviceRefNames.size-1)
    itemMap(lastServiceRefName).output.get
  }
}

object WorkflowEngine {
  val defaultWorkflowEngine = new DefaultWorkflowEngine
}
