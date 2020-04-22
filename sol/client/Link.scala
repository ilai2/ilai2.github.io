package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Hyperlink
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.event.ActionEvent

/**
  * A link element of an HTML page
  *
  * @param href - The URL of the link
  * @param text - The text to be rendered
  */
case class Link(href: String, text: PageText) extends HTMLElement {
  override def render(box: VBox, browser: GUIBrowser) {
    val link = new Hyperlink(text.text)

    val event = new EventHandler[ActionEvent]() {
      override def handle(a: ActionEvent) {
        browser.goToURL(href)
        browser.render
      }
    }

    link.setOnAction(event)

    box.getChildren.add(link)
  }
}
