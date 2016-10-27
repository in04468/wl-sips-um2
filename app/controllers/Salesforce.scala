package controllers

import play.api.cache.CacheApi
import play.api.mvc._
import play.api.libs.json._
import salesforce.{SalesforceDao, SalesforceService}

/**
  * Created by in04468 on 20-06-2016.
  */
class Salesforce(val cache: CacheApi, salesforceService: SalesforceService) extends Controller with Security with Logging {

  /**
    * Serves the /sfapi/contacts request
    * Retrieves all the contacts from sales force
    */
  def getContacts() = Action { request =>
    log.info("invoked getcontacts")
    Ok(Json.toJson(salesforceService.getContacts))
  }
}
