package controllers

import play.api._
import play.api.mvc._
import securesocial.core._
import models.Player


class Application(override implicit val env: RuntimeEnvironment[Player]) extends SecureSocial[Player] {

  def index = UserAwareAction { implicit request =>
    request.user map(_.main.userId) match {
      case None => Ok("No ID")
      case Some(id: String) => Ok(s"Your ID is $id")   
    }
  }

}
