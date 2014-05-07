package slingstone.config

import slingstone.workflow.Item

case class ServiceConfig(name: String, params: Map[String, String])
case class BucketConfig(name: String, items: Map[String, Item], plan: Seq[String])

