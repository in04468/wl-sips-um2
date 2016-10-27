package unit.controllers

import controllers.Users
import models.Contact
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.cache.CacheApi
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import salesforce.SalesforceService
import unit.common.UnitTestsSpec

import scala.concurrent.Future

/**
  * Created by in04468 on 07-09-2016.
  */
class UsersControllerTest extends UnitTestsSpec {
  "UsersController#retrieveUser" should {
    "retrieve the corresponding user/contact if it is a new user (non-activated)" in {
      val mockCacheApi = mock[CacheApi]
      val mockSalesforceService = mock[SalesforceService]
      when(mockSalesforceService.getContactByToken(anyString)) thenReturn Some(sampleNewContact.as[Contact])
      val controller = new Users(mockCacheApi, mockSalesforceService)
      val result: Future[Result] = controller.retrieveUser(anyString()).apply(FakeRequest())
      val json : JsValue = contentAsJson(result)

      status(result) mustEqual OK
      contentType(result) mustBe Some("application/json")
      (json  \ "FirstName").as[String] mustBe "S"
      (json \ "AccountId").as[String] mustBe "001580000085DDLAA2"
    }
  }

  "UsersController#retrieveUser" should {
    "NOT return any user/contact if the user dosent exist for the given token" in {
      val mockCacheApi = mock[CacheApi]
      val mockSalesforceService = mock[SalesforceService]
      when(mockSalesforceService.getContactByToken(anyString)) thenReturn None
      val controller = new Users(mockCacheApi, mockSalesforceService)
      val result: Future[Result] = controller.retrieveUser(anyString()).apply(FakeRequest())
      val json : String = contentAsString(result)

      status(result) mustEqual OK
      json mustBe ""
    }
  }

  "UsersController#retrieveUser" should {
    "NOT return any user/contact if the user is already activated" in {
      val mockCacheApi = mock[CacheApi]
      val mockSalesforceService = mock[SalesforceService]
      when(mockSalesforceService.getContactByToken(anyString)) thenReturn Some(sampleRetrieveContact.as[Contact])
      val controller = new Users(mockCacheApi, mockSalesforceService)
      val result: Future[Result] = controller.retrieveUser(anyString()).apply(FakeRequest())
      val json : String = contentAsString(result)

      status(result) mustEqual OK
      json mustBe ""
    }
  }

  "UsersController#setUserPassword" should {
    "return success value 'true' if the update contact operation is successful(result code 204)" in {
      val mockCacheApi = mock[CacheApi]
      val mockSalesforceService = mock[SalesforceService]
      when(mockSalesforceService.setPasswordExistingContact(anyString, anyString)) thenReturn 204
      val controller = new Users(mockCacheApi, mockSalesforceService)
      val request = Json.toJson("{\"id\": \"0035800000GW7GhAAL\",\"password\": \"Welcome1\"}")
      //val result: Future[Result] = controller.setUserPassword().apply(request.as[Request[JsValue]])
      //val json : String = contentAsString(result)

      //status(result) mustEqual OK
      //json mustBe "true"
    }
  }
}
