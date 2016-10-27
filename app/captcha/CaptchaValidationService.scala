package captcha

import controllers.Logging
import models.{SalesforceException}
import play.api.Configuration
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by in04468 on 08-07-2016.
  */
class CaptchaValidationService(wSClient: WSClient, configuration: Configuration) extends Logging{

  def captchaVerificationUrl = configuration.getString("captcha.siteverify.url")
    .getOrElse(throw new SalesforceException("Captcha Siteverify URL could not be loaded from configuration"))
  def secret = configuration.getString("captcha.siteverify.secret")
    .getOrElse(throw new SalesforceException("Captcha Siteverify secret could not be loaded from configuration"))

  /**
    * validates the captcha response with Google api
    *
    * @return A boolean value indicating the validation successful
    *         or failure
    */
  def validateCaptchaResponse(response: String) : Boolean = {
    log.debug("Querying google to validate captcha response: '" + response + "'")
    var retvalue = false
    try {
      implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
      log.info("captchaVerificationUrl --> "+captchaVerificationUrl +" "+response)
      val result: Future[Boolean] = getSiteverifyCallRequest(response).post("").map {
        res => (res.json \ "success").as[Boolean]
      }
      retvalue = Await.result(result, 30000 milliseconds)
    } catch {
      case ex: SalesforceException => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause)
      case ex: Exception => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause+ex.printStackTrace())
    }
    log.info("Got captcha validation results : " + retvalue)
    return retvalue
  }

  /**
    * Helper function to construct Siteverify call Request using credentials stored in
    * configuration properties or Environment variables
    *
    * @return A web service request
    */
  def getSiteverifyCallRequest(response: String) : WSRequest = {
    return wSClient.url(captchaVerificationUrl)
      .withQueryString("secret" -> secret,
        "response" -> response)
  }
}
