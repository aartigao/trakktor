package cat.aartigao.trakktor

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.model.Uri
import cat.aartigao.trakktor.FeedParserActor.CheckFeed

object FeedParserActor {

  final val Name = "feed-parser-actor"

  def apply(url: String): Props = Props(new FeedParserActor(Uri(url)))

  object CheckFeed

}

class FeedParserActor(uri: Uri) extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("Feed Parser started")
  override def postStop(): Unit = log.info("Feed Parser stopped")

  override def receive = {
    case CheckFeed ⇒
      log.info("Feed has changed!")
  }

}
