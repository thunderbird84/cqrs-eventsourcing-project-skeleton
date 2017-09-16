package robin.dev.examples.common

import com.typesafe.config.ConfigFactory

object Configs {
  val env: String = Option(System.getProperty("env")).getOrElse("dev")

  val config = ConfigFactory.load("conf/" + env + ".conf")

}
