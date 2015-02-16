package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import securesocial.core._

trait PlayerBasicProfile {
  def id: String
  def name: String
  def imageUrl: String
  def authMethod: AuthenticationMethod
  def oAuth1Info: Option[OAuth1Info]
}

case class PlayerProfile (
  id: String,
  name: String,
  imageUrl: String,
  authMethod: AuthenticationMethod,
  oAuth1Info: Option[OAuth1Info] = None
) extends PlayerBasicProfile

case class Player(main: BasicProfile, identities: List[BasicProfile])

object Player {


}