package slingstone.service

import com.typesafe.config.Config
import slingstone.config.ServiceConfig
import scala.collection.JavaConversions._

/**
 * @author jixu
 */
object ServiceFactory {
  def apply(config: Config): Map[String, GeneralService] = {
    val services = config.getConfigList("services") map { serviceConfig =>
      val name = serviceConfig.getString("name")
      val classRef = Class.forName(serviceConfig.getString("class"))
      val constructor = classRef.getConstructor(classOf[ServiceConfig])
      val params = serviceConfig.getConfig("params").entrySet().map(entry =>
        (entry.getKey, entry.getValue.unwrapped().asInstanceOf[String])
      ).toMap
      val service = constructor.newInstance(ServiceConfig(name, params)).asInstanceOf[GeneralService]
      (name, service)
    }
    services.toMap
  }
}
