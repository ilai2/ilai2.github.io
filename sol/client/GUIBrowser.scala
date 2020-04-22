package guizilla.sol.client

import java.io.IOException
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Stack
import scala.util.matching.Regex

import guizilla.src.parser.HTMLParser
import guizilla.src.HTMLTokenizer
import guizilla.src.parser.ParseException
import guizilla.src.LexicalException
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.stage.Stage

/**
  * Class responsible for handling browser navigation.
  * TODO: extend Browser class that was written for Sparkzilla, i.e.
  * where your code dealt with networking and communicating with server.
  */
class GUIBrowser {

  @FXML protected var gPane: GridPane = null
  @FXML protected var urlBar: TextField = null
  @FXML var box: VBox = null

  private var stage: Stage = null
  private var urlText: String = null

  /**
    * A list of HTMLElements representing the current page that has loaded
    */
  private var page: List[HTMLElement] = List()

  /**
    * A list of HTML Elements representing the start up page of the browser
    */
  private val startUp: List[HTMLElement] = List(new PageText("Welcome to GUIzilla!"))

  /**
    * A stack of HTMLElement lists representing the pages that the user has
    * loaded in the browser
    */
  private val cache: Stack[List[HTMLElement]] = Stack()

  /**
    * A Stack of Strings representing the URLs that the user has connected to
    * in the browser
    */
  private val urlCache: Stack[Option[String]] = Stack(None)

  /**
    * A Stack of Strings representing the hosts that the user has connected to
    * in the browser
    */
  private val hostCache: Stack[String] = Stack()

  /**
    * A string representing the current host that the socket is connected to
    */
  private var currentHost: String = null

  /**
    * A constant port number to connect to
    */
  private val PORT: Int = 8080

  /**
    * The current socket that the browser is connected to
    */
  private var currentSocket: Socket = null

  /**
    * Regex that matches to URLs
    */
  private val urlRegex: Regex =
    new Regex("""http:\/\/([^\/\s]+)((?:\/[^\/\s]+)*\/?)|((?:\/[^\/\s]+)*\/?)""")

  /**
    * Parses the input from the server into a list of HTMLElements.
    *
    * @param inputFromServer- BufferedReader containing HTML from server
    * @return hierarchical list of the HTMLElements. See the documentation
    *   and view the sol code for the specific composition of each HTMLElement
    *   within the list.
    */
  private def getHTMLElementList(inputFromServer: BufferedReader): List[HTMLElement] = {
    val parser = new HTMLParser(new HTMLTokenizer(inputFromServer))
    return parser.parse().toHTML
  }

  /**
    * Sets the page to the inputted one and changes the URL bar text
    */
  private def load(aPage: List[HTMLElement]): Unit = {
    page = aPage
    urlBar.setText(urlText)
  }

  /**
    * Given a host to connect to, closes the currentSocket and generates a new
    * one with the given host.
    *
    * @param host - the host to connect to
    */
  private def setHost(host: String) {
    try {
      if (currentSocket != null &&
        !currentSocket.isClosed) {
        currentSocket.shutdownInput
        currentSocket.close
      }

      currentSocket = new Socket(host, PORT)
      currentHost = host
    } catch {
      case i: IOException =>
        urlBar.clear
        page = List[HTMLElement](new PageText("Error communicating with server: " + host))
        urlCache.push(None)
        load(page)
      case d: IllegalArgumentException =>
        urlBar.clear
        page = List[HTMLElement](new PageText("Error communicating with server: " + host))
        urlCache.push(None)
        load(page)
    }
  }

  /**
    * Given a URL as input, this method parses and then generates the
    * appropriate GET request and submits it.
    *
    * @param url - the URL to go to
    */
  def goToURL(url: String) {
    var path: String = null
    println(s"going to: $url\n--------------------------")
    url match {
      case urlRegex(hst, pth, lpath) =>
        if (hst != null) {
          path = pth
          setHost(hst)
          urlText = url
        } else {
          urlText = s"http://$currentHost$lpath"
        }
        currentHost match {
          case null =>
            println("Local host could not be found")
            urlCache.push(None)
            urlBar.clear
            page = List[HTMLElement](new PageText("Error communicating with server"))
            load(page)
          case _ =>
            if (lpath != null) {
              path = lpath
              setHost(currentHost)
            }
            if (!currentSocket.isClosed) {
              val reader: BufferedReader =
                new BufferedReader(new InputStreamReader(
                  currentSocket.getInputStream))
              val writer: BufferedWriter =
                new BufferedWriter(new OutputStreamWriter(
                  currentSocket.getOutputStream))
              var request: String = s"GET $path HTTP/1.0\r\nConnection: close\r\n"
              request += "User-Agent: Guizilla/1.0\r\n"
              println(s"submitting request...\n--------------------------")
              submitRequest(reader, writer, request)
            } else {
              println(s"The current socket is closed: $currentSocket")
            }
        }
      case _ =>
        println("Invalid URL: " + url)
    }
  }

