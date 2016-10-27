import play.api.ApplicationLoader.Context
import play.api.cache.EhCacheComponents
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api._
import play.filters.gzip.GzipFilter
import router.Routes

class AppLoader extends ApplicationLoader {
  override def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach(_.configure(context.environment))
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with EhCacheComponents with AhcWSComponents {

  lazy val salesforceService = new salesforce.SalesforceService(salesforceDao)
  lazy val salesforceDao = new salesforce.SalesforceDao(defaultCacheApi, wsClient, configuration)
  lazy val captchaValidationService = new captcha.CaptchaValidationService(wsClient, configuration)

  lazy val applicationController = new controllers.Application(defaultCacheApi, configuration, salesforceService)
  lazy val usersController = new controllers.Users(defaultCacheApi, salesforceService)
  //lazy val salesforceController = new controllers.Salesforce(defaultCacheApi, salesforceService)
  lazy val captchaController = new controllers.Captcha(defaultCacheApi, captchaValidationService)
  lazy val assets = new controllers.Assets(httpErrorHandler)

  // Routes is a generated class
  // Remove user + salesforce controller whilst not being used
  override def router: Router = new Routes(httpErrorHandler, applicationController, usersController, captchaController, assets)
  // override def router: Router = new Routes(httpErrorHandler, applicationController, usersController, salesforceController, assets)

  val gzipFilter = new GzipFilter(shouldGzip =
    (request, response) => {
      val contentType = response.header.headers.get("Content-Type")
      contentType.exists(_.startsWith("text/html")) || request.path.endsWith("jsroutes.js")
    })

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(gzipFilter)
}
