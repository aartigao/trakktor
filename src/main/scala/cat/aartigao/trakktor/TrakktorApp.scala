package cat.aartigao.trakktor

import akka.actor.{ActorRef, ActorSystem}

object TrakktorApp extends App {

  implicit val system: ActorSystem = ActorSystem("trakktor-system")
  val supervisor: ActorRef = system.actorOf(TrakktorSupervisor.props(), "trakktor-supervisor")

}
