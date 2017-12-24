package cat.aartigao.trakktor

import akka.actor.{Actor, ActorLogging, Props, Timers}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{ETag, `Last-Modified`}

object FeedScoutActor {

  final val Name = "feed-scout-actor"
  final val EmptyLastModified: `Last-Modified` = `Last-Modified`(DateTime.MinValue)
  final val EmptyETag: ETag = ETag("")

  def apply(url: String): Props = Props(new FeedScoutActor(Uri(url)))

  object PollFeedTick
  case class PollFeed(headers: Seq[HttpHeader])
  case class PollFeedResult(headers: Seq[(HttpHeader, HttpHeader)])

}

class FeedScoutActor(uri: Uri) extends Actor with Timers with ActorLogging {

  import FeedScoutActor._
  import FeedParserActor._
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.HttpMethods._
  import akka.pattern.pipe
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
  import context.dispatcher

  import scala.concurrent.duration._

  implicit val mat: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val feedParser = context.actorOf(FeedParserActor(TrakktorConfig.FeedUrl), FeedParserActor.Name)

  override def preStart(): Unit = {
    log.info("Feed Scout started")
    schedulePollFeed(PollFeed(List(EmptyLastModified, EmptyETag)))
  }

  override def postStop(): Unit = log.info("Feed Scout stopped")

  override def receive: Receive = {
    case PollFeed(headers) ⇒
      Http(context.system)
        .singleRequest(HttpRequest(HEAD, uri))
        .map(extractHeadersAndDiscardBody(_))
        .map(extractPollFeedResult(headers, _))
        .pipeTo(self)

    case PollFeedResult(results) ⇒
      if (results.exists(header ⇒ header._1 != header._2)) feedParser ! CheckFeed
      schedulePollFeed(PollFeed(results.map(_._2)), 60 seconds)
  }

  def schedulePollFeed(poll: PollFeed, delay: FiniteDuration = 0 milliseconds): Unit = {
    timers.startSingleTimer(PollFeedTick, poll, delay)
  }

  def extractPollFeedResult(expected: Seq[HttpHeader], found: Seq[HttpHeader]): PollFeedResult = {
    val lastModified = (extractLastModifiedHeader(expected), extractLastModifiedHeader(found))
    val eTag = (extractETagHeader(expected), extractETagHeader(found))
    PollFeedResult(List(lastModified, eTag))
  }

  def extractHeadersAndDiscardBody(response: HttpResponse): Seq[HttpHeader] = {
    response.discardEntityBytes()
    response.headers
  }

  def extractLastModifiedHeader(headers: Seq[HttpHeader]): `Last-Modified` = {
    headers.find(_.isInstanceOf[`Last-Modified`]).map(_.asInstanceOf[`Last-Modified`]).getOrElse(EmptyLastModified)
  }

  def extractETagHeader(headers: Seq[HttpHeader]): ETag = {
    headers.find(_.isInstanceOf[ETag]).map(_.asInstanceOf[ETag]).getOrElse(EmptyETag)
  }

}