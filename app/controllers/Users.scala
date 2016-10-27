package controllers

import java.text.SimpleDateFormat
import java.util.{Date, SimpleTimeZone}

import play.api.cache.CacheApi
import play.api.libs.json._
import play.api.mvc._
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Reads._
import salesforce.SalesforceService
import play.api.libs.functional.syntax._

/** Example controller; see conf/routes for the the mapping to routes */
class Users(val cache: CacheApi, salesforceService: SalesforceService) extends Controller with Security with Logging{

  /** Used for obtaining the email and password from the HTTP login request */
  case class LoginCredentials(email: String, password: String)

  /** JSON reader for [[LoginCredentials]]. */
  implicit val LoginCredentialsFromJson = (
    (__ \ "email").read[String](minLength[String](5)) ~
      (__ \ "password").read[String](minLength[String](8))
    )((email, password) => LoginCredentials(email, password))

  /**
    * Log-in a user. Expects the credentials in the body in JSON format.
    *
    * Set the cookie [[AuthTokenCookieKey]] to have AngularJS set the X-XSRF-TOKEN in the HTTP
    * header.
    *
    * @return The token needed for subsequent requests
    */
  def login() = Action(parse.json) { implicit request =>
    request.body.validate[LoginCredentials].fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))
      },
      credentials => {
        findByEmailAndPassword(credentials.email, credentials.password).fold {
          log.info("Unregistered user tried to log in")
          BadRequest(Json.obj("status" -> "KO", "message" -> "User not registered"))
        } { user =>
          val token = java.util.UUID.randomUUID.toString
          cache.set(token, user.id)
          log.info(s"User ${user.id} succesfully logged in")
          Ok(Json.obj("token" -> token))
            .withCookies(Cookie(AuthTokenCookieKey, token, None, httpOnly = false))
        }
      }
    )
  }

  def findByEmailAndPassword(email: String, password: String): Option[User] = {
    val contact = salesforceService.getContact("Email", email)
    if (contact != None && contact.get.password != None) {
      if (BCrypt.checkpw(password, contact.get.password.getOrElse("empty"))) {
        Some(User(contact.get.id, contact.get.email.getOrElse("dummy"), password, contact.get.firstName + " " + contact.get.lastName, None))
      } else {
        None
      }
    } else {
      None
    }
  }

  /**
    * Log-out a user. Invalidates the authentication token.
    *
    * Discard the cookie [[AuthTokenCookieKey]] to have AngularJS no longer set the
    * X-XSRF-TOKEN in HTTP header.
    */
  def logout() = HasToken(parse.empty) { token => userId => implicit request =>
    cache.remove(token)
    log.info(s"User $userId succesfully logged out")
    Ok.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
  }

  /**
    * Retrieves a logged in user if the authentication token is valid.
    * If the token is invalid, [[HasToken]] does not invoke this function.
    *
    * @return The user in JSON format.
    */
  def authUser() = HasToken(parse.empty) { token => userId => implicit request =>
    Ok(Json.toJson(findOneById(userId)))
  }

  def findOneById(id: String): Option[User] = {
    val contact = salesforceService.getContact("Id", id)
    if (contact != None) {
      Some(User(contact.get.id, contact.get.email.getOrElse("dummy"), contact.get.password.getOrElse("dummy"), contact.get.firstName + " " + contact.get.lastName, None))
    } else {
      None
    }
  }

  def retrieveUser(token: String) = Action {
    //Verify the token with salesforce and fet the user details
    val contact = salesforceService.getContactByToken(token)
    //log.info("Retrived user with contact.tokenDate: "+contact.get.tokenExpiryDate.get)
    if (contact != None && isTokenValid(contact.get.tokenExpiryDate.get)) {
      Ok(Json.toJson(contact.get))
    } else {
      Ok("")
    }
  }

  def isTokenValid(expiryDate: String): Boolean = {
    if (expiryDate == None) false
    val dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")
    dateTimeFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"))
    val dateExpiry = dateTimeFormat.parse(expiryDate)
    dateExpiry.getTime > (new Date).getTime
  }

  def setUserPassword() = Action(parse.json) { request => {
    log.info("Got: " + (request.body \ "id").as[String])
    //log.info("Got flag: " + (request.body \ "newuser").as[Boolean])
    var success: Boolean = false
    val res = salesforceService.setPasswd((request.body \ "id").as[String], (request.body \ "password").as[String], (request.body \ "newuser").as[Boolean])
    if (res == 204) {
      success = true
    } else {
      success = false
    }
    Ok(Json.obj("success" -> success))
    }
  }

  def requestPasswdReset(email: String) = Action {
    var success: Boolean = false
    val contact = salesforceService.getContactByEmail(email)
    if (contact != None) {
      val res = salesforceService.updateContactToken(contact.get.id, java.util.UUID.randomUUID.toString)
      if (res == 204) {
        success = true
      } else {
        success = false
      }
    }
    Ok(Json.obj("success" -> success))
  }

  def getContactById(id:String) = Action {
    val contact = salesforceService.getContactById(id)
    if (contact != None && contact.get.accountId != None) {
      Ok(Json.obj("contact" -> contact.get, "account" -> salesforceService.getAccountById(contact.get.accountId.get).get))
    } else {
      Ok(Json.obj("contact" -> contact))
    }
  }

}
