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
import anorm.SqlParser._
import play.api.Logger
import securesocial.core._
import securesocial.core.providers.{ UsernamePasswordProvider, MailToken }
import scala.concurrent.Future
import securesocial.core.services.{ UserService, SaveMode }
import models.Player
import play.api.db.DB
import play.api.Play.current
import securesocial.core.providers.TwitterPlayerProvider

/**
 * A Sample In Memory user service in Scala
 *
 * IMPORTANT: This is just a sample and not suitable for a production environment since
 * it stores everything in memory.
 */
class InMemoryPlayerService extends UserService[Player] {

  // Parse a SQL output into a BasicProfile
  val parser = {
    get[String]("id") ~
    get[String]("name") ~
    get[String]("image_url") map {
      case id~name~imageUrl => 
        BasicProfile(TwitterPlayerProvider.Twitter, id, None, None, Some(name), None, Some(imageUrl), AuthenticationMethod("oauth1"), None)
    }
  }

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    DB.withConnection { implicit connection =>
	  var basicProfile = SQL("SELECT * FROM player p WHERE p.id = {id};").on("id" -> userId).as(parser.singleOpt)
	  Future.successful(basicProfile)
    }
  }

  private def findProfile(p: BasicProfile) = {
    DB.withConnection { implicit connection =>
	  var basicProfile = SQL("SELECT * FROM player p WHERE p.id = {id};").on("id" -> p.userId).as(parser.singleOpt)
	  basicProfile match {
	    case Some(user) => Player(user, List())
	    case _ => None
	  }
    }
  }

  /**
   * Update the profile
   */
  private def updateProfile(user: BasicProfile, entry: Player): Future[Player] = {
    // @TODO: Change to update only the necessary fields
  	// Save user in DB
    DB.withConnection { implicit connection =>
      SQL("""
        UPDATE player 
        SET name = {name}, image_url = {imageUrl} 
        WHERE id = {id}
      """).on('name -> entry.main.fullName.get, 'imageUrl -> entry.main.avatarUrl.get, 'id -> entry.main.userId)
      	  .executeUpdate()
    }
    
    Future.successful(entry)
  }

  def save(user: BasicProfile, mode: SaveMode): Future[Player] = {
    mode match {
      case SaveMode.SignUp =>
      	// Save user in DB
        DB.withConnection { implicit connection =>
          SQL("""
            INSERT INTO player (id, name, image_url) 
            VALUES ({id}, {name}, {imageUrl})
          """).on('id -> user.userId, 'name -> user.fullName, 'imageUrl -> user.avatarUrl)
          	  .executeUpdate()
        }
        
        // Keep it in memory
        val newPlayer = Player(user, List(user))
        Future.successful(newPlayer)
      case SaveMode.LoggedIn =>
        // first see if there is a user with this BasicProfile already.
        findProfile(user) match {
          case existingPlayer: Player =>
            // When the user logs back in
            updateProfile(user, existingPlayer)
            Future.successful(existingPlayer)
          case None =>
            val newPlayer = Player(user, List(user))
            Future.successful(newPlayer)
        }
    }
  }

  def link(current: Player, to: BasicProfile): Future[Player] = {
    // We don't allow linking to other social networks
    Future.successful(current)
    
//    if (current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
//      Future.successful(current)
//    } else {
//      val added = to :: current.identities
//      val updatedPlayer = current.copy(identities = added)
//      users = users + ((current.main.providerId, current.main.userId) -> updatedPlayer)
//      Future.successful(updatedPlayer)
//    }
  }
  
  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = ???
  override def saveToken(token: MailToken): Future[MailToken] = ???
  override def findToken(token: String): Future[Option[MailToken]] = ???
  override def deleteToken(uuid: String): Future[Option[MailToken]] = ???
  override def deleteExpiredTokens() = ???
  override def updatePasswordInfo(user: Player, info: PasswordInfo): Future[Option[BasicProfile]] = ???
  override def passwordInfoFor(user: Player): Future[Option[PasswordInfo]] = ???
}