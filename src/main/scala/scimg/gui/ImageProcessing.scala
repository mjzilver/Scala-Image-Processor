package scimg.gui

import scalafx.concurrent.Task
import scalafx.application.Platform
import scalafx.scene.control.ProgressBar

import javafx.concurrent as jfxc

import scala.language.implicitConversions

import scimg.processing.*
import scimg.gui.MainWindow.switchImage

import scalafx.Includes.jfxTask2sfxTask // convert javafx.concurrent.Task to scalafx.concurrent.Task

def performImageProcessing(processFunction: () => FIFImage, progressBar: ProgressBar): Task[FIFImage] = {
  val imageProcessingTask = new jfxc.Task[FIFImage] {
    override def call(): FIFImage = processFunction()
  }

  progressBar.progressProperty.bind(imageProcessingTask.progressProperty)
  
  imageProcessingTask.setOnSucceeded(_ =>
    Platform.runLater { () =>
      progressBar.progressProperty.unbind()
      progressBar.progress = 0
      switchImage(imageProcessingTask.getValue)
    }
  )

  new Thread(imageProcessingTask).start()
  imageProcessingTask
}
