package scimg.gui

import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.scene.control.MenuBar
import scalafx.scene.control.MenuItem
import scalafx.scene.control.Menu

import scimg.gui.MainWindow.switchImage
import scimg.processing.commands.combineImages
import scimg.processing.importImage
import scimg.processing.exportImage
import scimg.processing.FIFImage
import scalafx.scene.control.Alert
import scalafx.scene.control.ButtonType
import scalafx.scene.control.Alert.AlertType
import scimg.processing.commands.ImageCombinationOperations._
import scimg.processing.commands.resizeImage
import scalafx.scene.layout.{GridPane, HBox}
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.control.ControlIncludes.jfxDialogPane2sfx


def createFileMenu(
  getImage: () => FIFImage,
  updateImage: FIFImage => Unit
): Menu =
  new Menu("File") {
    items = Seq(
      openFileMenuItem(),
      combineFileMenuItem(getImage),
      exportImageMenuItem(getImage),
      resizeImageMenuItem(getImage)
    )
  }

def exportImageMenuItem(getImage: () => FIFImage): MenuItem =
  new MenuItem("Export") {
    onAction = _ => {
      val fileChooser = new FileChooser {
        title = "Export Image"
        extensionFilters.addAll(
          new ExtensionFilter("PNG", Seq("*.png")),
          new ExtensionFilter("JPG", Seq("*.jpg")),
          new ExtensionFilter("GIF", Seq("*.gif"))
        )
      }

      val selectedFile = fileChooser.showSaveDialog(MainWindow.stage)
      exportImage(getImage(), selectedFile.toURI.getPath)
    }
  }

def openFileMenuItem(): MenuItem =
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
        val confirmationDialog = new Alert(AlertType.Confirmation) {
          title = "Confirmation"
          headerText = "Confirm Action"
          contentText = "Do you want to override the current image?"
          buttonTypes = Seq(ButtonType.Yes, ButtonType.No)
        }

        val result = confirmationDialog.showAndWait()
        if (result.contains(ButtonType.Yes)) {
          importImage(selectedFile.toURI.getPath) match {
            case Some(image) => switchImage(image)
            case None        => println("Image not found!")
          }
        }
      }
    }
  }

def combineFileMenuItem(getImage: () => FIFImage): MenuItem =
  new MenuItem("Merge") {
    onAction = _ => {
      val fileChooser = new FileChooser {
        title = "Select Image"
        extensionFilters.addAll(
          new ExtensionFilter("Images", Seq("*.png", "*.jpg", "*.gif"))
        )
      }

      val BitWiseAndButton = new ButtonType("Bitwise And")
      val BitWiseOrButton = new ButtonType("Bitwise Or")
      val BitWiseXorButton = new ButtonType("Bitwise Xor")
      val BitWiseNotButton = new ButtonType("Bitwise Not")
      val LeftShiftButton = new ButtonType("Left Shift")
      val RightShiftButton = new ButtonType("Right Shift")

      val selectedFile = fileChooser.showOpenDialog(MainWindow.stage)
      if (selectedFile != null) {
        // show dialog on how combine with current image
        val confirmationDialog = new Alert(AlertType.Confirmation) {
          title = "Pick mode of combination"
          headerText = "Merge method?"
          contentText = "How do you want to merge the images?"
          // buttons for bitwise and, or, xor, not, left shift, right shift
          buttonTypes = Seq(
            BitWiseAndButton,
            BitWiseOrButton,
            BitWiseXorButton,
            BitWiseNotButton,
            LeftShiftButton,
            RightShiftButton
          )
        }

        val result = confirmationDialog.showAndWait()
        val image = importImage(selectedFile.toURI.getPath)

        // if user selects bitwise and
        if (result.contains(BitWiseAndButton)) {
          switchImage(combineImages(getImage(), image.get, BitwiseAnd))
        } else if (result.contains(BitWiseOrButton)) {
          switchImage(combineImages(getImage(), image.get, BitwiseOr))
        } else if (result.contains(BitWiseXorButton)) {
          switchImage(combineImages(getImage(), image.get, BitwiseXor))
        } else if (result.contains(BitWiseNotButton)) {
          switchImage(combineImages(getImage(), image.get, BitwiseNot))
        } else if (result.contains(LeftShiftButton)) {
          switchImage(combineImages(getImage(), image.get, LeftShift))
        } else if (result.contains(RightShiftButton)) {
          switchImage(combineImages(getImage(), image.get, RightShift))
        }
      }
    }
  }

def resizeImageMenuItem(getImage: () => FIFImage): MenuItem =
  new MenuItem("Resize") {
    onAction = _ => {
      // let the user set the new dimensions in one dialog
      val grid = new GridPane {
        hgap = 10
        vgap = 10
        padding = Insets(10, 10, 10, 10)

        add(new Label("Width:"), 0, 0)
        add(new Label("Height:"), 0, 1)
        add(new TextField, 1, 0)
        add(new TextField, 1, 1)
      }

      val confirmationDialog = new Alert(AlertType.Confirmation) {
        title = "Resize Image"
        headerText = "Set new dimensions"
        contentText = "Enter the new dimensions for the image"
        dialogPane().content = grid
        buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      }

      val result = confirmationDialog.showAndWait()

      if (result.contains(ButtonType.OK)) {
        val width = grid.children(2).asInstanceOf[TextField].text.value.toInt
        val height = grid.children(3).asInstanceOf[TextField].text.value.toInt
        switchImage(resizeImage(getImage(), (width, height)))
      }
    }
  }