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
// image is indexed by y, x

extension (image: FIFImage)
  def width: Int = image.headOption.map(_.length).getOrElse(0)
  def height: Int = image.length


enum FIFcolor:
  case Red, Green, Blue

def importImage(imagePath: String): FIFImage =
  try {
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
  } catch {
    case e: Exception =>
      println(s"Error importing image: ${e.getMessage}")
      Array.ofDim[FIFPixel](0, 0)
  }

def makeWriteableImage(image: FIFImage): WritableImage =
  val writableImage = new WritableImage(image.width, image.height)
  val pixelWriter = writableImage.getPixelWriter

  for
    y <- 0 until image.height
    x <- 0 until image.width
  do
    val (red, green, blue) = image(y)(x)
    val argb = (0xFF << 24) | (red << 16) | (green << 8) | blue
    pixelWriter.setArgb(x, y, argb)
  writableImage

def rotateImage(image: FIFImage, clockwise: Boolean = true): FIFImage =
  Array.tabulate(image.width, image.height) { (x, y) =>
    if clockwise then
      image(image.height - y - 1)(x)
    else
      image(y)(image.width - x - 1)
  }

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

def adjustColor(image: FIFImage, adjustment: Int, chosenColor: FIFcolor = FIFcolor.Red): FIFImage = {
  Array.tabulate(image.height, image.width) { (y, x) =>
    val (red, green, blue) = image(y)(x)

    val newRed = if (chosenColor == FIFcolor.Red) red + adjustment else red
    val newGreen = if (chosenColor == FIFcolor.Green) green + adjustment else green
    val newBlue = if (chosenColor == FIFcolor.Blue) blue + adjustment else blue

    (newRed, newGreen, newBlue)
  }
}
