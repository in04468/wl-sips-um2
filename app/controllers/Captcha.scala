package controllers

import play.api.cache.CacheApi
import play.api.libs.json._
import play.api.mvc._
import captcha.CaptchaValidationService

/**
  * Created by in04468 on 08-07-2016.
  */
class Captcha(val cache: CacheApi, captchaValidationService: CaptchaValidationService) extends Controller with Security with Logging {

  /**
    * Serves the /verifycaptcha request
    * Validates the captcha response with Google using APIs
    */
  def verifyCaptcha(response: String) = Action { request =>
    log.info("invoked verifycaptcha")
    Ok(Json.obj("success" -> captchaValidationService.validateCaptchaResponse(response)))
  }
}
