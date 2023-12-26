package scimg.gui

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos

import javafx.concurrent as jfxc

import scalafx.scene.Scene
import scalafx.scene.paint.*
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.{Button, Tooltip, Label}
import scalafx.scene.control.{Slider, ComboBox, ProgressBar}
import scalafx.scene.control.Control
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import scalafx.collections.ObservableBuffer
import scalafx.util.Duration

import scalafx.application.Platform
import scalafx.concurrent.Task

import scala.language.implicitConversions

import scimg.processing.*
import scimg.processing.commands.*

object MainWindow extends JFXApp3 {
  val imageSize = 800
  val insetSize = 20

  val windowWidth = imageSize 
  val windowHeight = imageSize + (insetSize * 3)

  // tooltip delay in milliseconds
  val customTooltipShowDelay = 100

  var chosenColor: FIFcolor = FIFcolor.Red

  var currentImage: FIFImage = _
  var imageView: ImageView = _

  var controls: Seq[Control] = Seq()
  var progressBar: ProgressBar = _

  override def start(): Unit =
    val imagePath = "/images/img6.png"  

    currentImage = importImage(imagePath)

    imageView = new ImageView(makeWriteableImage(currentImage)) {
      fitWidth = imageSize.toDouble
      fitHeight = imageSize.toDouble
      alignmentInParent = Pos.Center
    }
  
    controls = controls :+ createTextButton("Open", "Select New Image", () => {
        val fileChooser = new FileChooser {
          title = "Select Image"
          extensionFilters.addAll(
            new ExtensionFilter("Images", Seq("*.png", "*.jpg", "*.gif"))         
          )
        }
        
        val selectedFile = fileChooser.showOpenDialog(stage)
        if selectedFile != null then
          switchImage(importImage(selectedFile.toURI.getPath))
    })

    controls = controls :+ createTextButton("Pixelate", "Pixelate the image", () => {
      performImageProcessing(() => pixelateImage(currentImage))
    })

    controls = controls :+ createTextButton("Shuffle", "Shuffle the parts", () => {
      performImageProcessing(() => shuffleImage(currentImage))
    })

    controls = controls :+ createTextButton("Clockwise", "Rotate 90° Clockwise", () => {
      performImageProcessing(() => rotateImage(currentImage))
    })

    controls = controls :+ createTextButton("Anticlockwise", "Rotate 90° Counterclockwise", () => {
      performImageProcessing(() => rotateImage(currentImage, false))
    })

    progressBar = new ProgressBar {
      prefWidth = 256
      progress = 0.0
    }

    controls = controls :+ progressBar

    controls = controls :+ new Slider(0, 255, 0) {
      prefWidth = 256 / 2
      showTickLabels = true
      showTickMarks = true
      majorTickUnit = 64
      minorTickCount = 4
      blockIncrement = 1
      snapToTicks = true
      value.onChange { (_, _, newValue) =>
        performImageProcessing(() => adjustColor(currentImage, newValue.intValue, chosenColor))
      }
    }

    controls = controls :+ new ComboBox[FIFcolor] {
      items = ObservableBuffer(FIFcolor.Red, FIFcolor.Green, FIFcolor.Blue)
      value = FIFcolor.Red
      value.onChange { (_, _, newValue) =>
        chosenColor = newValue
      }
    }

    stage = new JFXApp3.PrimaryStage:
      title = "ScImg Processing"
      width = windowWidth
      height = windowHeight + insetSize
      scene = new Scene:
        root = new VBox:
            alignment = Pos.Center
            children = Seq(
                new HBox:
                    alignment = Pos.Center
                    children = Seq(imageView)
                ,
                new HBox:
                    alignment = Pos.Center
                    spacing = 10
                    children = controls
            )
      
  private def switchImage(image: FIFImage): Unit =
    currentImage = image
    imageView.image = makeWriteableImage(currentImage)

  private def createTextButton(emoji: String, tooltipText: String, action: () => Unit): Button =
    new Button { 
      text = emoji
      tooltip = new Tooltip(tooltipText) {
        showDelay = Duration(customTooltipShowDelay)
      }
      onAction = _ => action()
    }

  private def performImageProcessing(processFunction: () => FIFImage): Unit = {
    object ImageProcessingTask extends Task(new jfxc.Task[FIFImage] {
      override def call(): FIFImage = processFunction()})

    val imageProcessingTask = ImageProcessingTask.asInstanceOf[Task[FIFImage]]

    progressBar.progressProperty.bind(imageProcessingTask.progressProperty)

    imageProcessingTask.setOnSucceeded(_ => Platform.runLater(() => {
      progressBar.progressProperty.unbind()
      progressBar.progress = 0.0
      switchImage(imageProcessingTask.getValue)
    }))

    new Thread(imageProcessingTask).start()
  }
}