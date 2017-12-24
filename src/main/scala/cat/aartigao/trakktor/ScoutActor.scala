package cat.aartigao.trakktor

import akka.actor.{Actor, ActorLogging, Props, Timers}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{ETag, `Last-Modified`}

object ScoutActor {

  val EmptyLastModified: `Last-Modified` = `Last-Modified`(DateTime.MinValue)
  val EmptyETag: ETag = ETag("")

  def props(url: String): Props = Props(new ScoutActor(Uri(url)))

  object PollTick

  case class Poll(headers: Seq[HttpHeader])

  case class PollResult(headers: Seq[(HttpHeader, HttpHeader)])

}

class ScoutActor(uri: Uri) extends Actor with Timers with ActorLogging {

  import ScoutActor._
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.HttpMethods._
  import akka.pattern.pipe
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
  import context.dispatcher

  import scala.concurrent.duration._

  implicit val mat: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  override def preStart(): Unit = schedulePoll(Poll(List(EmptyLastModified, EmptyETag)), 1 second)

  override def receive: Receive = {
    case Poll(headers) ⇒
      Http(context.system)
        .singleRequest(HttpRequest(HEAD, uri))
        .map(extractHeadersAndDiscardBody(_))
        .map(extractPollResult(headers, _))
        .pipeTo(self)

    case PollResult(results) ⇒
      if (results.exists(header => header._1 != header._2)) log.info("Changed") else log.info("Unchanged")
      schedulePoll(Poll(results.map(_._2)), 5 seconds)
  }

  def schedulePoll(poll: Poll, delay: FiniteDuration): Unit = {
    timers.startSingleTimer(PollTick, poll, delay)
  }

  def extractPollResult(expected: Seq[HttpHeader], found: Seq[HttpHeader]): PollResult = {
    val lastModified = (extractLastModifiedHeader(expected), extractLastModifiedHeader(found))
    val eTag = (extractETagHeader(expected), extractETagHeader(found))
    PollResult(List(lastModified, eTag))
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