package guizilla.sol.client

import javafx.scene.layout.VBox
import javafx.scene.control.Label

/**
  * A text element of an HTML page
  *
  * @param text - The text
  */
case class PageText(val text: String) extends HTMLElement {
  override def render(box: VBox, browser: GUIBrowser) {
    val label = new Label
    label.setText(text)
    label.setWrapText(true)
    box.getChildren.add(label)
  }
}
