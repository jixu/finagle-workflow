package slingstone.service

import slingstone.config.ServiceConfig
import com.twitter.util.Future

/**
 * @author jixu
 */
class StringConcatService(config: ServiceConfig) extends GeneralService(config) {
  private val separator = config.params("separator")
  override def apply(input: Seq[Any]): Future[Any] = {
    Future.value(input.map(_.asInstanceOf[String]).mkString(separator))
  }
}

object StringConcatService {
  def apply(name: String, separator: String) = new StringConcatService(ServiceConfig(name, Map("separator" -> separator)))
}
