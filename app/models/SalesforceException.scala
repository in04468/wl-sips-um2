package models

/**
  * Created by in04468 on 29-06-2016.
  */
class SalesforceException (message: String, cause: Throwable)  extends RuntimeException(message, cause) {
  if (cause != null)
    initCause(cause)

  def this(message: String) = this(message, null)
}
