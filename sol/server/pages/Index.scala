package guizilla.sol.server.pages

import guizilla.src.Page

/**
 * A page of other pages to go to on the server
 */
class Index extends Page {

  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Welcome to the Index</p>" +
      "<p><a href=\"/MagicEightBall\">Consult the magic eight ball</a></p>" +
      "<p><a href=\"/GroceryStore\">Buy some groceries</a></p>" +
      "<p><a href=\"/Search\">Search the wiki</a></p>" +
      "</body></html>"
}

object Index extends App {
  val index = new Index
}