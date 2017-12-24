package cat.aartigao.trakktor

import akka.actor.{ Actor, ActorLogging, Props }

object TrakktorSupervisor {

  final val Name = "trakktor-supervisor"

  def apply(): Props = Props(new TrakktorSupervisor)

}

class TrakktorSupervisor extends Actor with ActorLogging {

  val feedScout = context.actorOf(FeedScoutActor(TrakktorConfig.FeedUrl), FeedScoutActor.Name)

  override def preStart(): Unit = log.info("Trakktor Application started")
  override def postStop(): Unit = log.info("Trakktor Application stopped")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior

}
