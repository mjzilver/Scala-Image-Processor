package scimg.processing.commands

import scimg.processing._
import scimg.processing.commands.resizeImage
import scimg.processing.commands.resizeToSameDimensions

object ImageCombinationOperations extends Enumeration {
  val BitwiseAnd, BitwiseOr, BitwiseXor, BitwiseNot, LeftShift, RightShift = Value
}

import ImageCombinationOperations._

def combineImages(image1: FIFImage, image2: FIFImage, operation: ImageCombinationOperations.Value): FIFImage = {
  // Resize images to have the same dimensions
  val (resizedImage1, resizedImage2) = resizeToSameDimensions(image1, image2)

  val combinedPixels = operation match {
    case BitwiseAnd =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (r1 & r2, g1 & g2, b1 & b2) }
    case BitwiseOr =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (r1 | r2, g1 | g2, b1 | b2) }
    case BitwiseXor =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (r1 ^ r2, g1 ^ g2, b1 ^ b2) }
    case BitwiseNot =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (~r1, ~g1, ~b1) }
    case LeftShift =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (r1 << r2, g1 << g2, b1 << b2) }
    case RightShift =>
      combinePixels(resizedImage1, resizedImage2) { case ((r1, g1, b1), (r2, g2, b2)) => (r1 >> r2, g1 >> g2, b1 >> b2) }
  }
  combinedPixels
}

private def combinePixels(image1: FIFImage, image2: FIFImage)(
  combineFunction: ((Int, Int, Int), (Int, Int, Int)) => (Int, Int, Int)
): FIFImage =
  image1.zip(image2).map { case (row1, row2) =>
    row1.zip(row2).map { case (pixel1, pixel2) =>
      combineFunction(pixel1, pixel2)
    }
  }
