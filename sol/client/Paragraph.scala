package guizilla.sol.client

import javafx.scene.layout.VBox

/**
  * A paragraph element of an HTML page
  *
  * @param elements - The HTML elements of the paragraph
  */
case class Paragraph(elements: List[HTMLElement]) extends HTMLElement {
  override def render(box: VBox, browser: GUIBrowser) {
    for (ele <- elements) {
      ele.render(box, browser)
    }
  }
}
