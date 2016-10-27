package models

import org.joda.time.DateTime
import salesforce.{SalesforceDao, SalesforceService}

case class User(
  id: String,
  email: String,
  password: String,
  name: String,
  dateOfBirth: Option[DateTime],
  createdAt: DateTime = DateTime.now()
)

object User {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val UserFromJson: Reads[User] = (
    (__ \ "id").read[String] ~
    (__ \ "email").read(Reads.email) ~
    (__ \ "password").read[String] ~
    (__ \ "name").read[String] ~
    (__ \ "dateOfBirth").readNullable[DateTime] ~
    (__ \ "createdAt").read(DateTime.now())
  )(User.apply _)

  implicit val UserToJson: Writes[User] = (
    (__ \ "id").write[String] ~
    (__ \ "email").write[String] ~
    (__ \ "password").writeNullable[String] ~ // make nullable so password can be omitted
    (__ \ "name").write[String] ~
    (__ \ "dateOfBirth").writeNullable[DateTime] ~
    (__ \ "createdAt").write[DateTime]
  )((user: User) => (
    user.id,
    user.email,
    None, // here we skip the password
    user.name,
    user.dateOfBirth,
    user.createdAt
  ))

}
