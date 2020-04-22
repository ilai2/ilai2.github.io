package guizilla.sol.client

import guizilla.src.parser.HTMLParser
import guizilla.src.HTMLTokenizer
import guizilla.src.parser.ParseException
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.stage.Stage

/**
  * An element of a HTML Page
  */
abstract class HTMLElement {
  /**
   * Converts the HTML element
   */
  def render(box: VBox, browser: GUIBrowser)

  /**
    * Returns an encoded string version of the data to be sent as form data
    *
    * @return the encoded string of data
    */
  def getEncodedStuff: String = ""
}