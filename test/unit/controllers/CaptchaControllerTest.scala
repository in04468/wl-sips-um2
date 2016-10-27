package unit.controllers

import captcha.CaptchaValidationService
import controllers.Captcha
import org.mockito.Matchers.anyString
import org.mockito.Mockito._
import play.api.cache.CacheApi
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import unit.common.UnitTestsSpec

import scala.concurrent.Future

/**
  * Created by in04468 on 14-09-2016.
  */
class CaptchaControllerTest extends UnitTestsSpec {
  "CaptchaController#verifyCaptcha" should {
    "return success value 'true' if the captcha validation result is successful" in {
      val mockCacheApi = mock[CacheApi]
      val mockCaptchaValidationService = mock[CaptchaValidationService]
      when(mockCaptchaValidationService.validateCaptchaResponse(anyString)) thenReturn true
      val controller = new Captcha(mockCacheApi, mockCaptchaValidationService)
      val result: Future[Result] = controller.verifyCaptcha(anyString()).apply(FakeRequest())
      val json : JsValue = contentAsJson(result)
      //println("JSResult "+json)

      status(result) mustEqual OK
      contentType(result) mustBe Some("application/json")
      (json  \ "success").as[Boolean] mustBe true
    }

    "return success value 'false' if the captcha validation result is not successful" in {
      val mockCacheApi = mock[CacheApi]
      val mockCaptchaValidationService = mock[CaptchaValidationService]
      when(mockCaptchaValidationService.validateCaptchaResponse(anyString)) thenReturn false
      val controller = new Captcha(mockCacheApi, mockCaptchaValidationService)
      val result: Future[Result] = controller.verifyCaptcha(anyString()).apply(FakeRequest())
      val json : JsValue = contentAsJson(result)

      status(result) mustEqual OK
      contentType(result) mustBe Some("application/json")
      (json  \ "success").as[Boolean] mustBe false
    }
  }
}
