package guizilla.sol.server.pages

import guizilla.src.Page
import java.net.URLDecoder

/**
  * A class for a series of pages that answers your heart's burning questions
  * with the power of the Magic Eight Ball
  */
class MagicEightBall extends Page {

  /**
    * A string representing the question the user asks
    */
  private var query: String = null

  /**
    * A string representing the user's name
    */
  private var name: String = null

  override def clone: Page = {
    val newPage = super.clone.asInstanceOf[MagicEightBall]
    newPage.query = this.query
    newPage.name = this.name
    newPage
  }
  override def defaultHandler(inputs: Map[String, String], sessionId: String): String =
    askForName(inputs, sessionId)

  /**
    * A page that asks for the user's name with a form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def askForName(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Hello and welcome. Before we begin, what is your name?" +
      " This will help the Almighty Magic Eight Ball™ to determine an answer to " +
      "your specific situation with its patented Amazing Formula™.</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/consultTheMagic\">" +
      "<input type=\"text\" name=\"name\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"

  /**
    * A page that thanks the user for thinking twice and deciding to input
    * their name after not inputting it the first time with a form for input
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def askForNameRedux(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Good choice :) thanks. " +
      "Enter your name below: </p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/consultTheMagic\">" +
      "<input type=\"text\" name=\"name\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"

  /**
    * A page that asks for the user's query with a form for input - changes
    * depending on whether the user has inputted a name
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def consultTheMagic(inputs: Map[String, String], sessionId: String): String =
    inputs.get("name") match {
      case Some(nme) =>
        name = URLDecoder.decode(nme, """UTF-8""")
        "<html><body><p>Welcome, " + name + ", to the home of the Almighty Magic Eight Ball™</p>" +
          "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
          "<p>What question can the Almighty Magic Eight Ball™ help you answer today?</p>" +
          "<input type=\"text\" name=\"query\" />" +
          "<input type=\"submit\" value=\"submit\" />" +
          "</form></body></html>"
      case None =>
        "<html><body><p>Alright then, Mysterious Anonymous Being, keep your secrets." +
          " Welcome to the home of the Almighty Magic Eight Ball™, but just a warning" +
          " that without a name, your results are going to be different...</p>" +
          "<p><a href=\"/id:" + sessionId + "/askForNameRedux\">Go back and enter name</a></p>" +
          "<form method=\"post\" action=\"/id:" + sessionId + "/displayResult\">" +
          "<p>What question can the Almighty Magic Eight Ball™ help you answer today?</p>" +
          "<input type=\"text\" name=\"query\" />" +
          "<input type=\"submit\" value=\"submit\" />" +
          "</form></body></html>"
    }

  /**
    * A page that uses super mystical methods to determine the answer to the
    * user's query and displays the result
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def displayResult(inputs: Map[String, String], sessionId: String): String =
    inputs.get("query") match {
      case Some(ques) =>
        try {
          query = URLDecoder.decode(ques, """UTF-8""")
          if (!query.endsWith("?")) {
            query = query + "?"
          }
          val answerCode = sessionId.toInt +
            (if (name != null) name.hashCode else 0)
          val answer = math.abs(answerCode % 20) match {
            case 0  => "It is certain."
            case 1  => "It is decidedly so."
            case 2  => "Without a doubt."
            case 3  => "Yes - definitely."
            case 4  => "You may rely on it."
            case 5  => "As I see it, yes."
            case 6  => "Most likely."
            case 7  => "Outlook good."
            case 8  => "Yes."
            case 9  => "Signs point to yes."
            case 10 => "Reply hazy, try again."
            case 11 => "Ask again later."
            case 12 => "Better not tell you now."
            case 13 => "Cannot predict now."
            case 14 => "Concentrate and ask again."
            case 15 => "Don't count on it."
            case 16 => "My reply is no."
            case 17 => "My sources say no."
            case 18 => "Outlook not so good."
            case 19 => "Very doubtful."
          }
          "<html><body>" +
            "<p>\"" + query + "\"</p><p>" +
            (if (name == null) {
              answer
            } else {
              name + ", the news is: " + answer
            }) +
            "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        } catch {
          case i: NumberFormatException =>
            "<html><body><p>I'm sorry, there was an error retrieving your answer." +
              "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
        }
      case None =>
        "<html><body><p>I'm sorry, there was an error retrieving your input." +
          "</p><p><a href=\"/Index\">Return to the Index</a></p></body></html>"
    }
}

/**
  * An object to instantiate the magic
  */
object MagicEightBall extends App {
  val magic = new MagicEightBall
}