package cat.aartigao.trakktor

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}

object TrakktorApp extends App {

  val config: Config = ConfigFactory.load()
  val system: ActorSystem = ActorSystem("trakktor-system", config)
  val supervisor: ActorRef = system.actorOf(TrakktorSupervisor(), TrakktorSupervisor.Name)

}
