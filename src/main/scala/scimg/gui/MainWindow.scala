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
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.control.*
import scalafx.scene.input.MouseEvent
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import scalafx.collections.ObservableBuffer
import scalafx.util.Duration

import scalafx.application.Platform
import scalafx.concurrent.Task

import scala.language.implicitConversions

import scimg.processing.*
import scimg.processing.commands.*
import scalafx.Includes.jfxMouseEvent2sfx
import scimg.gui.FileMenu.createOpenMenuItem
import scimg.gui.performImageProcessing
import scimg.gui.createBrushMenu

object MainWindow extends JFXApp3 {
  val imageSize = 600

  val windowWidth = imageSize

  // tooltip delay in milliseconds
  val customTooltipShowDelay = 100

  var chosenColor: FIFcolor = FIFcolor.Red
  var currentBrush: Brush = Brush(10, (0, 0, 0))

  var currentImage: FIFImage = Array.fill[FIFPixel](imageSize, imageSize)((255, 255, 255))
  var imageView: ImageView = _

  var controls: Seq[Control] = Seq()
  var progressBar: ProgressBar = _
  var brushSizeSlider: Slider = _
  var redSlider: Slider = _
  var greenSlider: Slider = _
  var blueSlider: Slider = _

  override def start(): Unit = {
    val imagePath = "/images/img6.png"
    importImage(imagePath) match {
      case Some(image) =>
        currentImage = image
      case None =>
        println("Image not found!")
    }

    imageView = new ImageView(makeWriteableImage(currentImage)) {
      fitWidth = imageSize.toDouble
      fitHeight = imageSize.toDouble
      alignmentInParent = Pos.Center
      onMouseDragged = mouseBrushEvent
      onMousePressed = mouseBrushEvent
    }

    progressBar = new ProgressBar {
      prefWidth = 256
      progress = 0.0
    }

    val imageProcessingFunction = performImageProcessing(_, progressBar)

    val effectsMenu = createEffectsMenu(() => currentImage, imageProcessingFunction, switchImage)

    val brushMenu = createBrushMenu(() => currentBrush, (newBrush: Brush) => {
      currentBrush = newBrush
    })

    val fileMenu = new Menu("File") {
      items = Seq(createOpenMenuItem())
    }

    val menuBar = new MenuBar {
      menus = Seq(fileMenu, brushMenu, effectsMenu)
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
        imageProcessingFunction(() => adjustColor(currentImage, newValue.intValue, chosenColor))
      }
    }

    controls = controls :+ new ComboBox[FIFcolor] {
      items = ObservableBuffer(FIFcolor.Red, FIFcolor.Green, FIFcolor.Blue)
      value = FIFcolor.Red
      value.onChange { (_, _, newValue) =>
        chosenColor = newValue
      }
    }

    stage = new JFXApp3.PrimaryStage {
      title = "ScImg Processing"
      width = windowWidth
      scene = new Scene {
        stylesheets = Seq("/styles.css")
        root = new VBox {
          alignment = Pos.TopCenter
          children = Seq(
            menuBar,
            new HBox {
              alignment = Pos.TopCenter
              hgrow = Priority.Always
              children = Seq(imageView)
            },
            new HBox {
              alignment = Pos.Center
              padding = Insets(10)
              spacing = 10
              hgrow = Priority.Always
              children = controls
            }
          )
        }
      }
    }
  }

  def switchImage(image: FIFImage): Unit = {
    currentImage = image
    imageView.image = makeWriteableImage(currentImage)
  }

  def mouseBrushEvent(event: MouseEvent): Unit = {
    val x = event.x.toInt
    val y = event.y.toInt

    // convert to image coordinates
    val imageX = x * currentImage.head.length / imageSize
    val imageY = y * currentImage.length / imageSize

    performImageProcessing(() => paintWithBrush(currentImage, currentBrush, imageX, imageY), progressBar)
  }

  def createTextButton(emoji: String, tooltipText: String, action: () => Unit): Button =
    new Button {
      text = emoji
      tooltip = new Tooltip(tooltipText) {
        showDelay = Duration(customTooltipShowDelay)
      }
      onAction = _ => action()
    }
}
