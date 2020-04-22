package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.TextField
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.event.ActionEvent
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.net.URLEncoder

/**
  * A text input element of an HTML page
  *
  * @param name - Name of the text input for communicating with the server
  * @param value - Value of the text input as given by the user
  */
case class TextInput(val name: String, private var value: Option[String]) extends HTMLElement {
  override def render(box: VBox, browser: GUIBrowser) {
    val field = new TextField()

    field.setPromptText(s"Enter $name here...")

    val listener = new ChangeListener[String]() {
      override def changed(ov: ObservableValue[_ <: String],
                           oldValue: String, newValue: String) {
        value = Some(newValue)
      }
    }

    field.textProperty.addListener(listener)

    box.getChildren.add(field)
  }

  override def getEncodedStuff: String = {
    val encodedName = URLEncoder.encode(name, "UTF-8")
    val encodedValue =
      value match {
        case None    => ""
        case Some(x) => URLEncoder.encode(x, "UTF-8")
      }

    s"$encodedName=$encodedValue&"
  }
}