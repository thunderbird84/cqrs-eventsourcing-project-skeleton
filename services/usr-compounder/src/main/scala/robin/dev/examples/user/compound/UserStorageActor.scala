package robin.dev.examples.user.compound

import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory
import robin.dev.examples.common.UserChangesEvent

class UserStorageActor(redisCli: RedisCliStubs) extends Actor {

  private implicit val log = LoggerFactory.getLogger(getClass)

  def receive: Receive = {
    case m: UserChangesEvent => handleUserChange(m)
    case o                   => log.error("Unknown message")
  }

  def handleUserChange(m: UserChangesEvent) = m.`type` match {
    case "user.created" => //TODO: get user from mysql database and save to redis
    case "user.updated" => //TODO: get user from mysql database and save to redis
  }
}

object UserStorageActor {
  def props(redisCli: RedisCliStubs) = Props(new UserStorageActor(redisCli))
}