package scimg.processing

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