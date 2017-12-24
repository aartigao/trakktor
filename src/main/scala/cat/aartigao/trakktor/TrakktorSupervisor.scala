package cat.aartigao.trakktor

import akka.actor.{ Actor, ActorLogging, Props }

object TrakktorSupervisor {

  def props(): Props = Props(new TrakktorSupervisor)

}

class TrakktorSupervisor extends Actor with ActorLogging {

  val scout = context.actorOf(ScoutActor.props("http://feeds2.feedburner.com/newpctorrent?format=xml"), "scout-actor")

  override def preStart(): Unit = log.info("Trakktor Application started")
  override def postStop(): Unit = log.info("Trakktor Application stopped")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior

}
