package scimg.gui

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.*
import scalafx.scene.paint.Color.*
import scalafx.scene.text.Text
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Font
import scalafx.scene.control.{Button, Tooltip, Label}
import scalafx.util.Duration

import scala.language.implicitConversions

object MainWindow extends JFXApp3:
  val imageSize = 256
  val insetSize = 20

  val windowWidth = imageSize + 2 * insetSize
  val windowHeight = imageSize + 2 * insetSize

  // tooltip delay in milliseconds
  val customTooltipShowDelay = 100

  override def start(): Unit =
    val imagePath = "/images/img6.png"  
    val image = new Image(getClass.getResourceAsStream(imagePath))
    val imageView = new ImageView(image) {
      fitWidth = imageSize.toDouble
      fitHeight = imageSize.toDouble
      alignmentInParent = Pos.Center
    }
  
    val selectImageBtn = createTextButton("Open", "Select New Image", () => {
        // to do open file dialog and load image into imageView
        print("Open")
    })
    val rotateClockwiseBtn = createTextButton("Clockwise", "Rotate 90° Clockwise", () => {
        // to do rotate image 90° clockwise
        print("Clockwise")
    })
    val rotateCounterClockwiseBtn = createTextButton("Anticlockwise", "Rotate 90° Counterclockwise", () => {
       // to do rotate image 90° counterclockwise
       print("Anticlockwise")
    })

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
                    children = Seq(selectImageBtn, rotateClockwiseBtn, rotateCounterClockwiseBtn)
            )
      
  private def createTextButton(emoji: String, tooltipText: String, action: () => Unit): Button =
    new Button { 
      text = emoji
      tooltip = new Tooltip(tooltipText) {
        showDelay = Duration(customTooltipShowDelay)
      }
      onAction = _ => action()
    }
