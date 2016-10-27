package models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import play.api.libs.json._

/**
  * Created by in04468 on 18-08-2016.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class Account(
  id: String,
  name: String,
  phone: Option[String],
  fax: Option[String],
  annualRevenue: Option[String],
  ownerId: Option[String],
  website: Option[String],
  billingAddress: BillingAddress
)

@JsonIgnoreProperties(ignoreUnknown = true)
case class BillingAddress(
  street: String,
  city: String,
  postalCode: String,
  country: String
)

object Account {
  import play.api.libs.functional.syntax._

  implicit val AccountFromJson: Reads[Account] = (
    (__ \ "Id").read[String] ~
      (__ \ "Name").read[String] ~
      (__ \ "Phone").readNullable[String] ~
      (__ \ "Fax").readNullable[String] ~
      (__ \ "AnnualRevenue").readNullable[String] ~
      (__ \ "OwnerId").readNullable[String] ~
      (__ \ "Website").readNullable[String] ~
      (__ \ "BillingAddress").read[BillingAddress]
    )(Account.apply _)

  implicit val AccountToJson: Writes[Account] = (
    (__ \ "Id").write[String] ~
      (__ \ "Name").write[String] ~
      (__ \ "Phone").writeNullable[String] ~
      (__ \ "Fax").writeNullable[String] ~
      (__ \ "AnnualRevenue").writeNullable[String] ~
      (__ \ "OwnerId").writeNullable[String] ~
      (__ \ "Website").writeNullable[String] ~
      (__ \ "BillingAddress").write[BillingAddress]
    )((account: Account) => (
    account.id,
    account.name,
    account.phone,
    account.fax,
    account.annualRevenue,
    account.ownerId,
    account.ownerId,
    account.billingAddress
    ))
}

object BillingAddress {
  import play.api.libs.functional.syntax._

  implicit val BillingAddressFromJson: Reads[BillingAddress] = (
    (__ \ "street").read[String] ~
      (__ \ "city").read[String] ~
      (__ \ "postalCode").read[String] ~
      (__ \ "country").read[String]
    )(BillingAddress.apply _)

  implicit val BillingAddressToJson: Writes[BillingAddress] = (
    (__ \ "street").write[String] ~
      (__ \ "city").write[String] ~
      (__ \ "postalCode").write[String] ~
      (__ \ "country").write[String]
    )((billingAddress: BillingAddress) => (
    billingAddress.street,
    billingAddress.city,
    billingAddress.postalCode,
    billingAddress.country
    ))
}


