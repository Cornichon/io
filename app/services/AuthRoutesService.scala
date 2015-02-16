package services

import play.api.mvc.RequestHeader
import securesocial.core.IdentityProvider
import models.Player
import securesocial.core.services.RoutesService

class AuthRoutesService extends RoutesService.Default {
  override def loginPageUrl(implicit req: RequestHeader): String = controllers.routes.AuthController.login().absoluteURL(IdentityProvider.sslEnabled)
}