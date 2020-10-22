package controllers

import de.htwg.se.stratego.Stratego

import javax.inject._
import play.api.mvc._



@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val gameController = Stratego.controller
  val matchfieldString = gameController.matchFieldToString()

  def about() = Action {
    Ok(views.html.index())
  }

  def stratego = Action {
    Ok(matchfieldString)
  }

}
