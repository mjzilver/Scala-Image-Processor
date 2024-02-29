package scimg.processing

// FIF stands for Functional Image Format
// FIFPixel is a tuple of (r, g, b) values
type FIFPixel = (Int, Int, Int)
// FIFImage is a 2D array of pixels
type FIFImage = Array[Array[FIFPixel]]

// constructor for FIFImage with a given width and height
def newFIFImage(width: Int, height: Int): FIFImage = Array.fill(height, width)((0, 0, 0))

extension (image: FIFImage)
  def width: Int = image.headOption.map(_.length).getOrElse(0)
  def height: Int = image.length

enum FIFcolor:
  case Red, Green, Blue