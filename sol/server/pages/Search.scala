package guizilla.sol.server.pages

import guizilla.src.Page
import java.io.IOException
import java.net.Socket
import java.net.URLDecoder
import java.net.UnknownHostException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.OutputStream
import java.io.BufferedReader
import java.io.BufferedWriter

/**
  * A class that searches through the eckert server based on a user query
  */
class Search extends Page {

  /**
    * A string representing the user's query
    */
  private var query: String = null

  /**
    * An array where each ith entry is the ith title in the search results
    */
  private val pageTitles: Array[String] = new Array(10)

  /**
    * The host to connect to and direct searches
    */
  private val HOST: String = "eckert"

  override def defaultHandler(inputs: Map[String, String], sessionId: String) =
    searchHomePage(inputs, sessionId)

  /**
    * Given a title, connects to the eckert server to retrieve the text for that
    * particular page
    *
    * @param title - a string representing the title of a page
    *
    * @return a string representing the text of that page
    */
  private def retrievePage(title: String): String = {
    val PORT: Int = 8082
    var socket: Socket = null
    var bufferedWriter: BufferedWriter = null
    var bufferedReader: BufferedReader = null
    try {
      socket = new Socket(HOST, PORT)
      val outputStream = socket.getOutputStream
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))
      val inputStream = socket.getInputStream
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
      bufferedWriter.write(title + "\n")
      bufferedWriter.flush
      socket.shutdownOutput
      var response: String = ""
      var advancer: String = bufferedReader.readLine
      while (advancer != null) {
        response += advancer + "\n"
        advancer = bufferedReader.readLine
      }
      response
    } catch {
      case n: NullPointerException =>
        println("Null Pointer Exception! Yikes! " + n.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, there was an error " +
          "in retrieving that page." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
      case e: UnknownHostException =>
        println("Unknown Host Exception! Yikes! " + e.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, we can't connect to eckert " +
          "right now." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
      case i: IOException =>
        println("IO Exception! Yikes! " + i.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, there was an error " +
          "in retrieving that page." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
    } finally {
      try {
        bufferedReader.close
        bufferedWriter.close
        if (socket != null &&
          !socket.isClosed()) {
          socket.shutdownInput()
          socket.close()
        }
      } catch {
        case e: IOException =>
          println("IO Exception! Yikes! " + e.getMessage)
          println("--------------------------")
      }
    }
  }

  /**
    * Connects to eckert and searches for the user query, returning the raw
    * response of the server
    */
  private def connectToServer: String = {
    val PORT: Int = 8081
    var socket: Socket = null
    var bufferedWriter: BufferedWriter = null
    var bufferedReader: BufferedReader = null
    try {
      socket = new Socket(HOST, PORT)
      val outputStream = socket.getOutputStream
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))
      val inputStream = socket.getInputStream
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
      bufferedWriter.write("REQUEST\t" + query + "\n")
      bufferedWriter.flush
      socket.shutdownOutput
      var response: String = ""
      var advancer: String = bufferedReader.readLine
      while (advancer != null && advancer != "") {
        response += advancer
        advancer = bufferedReader.readLine
      }
      response
    } catch {
      case n: NullPointerException =>
        println("Null Pointer Exception! Yikes! " + n.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, there was an error " +
          "in retrieving that page." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
      case e: UnknownHostException =>
        println("Unknown Host Exception! Yikes! " + e.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, we can't connect to eckert " +
          "right now." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
      case i: IOException =>
        println("IO Exception! Yikes! " + i.getMessage)
        println("--------------------------")
        "<html><body><p>I'm sorry, there was an error " +
          "in retrieving that page." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
    } finally {
      try {
        if (bufferedReader != null && bufferedWriter != null) {
          bufferedReader.close
          bufferedWriter.close
        }
        if (socket != null &&
          !socket.isClosed()) {
          socket.shutdownInput()
          socket.close()
        }
      } catch {
        case e: IOException =>
          println("IO Exception! Yikes! " + e.getMessage)
          println("--------------------------")
      }
    }
  }

  /**
    * Given the raw response of top 10 pages and the session ID, generates the
    * HTML page containing the links to each page in the top 10
    *
    * @param response - a string of the raw top 10 pages from the eckert server
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  private def generatePageListHTML(response: String, sessionId: String): String = {
    val responses: Array[String] = response.drop(9).split("\t")

    if (response.drop(9) == "") {
      "<html><body><p>:0 Oh no! Your search for " + query +
        " did not match any documents.</p><p>Suggestions:</p><p>" +
        "> Make sure all words are spelled correctly.</p><p>> Try different " +
        "keywords.</p><p>> Try more general keywords." +
        "</p><p><a href=\"/Index\">Return to Index</a></p></body></html>"
    } else if (responses.size < 10) {
      var links: String = ""
      for (i <- 1 to responses.size) {
        pageTitles(i - 1) = responses(i - 1).drop(2)
        links +=
          "<p><a href=\"/id:" + sessionId + "/result" + i.toString + "\">" +
          i.toString + ". " + pageTitles(i - 1) + "</a></p>"
      }
      "<html><body><p>We could only find " + responses.size + " results for " +
        query + ":</p>" + links + "</body></html>"
    } else {
      var links: String = ""
      for (i <- 1 to 9) {
        pageTitles(i - 1) = responses(i - 1).drop(2)
        links +=
          "<p><a href=\"/id:" + sessionId + "/result" + i.toString + "\">" +
          i.toString + ". " + pageTitles(i - 1) + "</a></p>"
      }
      pageTitles(9) = responses(9).drop(3)
      links +=
        "<p><a href=\"/id:" + sessionId + "/result10\">" +
        "10. " + pageTitles(9) + "</a></p>"
      "<html><body><p>Top 10 page results for " + query + ":</p>" +
        links + "</body></html>"
    }
  }

  /**
    * A page that prompts the user to enter a search query
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def searchHomePage(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>Search</p>" +
      "<form method=\"post\" action=\"/id:" + sessionId + "/listPages\">" +
      "<p>Please enter a free text query: </p>" + "<input type=\"text\" name=\"query\" />" +
      "<input type=\"submit\" value=\"submit\" />" +
      "</form></body></html>"

  /**
    * A page that displays the results of the user's query in the form of
    * a list of links
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def listPages(inputs: Map[String, String], sessionId: String): String =
    inputs.get("query") match {
      case Some(q) => {
        query = URLDecoder.decode(q, """UTF-8""")
        if (query.contains("\t")) {
          "<html><body><p>I'm sorry, please ensure that your query " +
            "does not contain a tab." +
            "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
        } else {
          generatePageListHTML(connectToServer, sessionId)
        }
      }
      case None =>
        "<html><body><p>I'm sorry, there was an error " +
          "in retrieving your query." +
          "</p><p><a href=\"/Search\">Return to Homepage</a></p></body></html>"
    }

  /**
    * A page displays the page text of the first result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result1(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(0)) + "</p></body></html>"

  /**
    * A page displays the page text of the second result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result2(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(1)) + "</p></body></html>"

  /**
    * A page displays the page text of the third result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result3(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(2)) + "</p></body></html>"

  /**
    * A page displays the page text of the fourth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result4(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(3)) + "</p></body></html>"

  /**
    * A page displays the page text of the fifth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result5(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(4)) + "</p></body></html>"

  /**
    * A page displays the page text of the sixth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result6(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(5)) + "</p></body></html>"

  /**
    * A page displays the page text of the seventh result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result7(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(6)) + "</p></body></html>"

  /**
    * A page displays the page text of the eighth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result8(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(7)) + "</p></body></html>"

  /**
    * A page displays the page text of the ninth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result9(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(8)) + "</p></body></html>"

  /**
    * A page displays the page text of the tenth result in the list
    *
    * @param inputs - a map mapping the names of inputs to the actual user
    *                 input for that form field
    * @param sessionId - the session Id associated with this instance of a page
    *
    * @return a string representing an HTML page
    */
  def result10(inputs: Map[String, String], sessionId: String): String =
    "<html><body><p>" + retrievePage(pageTitles(9)) + "</p></body></html>"

}

/**
  * An object to instantiate the search
  */
object Search {
  def main(args: Array[String]) {
    val search = new Search
  }
}