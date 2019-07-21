package triggernz.search

case class Email private (username: String, domain: String)
object Email {
  def fromString(s: String): Option[Email] = {
    val atIdx = s.indexOf('@')
    if (atIdx >= 0) {
      val username = s.slice(0, atIdx)
      val domain = s.slice(atIdx + 1, s.length)
      if (username.length > 0 && domain.length > 0)
        Some(Email(username, domain))
      else
        None
    } else
      None
  }
}