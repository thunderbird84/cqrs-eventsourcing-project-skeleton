package robin.dev.examples.user.cmd


import java.net.URI

import akka.actor.{Actor, ActorRef, Props}
import com.newmotion.akka.rabbitmq.ConnectionActor.{Connected, Disconnected}
import com.newmotion.akka.rabbitmq.{ChannelActor, ConnectionActor, _}
import com.rabbitmq.client.ConnectionFactory
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.slf4j.LoggerFactory
import robin.dev.examples.common.UserChangesEvent

import scala.concurrent.duration._

object RabbitMq {
  def props(rabbitMqUri: URI) = Props(new RabbitMq(rabbitMqUri))

  val ExchangeName = "robin.event"
  val QueueName ="user.changes"
  val UserChangeRoutingKey = "user.changes.key"
  class RabbitMq(rabbitMqUri: URI) extends Actor {
    implicit val formats = DefaultFormats

    private val log = LoggerFactory.getLogger(getClass)
    var healthCheckActor: ActorRef = self

    //private val system = context.system

    override def receive = {
      case In.SendMessage(key, message)   =>  publishMessage(key, message)
      case Connected                      => log.info(s"Qpid Connected")
      case Disconnected                   => log.error(s"Qpid Disconnected")
      case c: ChannelCreated              => log.info(s"Qpid ChannelCreated")
      case e                              => log.warn(s"Unknown event $e")
    }

    private val connectionFactory: ConnectionFactory = {
      val cf = new ConnectionFactory()
      cf.useNio()
      cf.setUri(rabbitMqUri)
      cf
    }

    val connection = context.actorOf(ConnectionActor.props(connectionFactory, reconnectionDelay = 10.seconds), "RabbitMq")


    def setupPublisher(channel: Channel, self: ActorRef): Unit = {
      log.info(s"Setting up publisher to queue: $QueueName, using exchange name: $ExchangeName, on server: ${rabbitMqUri.getHost}")

      channel.queueBind(QueueName, ExchangeName, "")
    }
    val publisher: ActorRef = connection.createChannel(ChannelActor.props(setupPublisher), Some("message_publisher"))

    def publishMessage(routingKey: String, msg: AnyRef): Unit = {
      def publish(channel: Channel) {

        val message = msg match {
          case x:UserChangesEvent => x
        }
        channel.basicPublish (ExchangeName, routingKey, null, write(message).getBytes)

      }
      publisher ! ChannelMessage(publish, dropIfNoChannel = false)
    }

    override def postStop(): Unit = {
      log.info(s"Closing $connection")
      context.stop(connection)
    }
  }

  case object In {
    sealed trait BusMessage
    case class SendMessage(routingKey: String, msg: AnyRef )
  }

  val NonExclusive: Boolean = false
  val NoAutoDelete: Boolean = false
  val NoMultipleMessageAck: Boolean = false
  val NoAutoAcknowledge: Boolean = false
  val Durable: Boolean = false

}
