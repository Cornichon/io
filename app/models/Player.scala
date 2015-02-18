package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import securesocial.core._

case class Player(main: BasicProfile, identities: List[BasicProfile])