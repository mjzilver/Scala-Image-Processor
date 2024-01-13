package scimg.processing.commands

import scimg.processing.*

case class Brush(size: Int, color: FIFPixel)

def paintBrush(image: FIFImage, brush: Brush, centerX: Int, centerY: Int): FIFImage = {
  val radiusSquared = brush.size * brush.size

  for {
    x <- 0 until image.head.length
    y <- 0 until image.length
    if isWithinCircle(x, y, centerX, centerY, radiusSquared)
  } {
    image(y)(x) = brush.color
  }

  image
}

def isWithinCircle(x: Int, y: Int, centerX: Int, centerY: Int, radiusSquared: Int): Boolean = {
  val dx = x - centerX
  val dy = y - centerY
  val distanceSquared = dx * dx + dy * dy
  distanceSquared <= radiusSquared
}
