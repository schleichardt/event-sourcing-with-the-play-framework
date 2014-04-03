package controllers

import example._
import play.api.mvc._
import play.api.libs.concurrent.Akka
import play.api.Play
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  val actor =  Akka.system(Play.current).actorOf(ExampleProcessor.props)
  implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  def index = Action.async { request =>
    actor ! Cmd(request.id.toString)
    (actor ? "get").map {
      case  ExampleState(events) =>  Ok(events.mkString("\n"))
    }
  }

}