package salesforce

import controllers.Logging
import models.{OAuthToken, SalesforceException}

import play.api.Configuration
import play.api.cache.CacheApi
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.Future
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by in04468 on 20-06-2016.
  */
class SalesforceDao(val cache: CacheApi, wSClient: WSClient, configuration: Configuration) extends Logging {

  val API_VERSION = "31.0"
  val BASE_URL = "/services/data/v" + API_VERSION + "/"
  val QUERY_URL = BASE_URL + "query"
  val SOBJECTS_URL = BASE_URL + "sobjects/"
  val ACCESS_TOKEN = "access_token"
  val INSTANCE_URL = "instance_url"

  /**
    * Load the properties form the configuration
    *
    * @return String value for each property
    */
  def salesforceUrl = configuration.getString("salesforce.oauth.url")
    .getOrElse(throw new SalesforceException("Salesforce oAuth URL could not be loaded from configuration"))
  def getSalesforceKey : String = configuration.getString("salesforce.oauth.consumer-key")
    .getOrElse(throw new SalesforceException("Salesforce consumer-key could not be loaded from configuration"))
  def getSalesforceSecret : String = configuration.getString("salesforce.oauth.consumer-secret")
    .getOrElse(throw new SalesforceException("Salesforce Consumer-secret could not be loaded from configuration"))
  def getSalesforceUserEmail : String = configuration.getString("salesforce.user.email")
    .getOrElse(throw new SalesforceException("Salesforce User email could not be loaded from configuration"))
  def getSalesforceUserPasswd : String = configuration.getString("salesforce.user.password")
    .getOrElse(throw new SalesforceException("Salesforce User Password could not be loaded from configuration"))

  /**
    * Gets the oAuth token details from
    *  - first checks in cache, if stored token found returns the details
    *  - if token not found in cache, generates a new one from Sales force
    * @return an OAuthToken object
    */
  def getOAuthToken: OAuthToken = {
    if (cache.get(ACCESS_TOKEN).isEmpty || cache.get(INSTANCE_URL).isEmpty) {
      var oAuthToken: OAuthToken = null
      try {
        oAuthToken = generateOAuthToken.get
      } catch {
        case ex: Exception => throw new SalesforceException(ex.getMessage + ex.printStackTrace(), ex.getCause)
      }
      cache.set(ACCESS_TOKEN, oAuthToken.access_token)
      cache.set(INSTANCE_URL, oAuthToken.instance_url)
      return oAuthToken
    } else {
      return  new OAuthToken(cache.get(ACCESS_TOKEN).getOrElse("None"),
        cache.get(INSTANCE_URL).getOrElse("None"), "", "", "", "")
    }
  }

  /**
    * Generates the oAuth token by making a call to Salesforce
    * @return A JsResult containing the access token and instance URL, etc details
    *          to Sales force domain
    */
  def generateOAuthToken: JsResult[OAuthToken] = {
    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
    val res: Future[JsResult[OAuthToken]] = getOAuthCallRequest.post("").map {
      response => (response.json).validate[OAuthToken]
    }
    return Await.result(res, 30000 milliseconds)
  }

  /**
    * Fetches the results in the form of Json for the desired query
    * from salesforce database
    *
    * @param queryStr String representation of sales force database query
    * @return query results from sales force in the Json array format
    */
  def query(queryStr : String): JsArray = {
    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
    val res: Future[JsArray] = constructQueryRequest(getOAuthToken, queryStr).get().map {
      response => (response.json \ "records").as[JsArray]
    }
    return Await.result(res, 30000 milliseconds)
  }

  /**
    * Updates the object in salesforce database identified by 'id'
    * for the fields included in the JSON message
    *
    * @param obj type of the object e.g. Contact, Account, etc
    * @param id String id of the object
    * @param jsonObj json representation of the fields to be updated on object
    * @return An HTTP status code indicating the result of the operation
    */
  def update(obj : String, id : String, jsonObj : JsValue) : Int = {
    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
    val res: Future[Int] = constructSObjectsRequest(getOAuthToken, obj, id).patch(jsonObj).map {
      //response => response.status
      response => response.status
    }
    return Await.result(res, 30000 milliseconds)
  }

  def getObject(obj : String, id : String) : JsValue = {
    implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
    val res: Future[JsValue] = constructSObjectsRequest(getOAuthToken, obj, id).get().map {
      response => response.json.as[JsValue]
    }
    Await.result(res, 30000 milliseconds)
  }

  /**
    * Helper function to construct the sObject request for salesforce call
    *
    * @param oAuthToken oAuth authentication details
    * @param obj type of the object e.g. Contact, Account, etc
    * @param id String id of the object
    * @return a constructed web-request
    */
  def constructSObjectsRequest(oAuthToken: OAuthToken, obj : String, id : String) : WSRequest = {
    return wSClient.url(oAuthToken.instance_url + SOBJECTS_URL + obj + "/" + id)
      .withHeaders("Authorization" -> ("Bearer " +oAuthToken.access_token))
  }

  /**
    * Helper function to construct oAuth call Request using credentials stored in
    * configuration properties or Environment variables
    *
    * @return String representation of constructed OAuth url
    */
  def getOAuthCallRequest : WSRequest = {
    return wSClient.url(salesforceUrl).withHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .withQueryString("grant_type" -> "password",
        "client_id" -> getSalesforceKey,
        "client_secret" -> getSalesforceSecret,
        "username" -> getSalesforceUserEmail,
        "password" -> getSalesforceUserPasswd)
  }

  /**
    * Helper function to construct actual query call Request
    * to sales force to fetch the data
    *
    * @param oAuthToken oAuth authentication details
    * @param queryStr String representation of sales force database query
    * @return a constructed web-request
    */
  def constructQueryRequest(oAuthToken: OAuthToken, queryStr : String) : WSRequest = {
    return wSClient.url(oAuthToken.instance_url + QUERY_URL)
      .withHeaders("Authorization" -> ("Bearer " +oAuthToken.access_token))
      .withQueryString("q" -> queryStr)
  }

}
