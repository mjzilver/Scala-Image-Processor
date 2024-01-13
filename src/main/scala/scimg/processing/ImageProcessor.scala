package scimg.processing

import scalafx.scene.image.{Image, ImageView, WritableImage, PixelWriter}
import javax.imageio.ImageIO
import scala.util.Random
import java.io.File

import scala.util.Try
import java.awt.image.BufferedImage

def importImage(imagePath: String): Option[FIFImage] =
  Try {
    val file = new File(imagePath)
    if (file.exists()) 
      ImageIO.read(file)
    else 
      ImageIO.read(this.getClass.getResourceAsStream(imagePath))
  }.toOption.map { image =>
    val width = image.getWidth.toInt
    val height = image.getHeight.toInt
    val pixels = Array.ofDim[FIFPixel](height, width)
    for {
      y <- 0 until height
      x <- 0 until width
    } {
      val rgb = image.getRGB(x, y)
      val red = (rgb >> 16) & 0xFF
      val green = (rgb >> 8) & 0xFF
      val blue = rgb & 0xFF
      pixels(y)(x) = (red, green, blue)
    }
    pixels
  }

def exportImage(image: FIFImage, imagePath: String): Unit =
  val width = image.head.length
  val height = image.length
  val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  for {
    y <- 0 until height
    x <- 0 until width
  } {
    val (red, green, blue) = image(y)(x)
    val rgb = (red << 16) | (green << 8) | blue
    bufferedImage.setRGB(x, y, rgb)
  }
  ImageIO.write(bufferedImage, imagePath.split('.').last, new File(imagePath))

def makeWriteableImage(image: FIFImage): WritableImage =
  val writableImage = new WritableImage(image.width, image.height)
  val pixelWriter = writableImage.getPixelWriter

  for
    y <- 0 until image.height
    x <- 0 until image.width
  do
    val pixel = image(y)(x)
    if (pixel != null) {
      val (red, green, blue) = pixel
      val argb = (0xFF << 24) | (red << 16) | (green << 8) | blue
      pixelWriter.setArgb(x, y, argb)
    } else {
      println(s"Unexpected null pixel at ($x, $y)")
    }

  writableImage
