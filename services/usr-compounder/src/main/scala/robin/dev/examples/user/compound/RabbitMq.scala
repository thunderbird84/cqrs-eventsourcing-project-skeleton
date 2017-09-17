package robin.dev.examples.user.compound

import java.net.URI

import akka.actor.{Actor, ActorRef, Props}
import org.json4s._
import com.newmotion.akka.rabbitmq.ConnectionActor.{Connected, Disconnected}
import com.newmotion.akka.rabbitmq.{ChannelActor, ConnectionActor, _}
import org.json4s.jackson.Serialization.write
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.util.control.NonFatal

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


    def setupConsumer(channel: Channel, self: ActorRef): Unit = {
      val log = LoggerFactory.getLogger(getClass)

      log.info(s"Setting up listener for event: $UserChangeRoutingKey, using exchange name: $ExchangeName, on server: ${rabbitMqUri.getHost}")

      channel.exchangeDeclare(ExchangeName, "topic", Durable)
      channel.queueDeclare(QueueName, Durable, NonExclusive, NoAutoDelete, null)
      channel.queueBind(QueueName, ExchangeName, UserChangeRoutingKey)

      val consumer = new DefaultConsumer(channel) {
        override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]): Unit = {
          handleMessage(UserChangeRoutingKey, body)
          // Will ack the message, unless a Fatal exception is thrown, as we don't want it clogging up the queue
          channel.basicAck(envelope.getDeliveryTag, NoMultipleMessageAck)
        }
      }
      channel.basicConsume(QueueName, NoAutoAcknowledge, consumer)
    }
    connection ! CreateChannel(ChannelActor.props(setupConsumer), Some("user-changes-listener"))

    def handleMessage(routingKey: String, body: Array[Byte]): Unit = try {
      val bodyString = new String(body)
      log.info(s"Got message $bodyString")
      routingKey match {
        case  UserChangeRoutingKey => //self ! DeletedTokenEvent(message.oAuthSession)
      }
    } catch {
      case NonFatal(e) =>
        log.error(s"Could not handle message ${new String(body)} from $this", e)
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
