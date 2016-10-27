package controllers

import models._
import play.api.cache._
import play.api.libs.json._
import play.api.mvc._
import play.api.Configuration
import salesforce.SalesforceService

import scala.concurrent.duration._

/** Application controller, handles authentication */
class Application(val cache: CacheApi, configuration: Configuration, salesforceService: SalesforceService) extends Controller with Security with Logging {

  val cacheDuration = 1.day

  /**
   * Caching action that caches an OK response for the given amount of time with the key.
   * NotFound will be cached for 5 mins. Any other status will not be cached.
   */
  def Caching(key: String, okDuration: Duration) =
    new Cached(cache)
      .status(_ => key, OK, okDuration.toSeconds.toInt)
      .includeStatus(NOT_FOUND, 5.minutes.toSeconds.toInt)

  /** Serves the index page, see views/index.scala.html */
  def index = Action {
    log.debug("Index called")
    Ok(views.html.index())
  }

  /**
   * Retrieves all routes via reflection.
   * http://stackoverflow.com/questions/12012703/less-verbose-way-of-generating-play-2s-javascript-router
    *
    * @todo If you have controllers in multiple packages, you need to add each package here.
   */
  val routeCache = {
    val jsRoutesClasses = Seq(classOf[routes.javascript]) // TODO add your own packages
    jsRoutesClasses.flatMap { jsRoutesClass =>
      val controllers = jsRoutesClass.getFields.map(_.get(null))
      controllers.flatMap { controller =>
        controller.getClass.getDeclaredMethods.filter(_.getName != "_defaultPrefix").map { action =>
          action.invoke(controller).asInstanceOf[play.api.routing.JavaScriptReverseRoute]
        }
      }
    }
  }

  /**
   * Returns the JavaScript router that the client can use for "type-safe" routes.
   * Uses browser caching; set duration (in seconds) according to your release cycle.
    *
    * @param varName The name of the global variable, defaults to `jsRoutes`
   */
  def jsRoutes(varName: String = "jsRoutes") = Caching("jsRoutes", cacheDuration) {
    Action { implicit request =>
      Ok(play.api.routing.JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
    }
  }

  def webToLeadId = configuration.getString("salesforce.webtolead.id")
    .getOrElse(throw new SalesforceException("Salesforce web to lead ID could not be loaded from configuration"))
  def captchaSiteKey : String = configuration.getString("captcha.site.key")
    .getOrElse(throw new SalesforceException("Captcha Site Key could not be loaded from configuration"))

  def environment() = Action { request =>
    val json: JsValue = JsObject(Seq(
      "webtoleadId" -> JsString(webToLeadId),
      "captchaSiteKey" -> JsString(captchaSiteKey)
    ))
    Ok(json)
  }
}
