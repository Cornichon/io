/**
 * Copyright 2012-2014 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package providers

import TwitterPlayerProvider._
import play.api.libs.oauth.{ RequestToken, OAuthCalculator }
import play.api.Logger
import scala.concurrent.{ ExecutionContext, Future }
import securesocial.core._
import securesocial.core.services.{ RoutesService, CacheService, HttpService }
import securesocial.core.providers._

/**
 * A Twitter Provider
 */
class TwitterPlayerProvider(
  routesService: RoutesService,
  cacheService: CacheService,
  client: OAuth1Client) extends OAuth1Provider(
  routesService,
  cacheService,
  client
) {
  override val id = TwitterPlayerProvider.Twitter

  override def fillProfile(info: OAuth1Info): Future[BasicProfile] = {
    client.retrieveProfile(TwitterPlayerProvider.VerifyCredentials, info).map { me =>
      val userId = (me \ Id).as[String]
      val name = (me \ Name).asOpt[String]
      val avatar = (me \ ProfileImage).asOpt[String]
      BasicProfile(id, userId, None, None, name, None, avatar, authMethod, Some(info))
    } recover {
      case e =>
        logger.error("[securesocial] error retrieving profile information from Twitter", e)
        throw new AuthenticationException()
    }
  }
}

object TwitterPlayerProvider {
  val VerifyCredentials = "https://api.twitter.com/1.1/account/verify_credentials.json"
  val Twitter = "twitter"
  val Id = "id_str"
  val Name = "screen_name"
  val ProfileImage = "profile_image_url_https"
}
