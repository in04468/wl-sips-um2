package unit.captcha

import captcha.CaptchaValidationService
import org.mockito.Mockito._
import play.api.Configuration
import play.api.mvc.{Results, _}
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server
import unit.common.UnitTestsSpec

/**
  * Created by in04468 on 14-09-2016.
  */
class CaptchaValidationServiceTest extends UnitTestsSpec {
  "CaptchavalidationService#validateCaptchaResponse" should {
    "return value 'true' if the if captcha validation request is successful" in {
      val mockConfiguration = mock[Configuration]
      when(mockConfiguration.getString("captcha.siteverify.url")) thenReturn Some("/captcha/siteverify")
      when(mockConfiguration.getString("captcha.siteverify.secret")) thenReturn Some("captcha.siteverify.secret")
      Server.withRouter() {
        case POST(p"/captcha/siteverify" ? q"secret=$secret"
          & q"response=$response") => Action {
          Results.Ok(sampleCaptchaValidateResponseSuccess)
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = new CaptchaValidationService(client, mockConfiguration).validateCaptchaResponse("response")
          result mustBe true
        }
      }
    }

    "return value 'false' if the if captcha validation request is un-successful" in {
      val mockConfiguration = mock[Configuration]
      when(mockConfiguration.getString("captcha.siteverify.url")) thenReturn Some("/captcha/siteverify")
      when(mockConfiguration.getString("captcha.siteverify.secret")) thenReturn Some("captcha.siteverify.secret")
      Server.withRouter() {
        case POST(p"/captcha/siteverify" ? q"secret=$secret"
          & q"response=$response") => Action {
          Results.Ok(sampleCaptchaValidateResponseFailure)
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = new CaptchaValidationService(client, mockConfiguration).validateCaptchaResponse("response")
          result mustBe false
        }
      }
    }

    "return value 'false' if the if property 'captcha.siteverify.url' is not set" in {
      val mockConfiguration = mock[Configuration]
      when(mockConfiguration.getString("captcha.siteverify.url")) thenReturn None
      when(mockConfiguration.getString("captcha.siteverify.secret")) thenReturn Some("captcha.siteverify.secret")
      Server.withRouter() {
        case POST(p"/captcha/siteverify" ? q"secret=$secret"
          & q"response=$response") => Action {
          Results.Ok(sampleCaptchaValidateResponseSuccess)
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = new CaptchaValidationService(client, mockConfiguration).validateCaptchaResponse("response")
          result mustBe false
        }
      }
    }

    "return value 'false' if the if property 'captcha.siteverify.secret' is not set" in {
      val mockConfiguration = mock[Configuration]
      when(mockConfiguration.getString("captcha.siteverify.url")) thenReturn Some("/captcha/siteverify")
      when(mockConfiguration.getString("captcha.siteverify.secret")) thenReturn None
      Server.withRouter() {
        case POST(p"/captcha/siteverify" ? q"secret=$secret"
          & q"response=$response") => Action {
          Results.Ok(sampleCaptchaValidateResponseSuccess)
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = new CaptchaValidationService(client, mockConfiguration).validateCaptchaResponse("response")
          result mustBe false
        }
      }
    }

    "return value 'false' if the if captcha validation request results in an Error" in {
      val mockConfiguration = mock[Configuration]
      when(mockConfiguration.getString("captcha.siteverify.url")) thenReturn Some("/captcha/siteverify")
      when(mockConfiguration.getString("captcha.siteverify.secret")) thenReturn Some("captcha.siteverify.secret")
      Server.withRouter() {
        case POST(p"/captcha/siteverify" ? q"secret=$secret"
          & q"response=$response") => Action {
          Results.BadRequest
        }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = new CaptchaValidationService(client, mockConfiguration).validateCaptchaResponse("response")
          result mustBe false
        }
      }
    }
  }
}
