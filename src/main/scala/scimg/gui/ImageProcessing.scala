package scimg.gui

import javafx.concurrent as jfxc

import scalafx.application.Platform
import scalafx.concurrent.Task

import scala.language.implicitConversions

import scimg.processing.*
import scalafx.scene.control.ProgressBar
import scimg.gui.MainWindow.switchImage

def performImageProcessing(processFunction: () => FIFImage, progressBar: Option[ProgressBar] = None): Unit = {
  val imageProcessingTask = new jfxc.Task[FIFImage] {
    override def call(): FIFImage = processFunction()
  }

  progressBar.foreach { pb =>
    pb.progressProperty.bind(imageProcessingTask.progressProperty)
  }

  imageProcessingTask.setOnSucceeded(_ =>
    Platform.runLater { () =>
      progressBar.foreach { pb =>
        pb.progressProperty.unbind()
        pb.progress = 0.0
      }
      switchImage(imageProcessingTask.getValue)
    }
  )

  new Thread(imageProcessingTask).start()
}
