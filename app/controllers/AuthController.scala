package controllers

import securesocial.controllers.BaseLoginPage
import play.api.mvc.{ AnyContent, Action }
import play.api.Logger
import securesocial.core.RuntimeEnvironment
import models.Player

class AuthController(implicit override val env: RuntimeEnvironment[Player]) extends BaseLoginPage[Player] {
  override def login: Action[AnyContent] = {
    super.login
  }
}