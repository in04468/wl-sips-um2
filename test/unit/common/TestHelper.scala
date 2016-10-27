package unit.common

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.Results
import play.mvc.Http.RequestBuilder

/**
  * Created by in04468 on 26-07-2016.
  */
abstract class UnitTestsSpec extends PlaySpec with MockitoSugar with Results {
  def sampleMultipleContacts = Json.parse("[{\"attributes\":{\"type\":\"Contact\",\"url\":\"/services/data/v31.0/sobjects/Contact/00358000007nP9kAAE\"},\"Id\":\"00358000007nP9kAAE\",\"FirstName\":\"Rose\",\"LastName\":\"Gonzalez\",\"Email\":\"rose@edge.com\"},{\"attributes\":{\"type\":\"Contact\",\"url\":\"/services/data/v31.0/sobjects/Contact/00358000007nP9lAAE\"},\"Id\":\"00358000007nP9lAAE\",\"FirstName\":\"Sean\",\"LastName\":\"Forbes\",\"Email\":\"sean@edge.com\"}]")
  def sampleSingleContact = Json.parse("[{\"Id\":\"0035800000HuGEfAAN\",\"FirstName\":\"S\",\"LastName\":\"Kandalkar\",\"Email\":\"sadanand.kandalkar@atos.net\",\"AccountId\":\"001580000085DDLAA2\",\"Password__c\":\"$2a$10$A1xHjX.HNVg8Lz2ItclXV.p.OQ8cpEZMF9mdGO8kuEairARZcRl1u\"}]")
  def sampleEmptyContacts = Json.parse("[]")
  def sampleoAuthToken = "{\"access_token\":\"00D58000000PA6v!AQgAQACi0JD3QFxOrMi1_CwIxd.G7hl3_OmjyoCWtX_D8cmeJQyaH7df3QzayQbYIJGc._MaoJcng2DgeSCMcQuZ7cJepBzs\",\"instance_url\":\"https://eu6.salesforce.com\",\"id\":\"https://login.salesforce.com/id/00D58000000PA6vEAG/00558000001IkYXAA0\",\"token_type\":\"Bearer\",\"issued_at\":\"1472566995708\",\"signature\":\"o+0WetiBNxetLTiX6kdy+MI7VC3sPkLMHCrjN1/rFFk=\"}"
  def sampleMultipleContactsJson = "[{\"attributes\":{\"type\":\"Contact\",\"url\":\"/services/data/v31.0/sobjects/Contact/00358000007nP9kAAE\"},\"Id\":\"00358000007nP9kAAE\",\"FirstName\":\"Rose\",\"LastName\":\"Gonzalez\",\"Email\":\"rose@edge.com\"},{\"attributes\":{\"type\":\"Contact\",\"url\":\"/services/data/v31.0/sobjects/Contact/00358000007nP9lAAE\"},\"Id\":\"00358000007nP9lAAE\",\"FirstName\":\"Sean\",\"LastName\":\"Forbes\",\"Email\":\"sean@edge.com\"}]"

  def sampleNewContact = Json.parse("{\"Id\":\"0035800000HuGEfAAN\",\"FirstName\":\"S\",\"LastName\":\"Kandalkar\",\"Email\":\"sadanand.kandalkar@atos.net\",\"AccountId\":\"001580000085DDLAA2\",\"Token_Expiry_Date__c\":\"2099-09-07T09:44:14.000+0000\",\"Password__c\":\"$2a$10$A1xHjX.HNVg8Lz2ItclXV.p.OQ8cpEZMF9mdGO8kuEairARZcRl1u\"}")
  def sampleActiveContact = Json.parse("{\"Id\":\"0035800000HuGEfAAN\",\"FirstName\":\"S\",\"LastName\":\"Kandalkar\",\"Email\":\"sadanand.kandalkar@atos.net\",\"AccountId\":\"001580000085DDLAA2\",\"Activated_On__c\":\"2016-09-07T09:44:14.000+0000\",\"Password__c\":\"$2a$10$A1xHjX.HNVg8Lz2ItclXV.p.OQ8cpEZMF9mdGO8kuEairARZcRl1u\"}")
  def sampleRetrieveContact = Json.parse("{\"Id\":\"0035800000HuGEfAAN\",\"FirstName\":\"S\",\"LastName\":\"Kandalkar\",\"Email\":\"sadanand.kandalkar@atos.net\",\"AccountId\":\"001580000085DDLAA2\",\"Activated_On__c\":\"2016-09-07T09:44:14.000+0000\",\"Password__c\":\"$2a$10$A1xHjX.HNVg8Lz2ItclXV.p.OQ8cpEZMF9mdGO8kuEairARZcRl1u\",\"Token_Expiry_Date__c\":\"2009-09-07T09:44:14.000+0000\"}")
  def sampleAccount = Json.parse("{\"Id\":\"001580000085DDLAA2\",\"Name\":\"sForce\",\"Phone\":\"(415) 901-7000\",\"Fax\":\"(415) 901-7002\",\"OwnerId\":\"00558000001IkYXAA0\",\"Website\":\"00558000001IkYXAA0\",\"BillingAddress\":{\"street\":\"The Landmark @ One Market\",\"city\":\"San Francisco\",\"postalCode\":\"94087\",\"country\":\"US\"}}")
  var requestBuilder: RequestBuilder = new RequestBuilder()
  var sampleActiveContactRequest = requestBuilder.bodyJson(sampleActiveContact).build()

  def sampleCaptchaValidateResponseSuccess = "{\"success\": true}"
  def sampleCaptchaValidateResponseFailure = "{\"success\": false}"

  val ACCESS_TOKEN = "access_token"
  val INSTANCE_URL = "instance_url"
}
