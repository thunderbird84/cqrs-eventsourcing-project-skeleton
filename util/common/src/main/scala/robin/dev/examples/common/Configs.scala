package robin.dev.examples.common

import java.net.URI

import com.typesafe.config.ConfigFactory

object Configs {
  val env: String = Option(System.getProperty("env")).getOrElse("dev")

  val config = ConfigFactory.load("conf/" + env + ".conf")

  def amqpUri: URI = URI.create(config.getString("amqp.uri"))

  def redisUrl: String = config.getString("redis.url")

  def jdbcUrl: String = config.getString("jdbc.url")
  def jdbcUsername: String = config.getString("jdbc.username")
  def jdbcPassword: String = config.getString("jdbc.password")
}
