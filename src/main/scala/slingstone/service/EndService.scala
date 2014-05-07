package slingstone.service

import slingstone.config.ServiceConfig
import com.twitter.util.Future
import com.twitter.finagle.redis.util.StringToChannelBuffer
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, HttpVersion, DefaultHttpResponse}
import org.jboss.netty.buffer.ChannelBuffer

/**
 * @author jixu
 */
class EndService(config: ServiceConfig) extends GeneralService(config) {
  implicit def s2b(str: String): ChannelBuffer = StringToChannelBuffer(str)
  override def apply(input: Seq[Any]): Future[Any] = {
    assert(input.size == 1)
    assert(input(0).isInstanceOf[String])
    val resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    resp.setContent(input(0).asInstanceOf[String])
    Future.value(resp)
  }
}

object EndService {
  def apply() = new EndService(new ServiceConfig("end", Map()))
}