package example

import akka.actor._
import akka.persistence._

object ExampleProcessor {
  def props = Props(new ExampleProcessor {
    override val processorId = "ExampleProcessor"
  })
}

//this following is copied from http://doc.akka.io/docs/akka/snapshot/scala/persistence.html on 03.04.2014

final case class Cmd(data: String)
final case class Evt(data: String)

final case class ExampleState(events: List[String] = Nil) {
  def update(evt: Evt) = copy(evt.data :: events)
  def size = events.length
  override def toString: String = events.reverse.toString
}

class ExampleProcessor extends EventsourcedProcessor {
  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.update(event)

  def numEvents =
    state.size

  val receiveRecover: Receive = {
    case evt: Evt                                 => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap"  => saveSnapshot(state)
    case "print" => println(state)
    case "get" => sender() ! state //new created code
  }

}