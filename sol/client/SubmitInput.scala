package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.event.ActionEvent
import scala.util.matching.Regex
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.BufferedWriter
import java.io.OutputStreamWriter

/**
  * A submit button for a form
  *
  * @param form - The form that contains this submit button
  */
case class SubmitInput(form: Form) extends HTMLElement {

  override def render(box: VBox, browser: GUIBrowser) {
    val button = new Button("Submit")

    val event = new EventHandler[ActionEvent]() {
      override def handle(a: ActionEvent) {
        val urlRegex: Regex = new Regex("""http:\/\/\S+(\/\S+)""")
        val localRegex: Regex = new Regex("""(\/\S+)""")
        var path: String = null

        form.url match {
          case urlRegex(pth) =>
            path = pth
          case localRegex(pth) =>
            path = pth
        }

        val formData: String = form.getFormData.dropRight(1)
        var request: String = s"POST $path HTTP/1.0\r\n"
        request += "Connection: close\r\n"
        request += "User-Agent: Guizilla/1.0\r\n"
        request += "Content-Type: application/x-www-form-urlencoded\r\n"
        request += "Content-Length: " + formData.length + "\r\n"
        request += "\r\n"
        request += formData
        println(request)
        browser.submit(form.url, request)
        browser.render
      }
    }

    button.setOnAction(event)

    box.getChildren.add(button)
  }
}