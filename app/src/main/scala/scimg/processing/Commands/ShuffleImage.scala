package scimg.processing.commands

import scimg.processing.*
import scala.util.Random

def shuffleImage(image: FIFImage): FIFImage =
  // calculate the block size
  val blockSize = image.width / 8

  // slice the image into blocks of blockSize x blockSize
  val blocks = (for {
    blockStartY <- 0 until image.height by blockSize
    blockStartX <- 0 until image.width by blockSize
  } yield {
    val blockEndY = math.min(blockStartY + blockSize, image.height)
    val blockEndX = math.min(blockStartX + blockSize, image.width)

    image.slice(blockStartY, blockEndY).map(_.slice(blockStartX, blockEndX))
  })

  // shuffle the blocks 
  val shuffledBlocks = Random.shuffle(blocks)

  // assign the shuffled blocks to the image
  Array.tabulate(image.height, image.width) { (y, x) =>
    val blockStartY = y / blockSize
    val blockStartX = x / blockSize

    val blockEndY = math.min(blockStartY + blockSize, image.height)
    val blockEndX = math.min(blockStartX + blockSize, image.width)

    val blockWidth = blockEndX - blockStartX
    val blockHeight = blockEndY - blockStartY

    val block = shuffledBlocks(blockStartY * (image.width / blockSize) + blockStartX)

    block(y % blockHeight)(x % blockWidth)
  }