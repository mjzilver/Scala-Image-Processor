package scimg.processing.commands

import scimg.processing.*

def rotateImage(image: FIFImage, clockwise: Boolean = true): FIFImage = 
  val rotatedImage = Array.tabulate(image.width, image.height) { (x, y) =>
    if (clockwise) {
      image(image.height - y - 1)(x)
    } else {
      image(y)(image.width - x - 1)
    }
  }

  rotatedImage
