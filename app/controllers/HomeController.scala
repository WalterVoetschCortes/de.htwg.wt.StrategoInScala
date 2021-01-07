package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import de.htwg.se.stratego.Stratego
import de.htwg.se.stratego.controller.controllerComponent.{ControllerInterface, FieldChanged, PlayerSwitch}
import de.htwg.se.stratego.model.matchFieldComponent.matchFieldBaseImpl.{Field, Matrix}
import javax.inject._
import play.api.libs.json.{JsNumber, JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import akka.stream.Materializer

import scala.swing.Reactor


@Singleton
class HomeController @Inject()(cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  val gameController: ControllerInterface = Stratego.controller
  var playerName1 = ""
  var playerName2 = ""
  var clientPlayerIndex = 0

  def matchFieldText: String = {
    gameController.matchFieldToString.replaceAll(s"\\033\\[.{1,5}m","")
  }

  def index(): Action[AnyContent] = Action {
    Ok(views.html.index())
  }

  def stratego: Action[AnyContent] = Action {
    gameToJson
    Ok(views.html.matchfield(gameController))
  }

  def menu: Action[AnyContent] = Action {
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
    gameController.createEmptyMatchfield(gameController.getSize)
    gameController.setPlayers(player1 + " " + player2)
    gameController.initMatchfield
    Redirect(controllers.routes.HomeController.stratego())
  }

  def setFigure(row: String, col: String, figure: String): Action[AnyContent] = Action {
    Stratego.tui.processInputLine("s " + row + col + figure)
    Ok(matchFieldText)
  }

  def setPlayer(): Action[JsValue] = Action(parse.json) {
    setPlayerRequest: Request[JsValue] => {
      val playerName = (setPlayerRequest.body \ "playerName").as[String]

      if(clientPlayerIndex == 1){
        clientPlayerIndex = 0
      }

      if(playerName1 == ""){
        playerName1 = playerName
      }else{
        playerName2 = playerName
        gameController.createEmptyMatchfield(gameController.getSize)
        gameController.setPlayers(playerName1 + " " + playerName2)
        clientPlayerIndex = 1
        gameController.initMatchfield
      }

      Ok(Json.obj(
        "clientPlayerIndex" -> JsNumber(clientPlayerIndex)
      ))
    }
  }

  def move: Action[JsValue] = Action(parse.json) {
    moveRequest: Request[JsValue] => {
      val dir = (moveRequest.body \ "dir").as[String].toCharArray
      val row = (moveRequest.body \ "row").as[Int]
      val col = (moveRequest.body \ "col").as[Int]

      if(dir(0) == 'r'){
        val rowD = row + 1
        if(rowD >= 0 && rowD < gameController.getField.matrixSize){
          if(gameController.getField.field(col, rowD).isSet){
            gameController.attack(col,row,col, rowD)
          }
        }
        gameController.move(dir(0),col,row)

      } else if(dir(0) == 'l'){
        val rowD = row - 1
        if(rowD >= 0 && rowD < gameController.getField.matrixSize){
          if(gameController.getField.field(col, rowD).isSet){
            gameController.attack(col,row,col, rowD)
          }
        }
        gameController.move(dir(0),col,row)

      }else if(dir(0) == 'd'){
        val colD = col + 1
        if(colD >= 0 && colD < gameController.getField.matrixSize){
          if(gameController.getField.field(colD, row).isSet){
            gameController.attack(col,row,colD, row)
          }
        }
        gameController.move(dir(0),col,row)

      } else if(dir(0) == 'u'){
        val colD = col - 1
        if(colD >= 0 && colD < gameController.getField.matrixSize){
          if(gameController.getField.field(colD, row).isSet){
            gameController.attack(col,row,colD, row)
          }
        }
        gameController.move(dir(0),col,row)
      }

        Ok(Json.obj(
          "matchField"-> Json.toJson(
            for{
              row <- 0 until gameController.getField.matrixSize
              col <- 0 until gameController.getField.matrixSize
            } yield {
              var obj = Json.obj(
                "row" -> row,
                "col" -> col
              )
              if(gameController.getField.field(row,col).isSet) {
                obj = obj.++(Json.obj(
                  "figName" -> gameController.getField.field(row, col).character.get.figure.name,
                  "figValue" -> gameController.getField.field(row, col).character.get.figure.value,
                  "colour" -> gameController.getField.field(row, col).colour.get.value
                )
                )
              }
              obj
            }
          ),
          "currentPlayerIndex" -> JsNumber(gameController.currentPlayerIndex),
          "currentPlayer" -> (gameController.playerList(gameController.currentPlayerIndex)).toString()
        ))
    }
  }


  def gameToJson: Action[AnyContent] = Action {
    Ok(    Json.obj(
      "currentPlayerIndex" -> JsNumber(gameController.currentPlayerIndex),
      "currentPlayer" -> (gameController.playerList(gameController.currentPlayerIndex)).toString(),
      "players" -> (gameController.playerList.head + " "+ gameController.playerList(1)),
      "matchField"-> Json.toJson(
        for{
          row <- 0 until gameController.getField.matrixSize
          col <- 0 until gameController.getField.matrixSize
        } yield {
          var obj = Json.obj(
            "row" -> row,
            "col" -> col
          )
          if(gameController.getField.field(row,col).isSet) {
            obj = obj.++(Json.obj(
              "figName" -> gameController.getField.field(row, col).character.get.figure.name,
              "figValue" -> gameController.getField.field(row, col).character.get.figure.value,
              "colour" -> gameController.getField.field(row, col).colour.get.value
            )
            )
          }
          obj
        }
      )
    ))
  }

  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      StrategoWebSocketActorFactory.create(out)
    }
  }

  object StrategoWebSocketActorFactory{
    def create(out: ActorRef): Props = {
      Props(new StrategoWebSocketActor(out))
    }
  }

  class StrategoWebSocketActor(out: ActorRef) extends Actor with Reactor{
    listenTo(gameController)

    override def receive: Receive = {
      case msg: String =>
        out ! Json.obj(
          "currentPlayerIndex" -> JsNumber(gameController.currentPlayerIndex),
          "currentPlayer" -> (gameController.playerList(gameController.currentPlayerIndex)).toString(),
          "players" -> (gameController.playerList.head + " "+ gameController.playerList(1)),
          "matchField"-> Json.toJson(
            for{
              row <- 0 until gameController.getField.matrixSize
              col <- 0 until gameController.getField.matrixSize
            } yield {
              var obj = Json.obj(
                "row" -> row,
                "col" -> col
              )
              if(gameController.getField.field(row,col).isSet) {
                obj = obj.++(Json.obj(
                  "figName" -> gameController.getField.field(row, col).character.get.figure.name,
                  "figValue" -> gameController.getField.field(row, col).character.get.figure.value,
                  "colour" -> gameController.getField.field(row, col).colour.get.value
                )
                )
              }
              obj
            }
          )
        ).toString()
    }

    reactions += {
      case event: FieldChanged => sendJsonToClient()
      case event: PlayerSwitch => sendJsonToClient()
    }

    def sendJsonToClient(): Unit = {
      println("Received event from Controller")
      out ! Json.obj(
        "currentPlayerIndex" -> JsNumber(gameController.currentPlayerIndex),
        "currentPlayer" -> (gameController.playerList(gameController.currentPlayerIndex)).toString(),
        "players" -> (gameController.playerList.head + " "+ gameController.playerList(1)),
        "matchField"-> Json.toJson(
          for{
            row <- 0 until gameController.getField.matrixSize
            col <- 0 until gameController.getField.matrixSize
          } yield {
            var obj = Json.obj(
              "row" -> row,
              "col" -> col
            )
            if(gameController.getField.field(row,col).isSet) {
              obj = obj.++(Json.obj(
                "figName" -> gameController.getField.field(row, col).character.get.figure.name,
                "figValue" -> gameController.getField.field(row, col).character.get.figure.value,
                "colour" -> gameController.getField.field(row, col).colour.get.value
              )
              )
            }
            obj
          }
        )
      ).toString()
    }
  }
}
