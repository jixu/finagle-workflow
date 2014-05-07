package slingstone.service

import com.twitter.finagle.Service
import slingstone.config.ServiceConfig

/**
 * @author jixu
 */
abstract class GeneralService(config: ServiceConfig) extends Service[Seq[Any], Any]
