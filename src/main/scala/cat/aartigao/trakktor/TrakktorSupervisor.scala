package cat.aartigao.trakktor

import akka.actor.{ Actor, ActorLogging, Props }

object TrakktorSupervisor {

  final val Name = "trakktor-supervisor"

  def apply(): Props = Props(new TrakktorSupervisor)

}

class TrakktorSupervisor extends Actor with ActorLogging {

  val scout = context.actorOf(ScoutActor("http://feeds2.feedburner.com/newpctorrent?format=xml"), ScoutActor.Name)

  override def preStart(): Unit = log.info("Trakktor Application started")
  override def postStop(): Unit = log.info("Trakktor Application stopped")

  // No need to handle any messages
  override def receive = Actor.emptyBehavior

}
