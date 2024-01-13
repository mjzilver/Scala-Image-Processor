package scimg.gui

import scalafx.scene.control.Menu
import scalafx.scene.control.MenuItem
import scimg.processing.FIFImage
import scimg.processing.commands._
import scalafx.concurrent.Task
import scala.util.Success
import scala.util.Failure

def createEffectsMenu(
    getImage: () => FIFImage,
    imageProcessingFunction: (() => scimg.processing.FIFImage) => Task[scimg.processing.FIFImage],
    updateImage: FIFImage => Unit
): Menu = {
  new Menu("Effects") {
    items = Seq(
      new MenuItem("Pixelate") {
        onAction = _ => {
          val task = imageProcessingFunction(() => pixelateImage(getImage()))
          task.onSucceeded = _ => updateImage(task.value.value)
        }
      },
      new MenuItem("Shuffle") {
        onAction = _ => {
          val task = imageProcessingFunction(() => shuffleImage(getImage()))
          task.onSucceeded = _ => updateImage(task.value.value)
        }
      },
      new MenuItem("Clockwise") {
        onAction = _ => {
          val task = imageProcessingFunction(() => rotateImage(getImage(), true))
          task.onSucceeded = _ => updateImage(task.value.value)
        }
      },
      new MenuItem("Anticlockwise") {
        onAction = _ => {
          val task = imageProcessingFunction(() => rotateImage(getImage(), false))
          task.onSucceeded = _ => updateImage(task.value.value)
        }
      }
    )
  }
}
