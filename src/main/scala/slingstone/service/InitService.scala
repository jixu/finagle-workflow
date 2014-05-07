package slingstone.service

import slingstone.config.ServiceConfig
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpRequest

/**
 * @author jixu
 */
class InitService(config: ServiceConfig) extends GeneralService(config) {
  override def apply(input: Seq[Any]): Future[Any] = {
    assert(input.size == 1)
    assert(input(0).isInstanceOf[HttpRequest])
    val req = input(0).asInstanceOf[HttpRequest]
    Future.value(req.getUri)
  }
}

object InitService {
  def apply() = new InitService(new ServiceConfig("init", Map()))
}