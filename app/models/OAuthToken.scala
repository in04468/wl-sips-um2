package models

import play.api.libs.json._

/**
  * Created by in04468 on 21-06-2016.
  */
case class OAuthToken (
  access_token: String,
  instance_url: String,
  id: String,
  token_type: String,
  issued_at: String,
  signature: String
)

object OAuthToken {

  import play.api.libs.functional.syntax._

  implicit val OAuthTokenFromJson: Reads[OAuthToken] = (
    (__ \ "access_token").read[String] ~
      (__ \ "instance_url").read[String] ~
      (__ \ "id").read[String] ~
      (__ \ "token_type").read[String] ~
      (__ \ "issued_at").read[String] ~
      (__ \ "signature").read[String]
    )(OAuthToken.apply _)

  implicit val OAuthTokenToJson: Writes[OAuthToken] = (
    (__ \ "access_token").write[String] ~
      (__ \ "instance_url").write[String] ~
      (__ \ "id").write[String] ~
      (__ \ "token_type").write[String] ~
      (__ \ "issued_at").write[String] ~
      (__ \ "signature").write[String]
    )((oAuthToken: OAuthToken) => (
    oAuthToken.access_token,
    oAuthToken.instance_url,
    oAuthToken.id,
    oAuthToken.token_type,
    oAuthToken.issued_at,
    oAuthToken.signature
    ))
}