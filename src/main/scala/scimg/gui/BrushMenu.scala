package scimg.gui

import scalafx.scene.control.Menu
import scalafx.scene.control.CustomMenuItem
import scalafx.scene.control.Slider
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.scene.text.Font
import scalafx.scene.text.FontWeight

import scimg.processing.commands.Brush

def createBrushMenu(getBrush: () => Brush, mainWindowsCurrentBrush: Brush => Unit): Menu = {
  var currentBrush: Brush = getBrush()

  new Menu("Brush") {
    items = Seq(
      new Menu("Brush Size") {
        items = Seq(
          new CustomMenuItem {
            content = new Slider(1, 50, currentBrush.size) {
              showTickLabels = true
              showTickMarks = true
              majorTickUnit = 10
              minorTickCount = 1
              blockIncrement = 1
              snapToTicks = true
              hideOnClick = false
              value.onChange { (_, _, newValue) =>
                currentBrush = Brush(newValue.intValue, currentBrush.color)
                mainWindowsCurrentBrush(currentBrush)
              }
            }
          }
        )
      },
      new Menu("RGB Color") {
        items = Seq(
          new CustomMenuItem {
            hideOnClick = false
            content = createColorSlider(getBrush, "Red", currentBrush.color._1, mainWindowsCurrentBrush)
          },
          new CustomMenuItem {
            hideOnClick = false
            content = createColorSlider(getBrush, "Green", currentBrush.color._2, mainWindowsCurrentBrush)
          },
          new CustomMenuItem {
            hideOnClick = false
            content = createColorSlider(getBrush, "Blue", currentBrush.color._3, mainWindowsCurrentBrush)
          }
        )
      }
    )
  }
}

def createColorSlider(
  getBrush: () => Brush,
  colorName: String,
  initialValue: Int,
  mainWindowsCurrentBrush: Brush => Unit
): VBox = new VBox {
  val label = new Label(colorName) {
    font = Font.font(null, FontWeight.Bold, 12)
  }

  val slider = new Slider(0, 255, initialValue) {
    prefWidth = 256 / 3
    showTickLabels = true
    showTickMarks = true
    majorTickUnit = 64
    minorTickCount = 4
    blockIncrement = 1
    snapToTicks = true

    value.onChange { (_, _, newValue) =>
      var currentBrush: Brush = getBrush()

      val updatedColor = colorName match {
        case "Red"   => (newValue.intValue, currentBrush.color._2, currentBrush.color._3)
        case "Green" => (currentBrush.color._1, newValue.intValue, currentBrush.color._3)
        case "Blue"  => (currentBrush.color._1, currentBrush.color._2, newValue.intValue)
      }
      val updatedBrush = Brush(currentBrush.size, updatedColor)
      mainWindowsCurrentBrush(updatedBrush)
    }
  }

  children = Seq(label, slider)
}
