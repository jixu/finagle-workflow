package slingstone

import com.twitter.finagle.{Service, Http}
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.twitter.util.{Await, Future}
import slingstone.workflow.{Item, WorkflowEngine}
import slingstone.service._
import com.twitter.finagle.redis.util.StringToChannelBuffer
import org.jboss.netty.buffer.ChannelBuffer
import scala.Some
import com.typesafe.config.ConfigFactory
import com.twitter.logging.Logger

/**
 * @author jixu
 */
object Server extends App {
  implicit def s2b(str: String): ChannelBuffer = StringToChannelBuffer(str)

  val logger = Logger.get(getClass)

  // load global config and create services
  val serviceConfig = ConfigFactory.parseResources("services.conf")
  val servicePool = ServiceFactory(serviceConfig)

  // load bucket configs and generate bucket context using topology sort
  val bktConfig = ConfigFactory.parseResources("buckets/demo.conf")
  val bucketConfig = BucketConfigLoader(bktConfig, servicePool)

  // create WorkflowEngine
  val workflowEngine = WorkflowEngine.defaultWorkflowEngine

  // create entry service
  val service = new Service[HttpRequest, HttpResponse] {
    override def apply(req: HttpRequest): Future[HttpResponse] = {
      val tsBeg = System.currentTimeMillis()
      // url mapping

      // get bucket context
      val itemMap = bucketConfig.items + (BucketConfigLoader.START_MODULE_KEY -> new Item(null, null))
      itemMap(BucketConfigLoader.START_MODULE_KEY).output = Some(Future.value(req))

      val plan = bucketConfig.plan
      val resultFuture = workflowEngine(itemMap, plan)
      assert(resultFuture.isInstanceOf[Future[HttpResponse]])
      resultFuture.asInstanceOf[Future[HttpResponse]] map { resp =>
        val tsEnd = System.currentTimeMillis()
        resp.setContent("total time: " + (tsEnd-tsBeg))
        resp
      }
    }
  }
  val server = Http.serve(":4082", service)
  Await.ready(server)
}
