package controllers

import de.htwg.se.stratego.Stratego

import javax.inject._
import play.api.mvc._



@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val gameController = Stratego.controller

  def matchFieldText = {
    gameController.matchFieldToString.replaceAll(s"\\033\\[.{1,5}m","")
  }

  def index() = Action {
    Ok(views.html.index())
  }

  def stratego = Action {
    Ok(views.html.matchfield(gameController))
  }

  def menu = Action {
    Ok(views.html.menu(gameController))
  }

  def newGame() = Action {
    Stratego.tui.processInputLine("n")
    Ok(matchFieldText)
  }

  def load() = Action {
    Stratego.tui.processInputLine("l")
    Ok(matchFieldText)
  }

  def save() = Action {
    Stratego.tui.processInputLine("s")
    Ok(matchFieldText)
  }

  def undo() = Action {
    Stratego.tui.processInputLine("z")
    Ok(matchFieldText)
  }

  def redo() = Action {
    Stratego.tui.processInputLine("r")
    Ok(matchFieldText)
  }

  def setPlayers(player1: String, player2: String) = Action {
    Stratego.tui.processInputLine(player1 + " " + player2)
    Ok("Hello " + gameController.playerList(0) +
    " and " + gameController.playerList(1) + "!")
  }

  def initMatchfield() = Action {
    Stratego.tui.processInputLine("i")
    Ok(matchFieldText)

  }

  def setFigure(row: String, col: String, figure: String) = Action {
    Stratego.tui.processInputLine("s " + row + col + figure)
    Ok(matchFieldText)
  }

  def move(dir: String, row: String, col: String) = Action {
    Stratego.tui.processInputLine("m " + dir + row + col)
    Ok(matchFieldText)

  }

  def attack(rowA: String, colA: String, rowD: String, colD: String) = Action {
    Ok(Stratego.tui.processInputLine("a " + rowA + colA + rowD + colD) + "\n" + matchFieldText)
  }
}
