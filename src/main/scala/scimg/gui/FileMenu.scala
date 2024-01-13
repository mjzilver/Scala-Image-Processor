package scimg.gui

import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.control.Menu

import scimg.gui.MainWindow.{switchImage}
import scimg.processing.*

object FileMenu {
  def createOpenMenuItem(): MenuItem = {
    new MenuItem("Open") {
      onAction = _ => {
        val fileChooser = new FileChooser {
          title = "Select Image"
          extensionFilters.addAll(
            new ExtensionFilter("Images", Seq("*.png", "*.jpg", "*.gif"))
          )
        }

        val selectedFile = fileChooser.showOpenDialog(MainWindow.stage)
        if (selectedFile != null) {
          importImage(selectedFile.toURI.getPath) match {
            case Some(image) => switchImage(image)
            case None        => println("Image not found!")
          }
        }
      }
    }
  }
}
