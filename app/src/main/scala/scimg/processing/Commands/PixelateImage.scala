package scimg.processing.commands

import scimg.processing.*
import scala.util.Random

def pixelateImage(image: FIFImage): FIFImage = 
  val blockSize = image.width / 64

  Array.tabulate(image.height, image.width) { (y, x) =>
    val blockStartY = (y / blockSize) * blockSize
    val blockStartX = (x / blockSize) * blockSize

    val blockEndY = math.min(blockStartY + blockSize, image.height)
    val blockEndX = math.min(blockStartX + blockSize, image.width)

    val blockWidth = blockEndX - blockStartX
    val blockHeight = blockEndY - blockStartY

    // Create a list of all pixels in the current block
    val blockPixels = for {
      blockY <- blockStartY until blockEndY
      blockX <- blockStartX until blockEndX
    } yield image(blockY)(blockX)

    // Shuffle the list of pixels
    val shuffledBlockPixels = Random.shuffle(blockPixels)

    // Calculate the position in the original image
    val originalY = blockStartY + (y % blockSize)
    val originalX = blockStartX + (x % blockSize)

    // Return the pixel to be placed in the shuffledImage
    shuffledBlockPixels(originalY % blockHeight * blockWidth + originalX % blockWidth)
  }
