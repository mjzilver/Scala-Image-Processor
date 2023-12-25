package scimg.processing

trait ImageCommand {
  def execute(image: FIFImage): FIFImage
}