package unit.salesforce

import models.{Account, Contact, SalesforceException}
import org.mockito.Matchers.{anyObject, anyString}
import org.mockito.Mockito._
import play.api.libs.json.{JsArray, JsValue}
import salesforce.{SalesforceDao, SalesforceService}
import unit.common.UnitTestsSpec

/**
  * Created by in04468 on 26-07-2016.
  */
class SalesforceServiceTest extends UnitTestsSpec {
  "SalesforceService#getContacts" should {
    "return list of contacts in case of successful processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenReturn sampleMultipleContacts.as[JsArray]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContacts

      result.size mustBe 2
      result.head mustBe an[Contact]
      result.last.firstName mustBe "Sean"
    }

    "return Nil in case of any Salesforce exception in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContacts

      result mustBe Nil
    }

    "return Nil in case of any general exception in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenThrow new NoSuchElementException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContacts

      result mustBe Nil
    }
  }

  "SalesforceService#getContactByToken" should {
    "return contact object in case contact exists for a given Token" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenReturn sampleSingleContact.as[JsArray]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactByToken("sample-token")

      result.get mustBe an[Contact]
      result.get.firstName mustBe "S"
      result.get.accountId.get mustBe "001580000085DDLAA2"
    }

    "return nothing in case contact does not exist for a given token" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenReturn sampleEmptyContacts.as[JsArray]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactByToken("sample-token")

      result mustBe None
    }
  }

  "SalesforceService#getContactByEmail" should {
    "return nothing in case more than one contact exists for a given email" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenReturn sampleMultipleContacts.as[JsArray]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactByEmail("sample-email")

      result mustBe None
    }

    "return nothing in case an exception occurs in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.query(anyString)) thenThrow new NoSuchElementException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactByEmail("sample-email")

      result mustBe None
    }
  }

  "SalesforceService#getContactById" should {
    "return a specific contact object in success scenario" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.getObject("Contact", "0035800000HuGEfAAN")) thenReturn sampleActiveContact.as[JsValue]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactById("0035800000HuGEfAAN")

      result.get mustBe an[Contact]
      result.get.lastName mustBe "Kandalkar"
    }

    "return nothing in case of an exception or failure scenario" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.getObject("Contact", "sample-contact-id")) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getContactById("sample-contact-id")

      result mustBe None
    }
  }

  "SalesforceService#getAccountById" should {
    "return a specific account object in success scenario" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.getObject("Account", "001580000085DDLAA2")) thenReturn sampleAccount.as[JsValue]
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getAccountById("001580000085DDLAA2")

      result.get mustBe an[Account]
      result.get.name mustBe "sForce"
      result.get.ownerId.get mustBe "00558000001IkYXAA0"
      result.get.billingAddress.street mustBe "The Landmark @ One Market"
      result.get.billingAddress.city mustBe "San Francisco"
    }

    "return nothing in case of an exception or failure scenario" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.getObject("Account", "sample-account-id")) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.getAccountById("sample-account-id")

      result mustBe None
    }
  }

  "SalesforceService#setPassword(ExistingContact)" should {
    "return 204 resultcode in case of successfull processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenReturn 204
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.setPasswd("sample-id", "sample-password", false)

      result mustBe 204
    }

    "return 500 resultcode in case of an exception or failure in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.setPasswd("sample-id", "sample-password", false)

      result mustBe 500
    }
  }

  "SalesforceService#setPassword(NewContact)" should {
    "return 204 resultcode in case of successfull processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenReturn 204
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.setPasswd("sample-id", "sample-password", true)

      result mustBe 204
    }

    "return 500 resultcode in case of an exception or failure in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.setPasswd("sample-id", "sample-password", true)

      result mustBe 500
    }
  }

  "SalesforceService#updateContactToken" should {
    "return 204 resultcode in case of successfull processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenReturn 204
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.updateContactToken("sample-id", "sample-token")

      result mustBe 204
    }

    "return 500 resultcode in case of an exception or failure in processing" in {
      val mockSalesforceDao = mock[SalesforceDao]
      when(mockSalesforceDao.update(org.mockito.Matchers.eq("Contact"), org.mockito.Matchers.eq("sample-id"), anyObject.asInstanceOf[JsValue])) thenThrow new SalesforceException("Some reason")
      val sfService = new SalesforceService(mockSalesforceDao)
      val result = sfService.updateContactToken("sample-id", "sample-token")

      result mustBe 500
    }
  }
}
