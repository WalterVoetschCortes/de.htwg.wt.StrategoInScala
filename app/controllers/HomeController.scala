package controllers

import de.htwg.se.stratego.Stratego

import javax.inject._
import play.api.mvc._



@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val gameController = Stratego.controller
  val matchfieldString = gameController.matchFieldToString

  def index() = Action {
    Ok(views.html.index())
  }

  def stratego = Action {
    Ok(matchfieldString)
  }

  def newGame() = Action {
    Ok(Stratego.tui.processInputLine("n"))
  }

  def load() = Action {
    Ok(Stratego.tui.processInputLine("l"))
  }

  def save() = Action {
    Ok(Stratego.tui.processInputLine("s"))
  }

  def undo() = Action {
    Ok(Stratego.tui.processInputLine("z"))
  }

  def redo() = Action {
    Ok(Stratego.tui.processInputLine("r"))
  }

  def quit() = Action {
    Ok(Stratego.tui.processInputLine("q"))
  }

  def handle(input: String) = Action {
    Ok(Stratego.tui.processInputLine(input))
  }

}