  /**
    * This method submits requests to the server, reads the response, calls
    * the parser and then loads the page appropriately for the response. It
    * takes in a BufferedReader and Writer to write and read to and from the
    * server, and a request as a string to send.
    *
    * @param bRead - a buffered reader to read the server response
    * @param bWrite - a buffered writer to write the request to the server
    * @param request - a GET or POST request to send to the server
    */
  private def submitRequest(
      bRead:   BufferedReader,
      bWrite:  BufferedWriter,
      request: String) {
    urlCache.push(Some(urlText))
    try {
      bWrite.write(request)
      bWrite.flush
      currentSocket.shutdownOutput()
      var serverResponse: String = bRead.readLine()
      while (serverResponse != null &&
        serverResponse != "" &&
        !serverResponse.startsWith("<html>")) {
        serverResponse = bRead.readLine()
      }
      page = getHTMLElementList(bRead)
      load(page)
    } catch {
      case p: ParseException =>
        println("Parser Error! " + p.getMessage)
        urlBar.clear
        urlCache.push(None)
        page = List[HTMLElement](new PageText("Parser Error! " + p.getMessage))
        render
      case l: LexicalException =>
        println("Lexical Exception! " + l.getMessage)
        urlBar.clear
        urlCache.push(None)
        page = List[HTMLElement](new PageText("Lexical Exception! " + l.getMessage))
        render
    } finally {
      try {
        bRead.close
        bWrite.close
        if (currentSocket != null &&
          !currentSocket.isClosed()) {
          currentSocket.shutdownInput()
          currentSocket.close()
        }
      } catch {
        case e: IOException =>
          println("IO Exception! Yikes! " + e.getMessage)
          urlCache.push(None)
          page = List[HTMLElement](new PageText("IO Exception! Yikes! " + e.getMessage))
          urlBar.clear
          render
      }
    }
  }

  /**
    * Given a url and a request, sets the URL text to the URL and submits the
    * request by calling submitRequest
    *
    * @param url - the URL to post the request to
    * @param req - the request to send
    */
  def submit(url: String, req: String) =
    url match {
      case urlRegex(hst, pth, lpath) =>
        if (hst != null) {
          setHost(hst)
          urlText = url
        } else if (currentHost != null) {
          setHost(currentHost)
          urlText = s"http://$currentHost$lpath"
        }
        if (currentSocket != null &&
          !currentSocket.isClosed) {
          val reader: BufferedReader =
            new BufferedReader(new InputStreamReader(currentSocket.getInputStream))
          val writer: BufferedWriter =
            new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream))
          submitRequest(reader, writer, req)
        }
    }

  /**
    * Handles the pressing of the submit button on the main GUI page.
    */
  @FXML def handleQuitButtonAction(event: ActionEvent) {
    stage.close()
  }

  /**
    * Handles the pressing of the back button on the main GUI page.
    */
  @FXML def handleBackButtonAction(event: ActionEvent) {
    if (cache.size <= 1 || urlCache.size <= 1 || hostCache.size <= 1) {
      urlBar.clear
      startUpBrowser
    } else {
      cache.pop
      page = cache.pop
      urlCache.pop
      urlCache.pop match {
        case None =>
          urlBar.clear
          urlCache.push(None)
        case Some(x) =>
          urlText = x
          urlBar.setText(urlText)
          urlCache.push(Some(x))
      }
      hostCache.pop
      currentHost = hostCache.pop
      render
    }
  }

  /**
    * Handles submitting URL button action.
    */
  @FXML def handleSubmitButtonAction(event: ActionEvent) {
    urlText = urlBar.getText
    urlBar.setText(urlText)
    if (urlText.startsWith("http://")) {
      if (!urlText.drop(7).contains("/")) {
        urlText = urlText + "/"
      }
      goToURL(urlText)
      render
    } else if (urlText.startsWith("/")) {
      goToURL(urlText)
      render
    } else {
      println("Invalid URL :(")
    }
  }

  /**
    * Sets the stage field of the controller to the given stage.
    *
    * @param stage The stage
    */
  def setStage(stage: Stage) {
    this.stage = stage
  }

  /**
    * Renders each element in the page and adds it to the VBox and shows the
    * stage at the end
    */
  def render {
    val aPage: List[HTMLElement] = List() ++ page
    cache.push(aPage)
    hostCache.push(currentHost)
    box.getChildren.clear()
    for (ele <- aPage) {
      ele.render(box, this)
    }
    stage.show
  }

  /**
    * The method called to start up the browser. It renders the welcome page
    */
  def startUpBrowser {
    page = startUp
    render
  }
}

