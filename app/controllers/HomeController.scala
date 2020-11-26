package controllers

import de.htwg.se.stratego.Stratego
import de.htwg.se.stratego.controller.controllerComponent.ControllerInterface
import de.htwg.se.stratego.model.matchFieldComponent.matchFieldBaseImpl.{Field, Matrix}
import javax.inject._
import play.api.libs.json.{JsNumber, Json}
import play.api.mvc._



@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val gameController: ControllerInterface = Stratego.controller
  val currentPlayerIndex: Int = Stratego.controller.currentPlayerIndex
  val players: String = Stratego.controller.playerList.head + " "+ Stratego.controller.playerList(1)
  val field: Matrix[Field] = Stratego.controller.getField

  def matchFieldText: String = {
    gameController.matchFieldToString.replaceAll(s"\\033\\[.{1,5}m","")
  }

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def stratego: Action[AnyContent] = Action {
    Ok(views.html.matchfield(gameController))
  }

  def menu: Action[AnyContent] = Action {
    Ok(views.html.menu(gameController))
  }

  def newGame(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("n")
    Ok(views.html.menu(gameController))
  }

  def load(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("l")
    Ok(views.html.matchfield(gameController))
  }

  def save(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("s")
    Ok(views.html.matchfield(gameController))
  }

  def undo(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("z")
    Ok(views.html.matchfield(gameController))
  }

  def redo(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("r")
    Ok(views.html.matchfield(gameController))
  }

  def setPlayers(player1: String, player2: String): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("n")
    Stratego.tui.processInputLine(player1 + " " + player2)
    Stratego.tui.processInputLine("i")
    Ok(views.html.matchfield(gameController))
  }

  def initMatchfield(): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("i")
    Ok(matchFieldText)

  }

  def setFigure(row: String, col: String, figure: String): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("s " + row + col + figure)
    Ok(matchFieldText)
  }

  def move(dir: String, row: String, col: String): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("m " + dir + row + col)
    Ok(views.html.matchfield(gameController))

  }

  def attack(rowA: String, colA: String, rowD: String, colD: String): Action[AnyContent] = Action {
    Ok(Stratego.tui.processInputLine("a " + rowA + colA + rowD + colD) + "\n" + matchFieldText)
    Ok(views.html.matchfield(gameController))
  }

  def gameToJson: Action[AnyContent] = Action {
    Ok(    Json.obj(
      "currentPlayerIndex" -> JsNumber(currentPlayerIndex),
      "players" -> players,
      "matchField"-> Json.toJson(
        for{
          row <- 0 until field.matrixSize
          col <- 0 until field.matrixSize
        } yield {
          var obj = Json.obj(
            "row" -> row,
            "col" -> col
          )
          if(field.field(row,col).isSet) {
            obj = obj.++(Json.obj(
              "figName" -> field.field(row, col).character.get.figure.name,
              "figValue" -> field.field(row, col).character.get.figure.value,
              "colour" -> field.field(row, col).colour.get.value
            )
            )
          }
          obj
        }
      )
    ))
  }
}
