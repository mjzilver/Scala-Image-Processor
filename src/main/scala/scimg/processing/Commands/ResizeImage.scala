package scimg.processing.commands

import scimg.processing.*
import scimg.processing.FIFImage
import scimg.processing.FIFPixel
import scimg.processing.newFIFImage

def resizeImage(image: FIFImage, size: (Int, Int) ): FIFImage = {
    val (width, height) = size
    val newImage =  newFIFImage(width, height)
    for (x <- 0 until width; y <- 0 until height) {
        val (oldX, oldY) = (x * image.width / width, y * image.height / height)
        newImage(y)(x) = image(oldY)(oldX)
    }
    newImage
}

def resizeToSameDimensions(image1: FIFImage, image2: FIFImage): (FIFImage, FIFImage) = {
  val maxSize = (math.max(image1.width, image2.width), math.max(image1.height, image2.height))
  val resizedImage1 = resizeImage(image1, maxSize)
  val resizedImage2 = resizeImage(image2, maxSize)
  (resizedImage1, resizedImage2)
}