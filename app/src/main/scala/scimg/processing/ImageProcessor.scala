package scimg.processing

import scalafx.scene.image.{Image, ImageView, WritableImage, PixelWriter}
import javax.imageio.ImageIO
import scala.compiletime.ops.boolean
import scala.util.Random
import java.io.File

// FIF stands for Functional Image Format
// my own image format for image processing in Scala 
// (so it doesnt conflict with JavaFX Image class)
type FIFPixel = (Int, Int, Int)
type FIFImage = Array[Array[FIFPixel]]

enum FIFcolor:
  case Red, Green, Blue

def importImage(imagePath: String): FIFImage =
  val file = new File(imagePath)
  val image = if (file.exists()) 
    ImageIO.read(file)
  else 
    ImageIO.read(getClass.getResourceAsStream(imagePath))

  val width = image.getWidth
  val height = image.getHeight
  val pixels = Array.ofDim[FIFPixel](height, width)
  for
    y <- 0 until height
    x <- 0 until width
  do
    val rgb = image.getRGB(x, y)
    val red = (rgb >> 16) & 0xFF
    val green = (rgb >> 8) & 0xFF
    val blue = rgb & 0xFF
    pixels(y)(x) = (red, green, blue)
  pixels

def makeWriteableImage(image: FIFImage): WritableImage =
  val width = image(0).length
  val height = image.length
  val writableImage = new WritableImage(width, height)
  val pixelWriter = writableImage.getPixelWriter
  for
    y <- 0 until height
    x <- 0 until width
  do
    val (red, green, blue) = image(y)(x)
    val argb = (0xFF << 24) | (red << 16) | (green << 8) | blue
    pixelWriter.setArgb(x, y, argb)
  writableImage

def rotateImage(image: FIFImage, clockwise: Boolean = true): FIFImage =
  val width = image(0).length
  val height = image.length
  val rotatedImage = Array.ofDim[FIFPixel](width, height)
  for
    y <- 0 until height
    x <- 0 until width
  do
    val (red, green, blue) = image(y)(x)
    if clockwise then
      rotatedImage(x)(height - y - 1) = (red, green, blue)
    else
      rotatedImage(width - x - 1)(y) = (red, green, blue)
  rotatedImage

def pixelateImage(image: FIFImage, blockSize: Int = 10): FIFImage =
  val width = image(0).length
  val height = image.length
  val shuffledImage = Array.ofDim[FIFPixel](height, width)

  for
    blockStartY <- 0 until height by blockSize
    blockStartX <- 0 until width by blockSize
  do
    val blockEndY = math.min(blockStartY + blockSize, height)
    val blockEndX = math.min(blockStartX + blockSize, width)

    val blockWidth = blockEndX - blockStartX
    val blockHeight = blockEndY - blockStartY

    // Create a list of all pixels in the current block
    val blockPixels = for
      y <- blockStartY until blockEndY
      x <- blockStartX until blockEndX
    yield image(y)(x)

    // Shuffle the list of pixels
    val shuffledBlockPixels = Random.shuffle(blockPixels)

    // Assign the shuffled pixels back to the shuffledImage
    for
      (pixel, i) <- shuffledBlockPixels.zipWithIndex
      y = blockStartY + i / blockWidth
      x = blockStartX + i % blockWidth
    do
      shuffledImage(y)(x) = pixel

  shuffledImage

def shuffleImage(image: FIFImage): FIFImage =
  val width = image(0).length
  val height = image.length
  val shuffledImage = Array.ofDim[FIFPixel](height, width)

  // calculate the block size
  val blockSize = width / 8

  // create a list of all blocks
  var blocks: Array[Array[FIFImage]] = Array.ofDim(height / blockSize, width / blockSize)

  // fill the list with the blocks
  for
    blockStartY <- 0 until height by blockSize
    blockStartX <- 0 until width by blockSize
  do
    val blockEndY = math.min(blockStartY + blockSize, height)
    val blockEndX = math.min(blockStartX + blockSize, width)

    val blockWidth = blockEndX - blockStartX
    val blockHeight = blockEndY - blockStartY

    val block = Array.ofDim[FIFPixel](blockHeight, blockWidth)

    for
      y <- blockStartY until blockEndY
      x <- blockStartX until blockEndX
    do
      block(y - blockStartY)(x - blockStartX) = image(y)(x)
    
    blocks(blockStartY / blockSize)(blockStartX / blockSize) = block

  // put the 4d array into a 2d array
  val shuffledBlocks = Random.shuffle(blocks.flatten)

  // assign the shuffled blocks back to the shuffledImage
  for
    (block, i) <- shuffledBlocks.zipWithIndex 
    blockStartY = i / (width / blockSize) * blockSize
    blockStartX = i % (width / blockSize) * blockSize
  do
    for
      y <- blockStartY until blockStartY + blockSize
      x <- blockStartX until blockStartX + blockSize
    do
      shuffledImage(y)(x) = block(y - blockStartY)(x - blockStartX)
    
  shuffledImage

def adjustColor(image: FIFImage, adjustment: Int, chosenColor: FIFcolor = FIFcolor.Red): FIFImage =
  val width = image(0).length
  val height = image.length
  val adjustedImage = Array.ofDim[FIFPixel](height, width)

  for
    y <- 0 until height
    x <- 0 until width
  do
    val (red, green, blue) = image(y)(x)
    
    val newRed = if chosenColor == FIFcolor.Red then red + adjustment else red
    val newGreen = if chosenColor == FIFcolor.Green then green + adjustment else green
    val newBlue = if chosenColor == FIFcolor.Blue then blue + adjustment else blue
    adjustedImage(y)(x) = (newRed, newGreen, newBlue)
  adjustedImage