package salesforce

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import controllers.Logging
import models.{Account, Contact, SalesforceException}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.{JsArray, JsResult, JsValue, Json}

/**
  * Created by in04468 on 29-06-2016.
  */
class SalesforceService(val salesforceDao: SalesforceDao) extends Logging{

  /**
    * Retrieves all the contacts from sales force
    *
    * @return A list of Json elements holding the contact details for retrieved
    *         customers from sales force
    */
  def getContacts : Seq[Contact] = {
    val queryStr = "select Id, FirstName, LastName, Email from Contact"
    log.info("Querying sales force with: '" + queryStr + "'")
    var result:Seq[Contact] = Nil
    try {
      result = salesforceDao.query(queryStr).validate[Seq[Contact]].get
    } catch {
      case ex: SalesforceException => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause)
      case ex: Exception => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause)
    }
    log.info("Got results : " + result.length)
    return result
  }

  def getContactByToken(token: String) : Option[Contact] = {
    getContact("Token__c", token)
  }

  def getContactByEmail(email: String) : Option[Contact] = {
    getContact("Email", email)
  }

  def getContact(field: String, value:String) : Option[Contact] = {
    val queryStr = "select Id,FirstName,LastName,Email,Token__c,Activated_On__c,Password__c,Token_Expiry_Date__c,Password_Updated_On__c from Contact where " + field + " = '" + value + "'"
    log.info("Querying sales force with: '" + queryStr + "'")
    try {
      val lresult = salesforceDao.query(queryStr).validate[Seq[Contact]].get
      if (lresult.isEmpty || lresult.length > 1) throw new SalesforceException("0 or more than one records found")
      Some(lresult.apply(0))
    } catch {
      case ex: SalesforceException => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause + ex.printStackTrace())
        None
      case ex: Exception => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause + ex.printStackTrace())
        None
    }
  }

  def getContactById(id:String) : Option[Contact] = {
    try {
      Some(salesforceDao.getObject("Contact", id).validate[Contact].get)
    } catch {
      case ex: Exception => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause + ex.printStackTrace())
        None
    }
  }

  def getAccountById(id:String) : Option[Account] = {
    try {
      Some(salesforceDao.getObject("Account", id).validate[Account].get)
    } catch {
      case ex: Exception => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause + ex.printStackTrace())
        None
    }
  }

  def setPasswd(id:String, password:String, newContact:Boolean): Int = {
    if (newContact)
      setPasswordNewContact(id, password)
    else
      setPasswordExistingContact(id, password)
  }

  def setPasswordNewContact(id:String, password: String): Int = {
    val jsonReq  : JsValue = Json.obj(
      "Password__c" -> BCrypt.hashpw(password, BCrypt.gensalt()),
      "Activated_On__c" -> (new Date).getTime,
      "Password_Updated_On__c" -> (new Date).getTime,
      "Token__c" -> "",
      "Token_Expiry_Date__c" -> ""
    )
    updateContact(id, jsonReq)
  }

  def setPasswordExistingContact(id:String, password: String): Int = {
    val jsonReq  : JsValue = Json.obj(
      "Password__c" -> BCrypt.hashpw(password, BCrypt.gensalt()),
      "Password_Updated_On__c" -> (new Date).getTime,
      "Token__c" -> "",
      "Token_Expiry_Date__c" -> ""
    )
    updateContact(id, jsonReq)
  }

  def updateContact(id:String, jsonReq: JsValue): Int = {
    var res: Int = 0
    //val dateTimeFormat = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ssZ")
    //log.info("Date is :"+dateTimeFormat.format(new Date))
    try {
      res = salesforceDao.update("Contact", id, jsonReq)
      log.info("Update query result " + res)
    } catch {
      case ex: SalesforceException => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause)
      res = 500
    }
    return res
  }

  def updateContactToken(id: String, token: String): Int = {
    var res: Int = 0
    //val dateTimeFormat = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ssZ")
    //log.info("Date is :"+BCrypt.hashpw(password, BCrypt.gensalt()))
    val jsonReq  : JsValue = Json.obj(
      "Token__c" -> token
    )
    try {
      res = salesforceDao.update("Contact", id, jsonReq)
      log.info("Update query result " + res)
    } catch {
      case ex: SalesforceException => log.error("ERROR: " + ex.getMessage +"\nError Description: " + ex.getCause)
        res = 500
    }
    return res
  }

}
