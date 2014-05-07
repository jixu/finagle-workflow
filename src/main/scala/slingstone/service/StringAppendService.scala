package slingstone.service

import slingstone.config.ServiceConfig
import com.twitter.util.Future

/**
 * @author jixu
 */
class StringAppendService(config: ServiceConfig) extends GeneralService(config: ServiceConfig) {
  private val appendix = config.params("appendix")
  override def apply(input: Seq[Any]): Future[Any] = {
    assert(input.size == 1)
    assert(input(0).isInstanceOf[String])
    val str = input(0).asInstanceOf[String]
    Future.value(str + appendix)
  }
}

object StringAppendService {
  def apply(name: String, appendix: String) = new StringAppendService(ServiceConfig(name, Map("appendix" -> appendix)))
}
