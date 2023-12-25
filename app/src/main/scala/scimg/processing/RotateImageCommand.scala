package scimg.processing

val rotateImageCommand: ImageCommand = new ImageCommand {
    override def execute(image: FIFImage): FIFImage = {
        Array.tabulate(image.width, image.height) { (x, y) =>
            if clockwise then
                image(image.height - y - 1)(x)
            else
                image(y)(image.width - x - 1)
        }
    }
}