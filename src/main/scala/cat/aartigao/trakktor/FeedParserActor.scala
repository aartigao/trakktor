package cat.aartigao.trakktor


import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport
import akka.http.scaladsl.model._
import cat.aartigao.trakktor.entity.FeedItem


object FeedParserActor {

  final val Name = "feed-parser-actor"

  def apply(url: String): Props = Props(new FeedParserActor(Uri(url)))

  case object FetchFeedItems

  case class FetchedFeedItems(items: Seq[FeedItem])

}

class FeedParserActor(uri: Uri) extends Actor with ActorLogging with ScalaXmlSupport {

  import java.time.format.DateTimeFormatter

  import FeedParserActor._
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.unmarshalling.Unmarshal
  import akka.pattern.pipe
  import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
  import context.dispatcher

  import scala.xml.{Node, NodeSeq}

  private implicit val mat: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val formatter = DateTimeFormatter.ofPattern("E d MMM uuuu HH:mm:ss") // Sun 24 Dec 2017 05:04:56

  var lastItemTitle = ""

  override def preStart(): Unit = log.info("Feed Parser started for {}", uri)

  override def postStop(): Unit = {
    mat.shutdown()
    log.info("Feed Parser stopped for {}", uri)
  }

  override def receive = {
    case FetchFeedItems ⇒
      Http(context.system)
        .singleRequest(HttpRequest(uri = uri))
        .flatMap(Unmarshal(_).to[NodeSeq])
        .map(fetchItemsUntil(_, lastItemTitle))
        .map(FetchedFeedItems(_))
        .pipeTo(self)

    case FetchedFeedItems(items) ⇒
      lastItemTitle = items(0).title
      items.foreach(item ⇒ log.info("Fetched item: {}", item.title))
      log.info("Updated last item title to: {}", lastItemTitle)
  }

  private def fetchItemsUntil(root: NodeSeq, itemTitle: String): Seq[FeedItem] = {

    def mapRssItem(itemNode: Node): FeedItem = {

      def parseDateTime(pubDate: String): LocalDateTime = {

        import scala.util.Try

        Try(LocalDateTime.parse(pubDate, formatter)) getOrElse LocalDateTime.MIN
      }

      val title = (itemNode \ "title").text
      val description = (itemNode \ "description").text
      val link = (itemNode \ "link").text
      val author = (itemNode \ "author").text
      val category = (itemNode \ "category").text
      val pubDate = parseDateTime((itemNode \ "pubDate").text)
      FeedItem(title, description, link, author, category, pubDate)
    }

    (root \\ "item").map(mapRssItem).takeWhile(itemTitle != _.title)
  }

}
