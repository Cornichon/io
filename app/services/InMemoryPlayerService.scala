/**
 * Modified by Thomas Potaire - twitter @teapot
 * --------------------------------------------
 * 
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
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
package services

import anorm._
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.{ UsernamePasswordProvider, MailToken }
import scala.concurrent.Future
import securesocial.core.services.{ UserService, SaveMode }
import models.Player
import play.api.db.DB

/**
 * A Sample In Memory user service in Scala
 *
 * IMPORTANT: This is just a sample and not suitable for a production environment since
 * it stores everything in memory.
 */
class InMemoryPlayerService extends UserService[Player] {
  val logger = Logger("application.controllers.InMemoryPlayerService")

  var users = Map[(String, String), Player]()

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    if (logger.isDebugEnabled) {
      logger.debug("users = %s".format(users))
    }
    val result = for (
      user <- users.values;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.userId == userId)
    ) yield {
      basicProfile
    }
    Future.successful(result.headOption)
  }

  private def findProfile(p: BasicProfile) = {
    users.find {
      case (key, value) if value.identities.exists(su => su.providerId == p.providerId && su.userId == p.userId) => true
      case _ => false
    }
  }

  private def updateProfile(user: BasicProfile, entry: ((String, String), Player)): Future[Player] = {
    val identities = entry._2.identities
    val updatedList = identities.patch(identities.indexWhere(i => i.providerId == user.providerId && i.userId == user.userId), Seq(user), 1)
    val updatedPlayer = entry._2.copy(identities = updatedList)
    users = users + (entry._1 -> updatedPlayer)
    Future.successful(updatedPlayer)
  }

  def save(user: BasicProfile, mode: SaveMode): Future[Player] = {
    mode match {
      case SaveMode.SignUp =>
        val newPlayer = Player(user, List(user))
//        user.
        logger.debug(s"User: $user")
        logger.debug(s"newPlayer: $newPlayer")
//        DB.withConnection {
//          var result: Boolean = SQL(
//              """
//              INSERT INTO player (id, name, image_url) VALUES ({id}, {name}, {image_url})
//              """
//          )
//        }
        users = users + ((user.providerId, user.userId) -> newPlayer)
        Future.successful(newPlayer)
      case SaveMode.LoggedIn =>
        // first see if there is a user with this BasicProfile already.
        findProfile(user) match {
          case Some(existingPlayer) =>
            updateProfile(user, existingPlayer)

          case None =>
            val newPlayer = Player(user, List(user))
            users = users + ((user.providerId, user.userId) -> newPlayer)
            Future.successful(newPlayer)
        }
    }
  }

  def link(current: Player, to: BasicProfile): Future[Player] = {
    if (current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
      Future.successful(current)
    } else {
      val added = to :: current.identities
      val updatedPlayer = current.copy(identities = added)
      users = users + ((current.main.providerId, current.main.userId) -> updatedPlayer)
      Future.successful(updatedPlayer)
    }
  }
  
  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = ???
  override def saveToken(token: MailToken): Future[MailToken] = ???
  override def findToken(token: String): Future[Option[MailToken]] = ???
  override def deleteToken(uuid: String): Future[Option[MailToken]] = ???
  override def deleteExpiredTokens() = ???
  override def updatePasswordInfo(user: Player, info: PasswordInfo): Future[Option[BasicProfile]] = ???
  override def passwordInfoFor(user: Player): Future[Option[PasswordInfo]] = ???
}