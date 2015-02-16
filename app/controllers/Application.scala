package controllers

import play.api._
import play.api.mvc._
import securesocial.core._
import models.Player


class Application(override implicit val env: RuntimeEnvironment[Player]) extends SecureSocial[Player] {

  def index = Action.async { implicit request =>
    SecureSocial.currentUser[Player].map { maybeUser =>
      val userId = maybeUser.map(_.main.userId).getOrElse("unknown")
      Ok(s"UserID: $userId - User: $maybeUser")
//      Ok(s"Your id is $userId")
    }
  }

}
