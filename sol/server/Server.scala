package guizilla.sol.server

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.Socket
import java.net.ServerSocket
import java.net.URLDecoder
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.OutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import scala.collection.immutable.HashMap
import guizilla.src.Page

/**
  * A class for a server that browsers can connect to
  */
class Server {

  /**
    * The port number the server runs off of
    */
  private val PORT = 8080

  /**
    * The server socket that the server runs
    */
  private var server: ServerSocket = new ServerSocket(PORT)

  /**
    * A HashMap mapping session IDs to page objects
    */
  private var sessionMap: HashMap[Int, Page] = new HashMap[Int, Page]

  /**
    * The socket to write and read from
    */
  private var socket: Socket = null

  /**
    * An int representing the current session ID
    */
  private var currentSessionID: Int = 0

  /**
    * A string representing the response that the server will write back to the
    * browser
    */
  private var response: String = ""

  /**
    * An Error object that contains HTML pages and HTTP headers for errors for
    * use in responses to the browser
    */
  private val e: Error = new Error

  /**
    * A content header used in every response
    */
  private val content: String = "Content-Type: text/html\n\n"

  /**
    * A string representing the request that was sent by the browser
    */
  private var request: String = ""

  /**
    * Starts an infinite while loop and listens for input from the browser,
    * parses the request, checks its validity, and calls the dispatcher to
    * generate a message to write back to the browser
    */
  private def listener {
    while (true) {
      socket = server.accept
      var bufferedReader: BufferedReader = null
      var bufferedWriter: BufferedWriter = null
      var next: Int = 0

      try {
        val inputStream = socket.getInputStream
        val outputStream = socket.getOutputStream
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))

        next = bufferedReader.read
        while (next != -1 && bufferedReader.ready) {
          request += next.toChar
          next = bufferedReader.read
        }
        request += next.toChar
        var path: String = ""
        var formData: HashMap[String, String] = new HashMap
        if (request != null && request != "") {
          val parsed: Array[String] = request.split("\r\n")
          val firstLine: Array[String] = parsed(0).split(" ")

          if (!request.startsWith("GET") && !request.startsWith("POST")) {
            response = e.h400 + content + e.notGetPost
          } else if (firstLine.size != 3 ||
            !firstLine(1).matches("""((?:\/[^\/\s]+)*\/?)""") ||
            !(firstLine(2).equals("HTTP/1.0") ||
              firstLine(2).equals("HTTP/1.1"))) {
            response = e.h505 + content + e.http
          } else if (request.startsWith("POST")) {
            var cLLine: String = request.toLowerCase
            var fDLine: String = request
            var contentLength: Int = 0

            while (!cLLine.startsWith("\r\ncontent-length: ") && cLLine != "") {
              cLLine = cLLine.drop(1)
              cLLine = cLLine.dropWhile(x => x != '\r')
            }

            while (!fDLine.startsWith("\r\n\r\n") && fDLine != "") {
              fDLine = fDLine.drop(1)
              fDLine = fDLine.dropWhile(x => x != '\r')
            }

            if (fDLine.size < 5) {
              response = e.h400 + content + e.formData
            } else if (cLLine.size < 19) {
              response = e.h400 + content + e.contentLength
            } else {
              cLLine = cLLine.drop(18)
              fDLine = fDLine.drop(4)
              try {
                contentLength = cLLine.takeWhile(p => p != '\r').toInt
                fDLine = fDLine.take(contentLength)
                if (fDLine.matches("""[^\&\s]+=[^\&\s]*(?:\&[^\&\s]+=[^\&\s]*)*""")) {
                  formData = parseFormData(fDLine)
                } else {
                  response = e.h400 + content + e.formData
                }
              } catch {
                case n: NumberFormatException =>
                  response = e.h400 + content + e.contentLength
                case p: NullPointerException =>
                  response = e.h400 + content + e.formData
                case i: IllegalArgumentException =>
                  response = e.h400 + content + e.formData
                case u: UnsupportedEncodingException =>
                  response = e.h400 + content + e.formData
              }
            }
          }

          if (firstLine.size > 1) {
            path = firstLine(1)
          }

          if (response == "") {
            dispatcher(path, formData)
          }
        } else {
          response = e.h400 + content + e.notGetPost
        }
        bufferedWriter.write(response)
        bufferedWriter.flush
        response = ""
        request = ""
      } finally {
        socket.shutdownInput()
        socket.shutdownOutput
        socket.close
        bufferedReader.close
        bufferedWriter.close
      }
    }
  }

  /**
    * A method that generates a unique session ID for pages
    *
    * @return an integer that is not contained in the session map already
    */
  def generateUniqueID: Int = {
    val start: Int = 10000000
    val randomGenerator = new scala.util.Random
    var newID: Int = start + randomGenerator.nextInt
    while (sessionMap.contains(newID)) {
      newID = start + randomGenerator.nextInt
    }
    newID
  }

  /**
    * Takes in the form data from a request and returns a hash map mapping
    * names to values -- if the form data value is empty, it does not place the
    * name in the hash map
    *
    * @param formData - a string representing the form data from a request
    *
    * @return a hash map that maps the names to values from the form data
    */
  def parseFormData(formData: String): HashMap[String, String] = {
    var parsedFormData: HashMap[String, String] =
      new HashMap[String, String]
    if (formData != "" && formData != null) {
      val nameValuePairs: Array[String] = formData.split("""\&""")
      for (pair <- nameValuePairs) {
        var decodedPair = URLDecoder.decode(pair, """UTF-8""")
        val nameValue = pair.split("=")
        if (nameValue.size == 2) {
          parsedFormData = parsedFormData.+(nameValue(0) -> nameValue(1))
        }
      }
    }
    parsedFormData
  }

  /**
    * Takes in a path and form data and uses it to construct a valid 200 OK
    * response to the browser. It also checks for 404 and 500 errors and
    * generates the appropriate responses for each.
    *
    * @param path - the path in the first line of the request from the browser
    * @param formData - a hash map mapping the form data names to values from
    *                   the request
    */
  def dispatcher(path: String, formData: HashMap[String, String]) = {
    var className: String = null
    var methodName: String = null
    var sessionID: Int = 0
    var pageObj: Page = null
    var htmlPage: String = ""

    if (path.matches("""\/([^\/\s]+)\/([^\/\s]+)""")) {
      val pattern: scala.util.matching.Regex =
        """\/([^\/\s]+)\/([^\/\s]+)""".r
      val pattern(pathClass, pathMethod) = path
      className = pathClass
      methodName = pathMethod
    } else if (path.matches("""\/([^\/\s]+)""")) {
      val pattern: scala.util.matching.Regex = """\/([^\/\s]+)""".r
      val pattern(pathClass) = path
      className = pathClass
    } else {
      response = e.h404 + content + e.classNotFound
    }

    if (className != null && response == "") {
      if (className.startsWith("id:")) {
        try {
          val id: Int = className.drop(3).toInt

          if (sessionMap.contains(id)) {
            pageObj = sessionMap(id).clone
            sessionID = generateUniqueID
            sessionMap = sessionMap.+(sessionID -> pageObj)
          } else {
            response = e.h404 + content + e.invalidID
          }
        } catch {
          case exception: NumberFormatException =>
            response = e.h404 + content + e.invalidID
        }
      } else {
        try {
          val pageClass =
            Class.forName("guizilla.sol.server.pages." + className)
          pageObj = pageClass.newInstance.asInstanceOf[Page]
          sessionID = generateUniqueID
          sessionMap = sessionMap.+(sessionID -> pageObj)
        } catch {
          case exception: ClassNotFoundException =>
            response = e.h404 + content + e.classNotFound
          case exception: IllegalAccessException =>
            response = e.h500 + content + e.illegalAccess
          case exception: InstantiationException =>
            response = e.h500 + content + e.instantiation
          case exception: ExceptionInInitializerError =>
            response = e.h500 + content + e.initializer
          case exception: SecurityException =>
            response = e.h500 + content + e.security
          case exception: ClassCastException =>
            response = e.h500 + content + e.classCast
        }
      }
    }

    if (response == "") {
      try {
        var method: java.lang.reflect.Method = null
        if (methodName != null && pageObj != null) {
          method = pageObj.getClass.getMethod(
            methodName,
            classOf[Map[String, String]], classOf[String])
        } else {
          method = pageObj.getClass.getMethod(
            "defaultHandler",
            classOf[Map[String, String]], classOf[String])
        }
        htmlPage = method.invoke(pageObj, formData, sessionID.toString).asInstanceOf[String]

        response = "HTTP/1.0 200 OK\n" + content + htmlPage
      } catch {
        case exception: NoSuchMethodException =>
          response = e.h404 + content + e.methodNotFound
        case exception: SecurityException =>
          response = e.h500 + content + e.security
      }
    }
  }
}

/**
 * Server object for the main method to run the server
 */
object Server {
  def main(args: Array[String]) {
    val server = new Server
    server.listener
  }
}