package guizilla.sol.client

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.layout.GridPane

class RunApplication extends Application {
  override def start(stage: Stage) {
    // Loads fxml_main.fxml
    var loader = new FXMLLoader(getClass().getResource("fxml_main.fxml"))

    // Loads parent root
    var root: Parent = loader.load().asInstanceOf[GridPane]

    // Creates controller object from GUIBrowser
    var controller: GUIBrowser = loader.getController()

    // Sets the stage for GUIBrowser 
    controller.setStage(stage)

    // Customizes and shows stage
    stage.setTitle("Guizilla")
    stage.setScene(new Scene(root, 1000, 700))
    stage.show()

    controller.startUpBrowser
  }
}

object RunApplication extends App {
  javafx.application.Application.launch(classOf[RunApplication])
}
