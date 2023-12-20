package scimg.gui

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos

import scalafx.scene.Scene
import scalafx.scene.paint.*
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight, Text}
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.{Button, Tooltip, Label}
import scalafx.scene.control.{Slider, ComboBox}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import scalafx.collections.ObservableBuffer
import scalafx.util.Duration

import scala.language.implicitConversions

import scimg.processing.*

object MainWindow extends JFXApp3:
  val imageSize = 516
  val insetSize = 20

  val windowWidth = imageSize + 2 * insetSize
  val windowHeight = imageSize + 2 * insetSize

  // tooltip delay in milliseconds
  val customTooltipShowDelay = 100

  var chosenColor: FIFcolor = FIFcolor.Red

  var currentImage: FIFImage = _

  override def start(): Unit =
    val imagePath = "/images/img1.png"  

    // Your custom image loading and conversion functions
    currentImage = importImage(imagePath)

    val imageView = new ImageView(makeWriteableImage(currentImage)) {
      fitWidth = imageSize.toDouble
      fitHeight = imageSize.toDouble
      alignmentInParent = Pos.Center
    }
  
    val selectImageBtn = createTextButton("Open", "Select New Image", () => {
        val fileChooser = new FileChooser {
          title = "Select Image"
          extensionFilters.addAll(
            new ExtensionFilter("Images", Seq("*.png", "*.jpg", "*.gif"))         
          )
        }
        
        val selectedFile = fileChooser.showOpenDialog(stage)
        if selectedFile != null then
          currentImage = importImage(selectedFile.toURI.getPath)
          imageView.image = makeWriteableImage(currentImage)
    })

    val shuffleBtn = createTextButton("Shuffle", "Shuffle the parts", () => {
        currentImage = shuffleImage(currentImage)
        imageView.image = makeWriteableImage(currentImage)
    })

    val rotateClockwiseBtn = createTextButton("Clockwise", "Rotate 90° Clockwise", () => {
        currentImage = rotateImage(currentImage)
        imageView.image = makeWriteableImage(currentImage)
    })

    val rotateCounterClockwiseBtn = createTextButton("Anticlockwise", "Rotate 90° Counterclockwise", () => {
        currentImage = rotateImage(currentImage, false)
        imageView.image = makeWriteableImage(currentImage)
    })

    val colorAdjustmentSlider = new Slider(0, 255, 0) {
      prefWidth = 256
      showTickLabels = true
      showTickMarks = true
      majorTickUnit = 64
      minorTickCount = 4
      blockIncrement = 1
      snapToTicks = true
      value.onChange { (_, _, newValue) =>
        currentImage = scimg.processing.adjustColor(currentImage, newValue.intValue, chosenColor)
        imageView.image = makeWriteableImage(currentImage)
      }
    }

    val colorSelectionComboBox = new ComboBox[FIFcolor] {
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
            padding = Insets(insetSize)
            children = Seq(
                new HBox:
                    alignment = Pos.Center
                    children = Seq(imageView)
                ,
                new HBox:
                    alignment = Pos.Center
                    spacing = 10
                    children = Seq(selectImageBtn, shuffleBtn, rotateClockwiseBtn, rotateCounterClockwiseBtn, colorAdjustmentSlider, colorSelectionComboBox)
            )
      
  private def createTextButton(emoji: String, tooltipText: String, action: () => Unit): Button =
    new Button { 
      text = emoji
      tooltip = new Tooltip(tooltipText) {
        showDelay = Duration(customTooltipShowDelay)
      }
      onAction = _ => action()
    }
