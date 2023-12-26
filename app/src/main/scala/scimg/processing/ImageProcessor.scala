package scimg.processing

import scalafx.scene.image.{Image, ImageView, WritableImage, PixelWriter}
import javax.imageio.ImageIO
import scala.compiletime.ops.boolean
import scala.util.Random
import java.io.File

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