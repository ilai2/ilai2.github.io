package guizilla.sol.client

import javafx.scene.layout.VBox

/**
  * A form element of an HTMLPage
  *
  * @param url - The URL to send the form data
  * @param elements - The HTML elements contained in the form
  */
case class Form(val url: String, private var elements: List[HTMLElement]) extends HTMLElement {

  /**
    * A method that sets the form's elements
    *
    * @param newElements - the list of HTMLElements to set elements to
    */
  def setElements(newElements: List[HTMLElement]) {
    elements = newElements
  }

  /**
    * Gets the encoded form data, in a form ready for submission to the server
    *
    * @return a string of encoded form data
    */
  def getFormData: String = {
    var formData: String = ""
    for (element <- elements) {
      formData += element.getEncodedStuff
    }
    formData
  }

  override def render(box: VBox, browser: GUIBrowser) {
    for (ele <- elements) {
      ele.render(box, browser)
    }
  }
}