package scimg.processing.commands

import scimg.processing.*

def adjustColor(image: FIFImage, adjustment: Int, chosenColor: FIFcolor = FIFcolor.Red): FIFImage = {
  Array.tabulate(image.height, image.width) { (y, x) =>
    val (red, green, blue) = image(y)(x)

    val newRed = if (chosenColor == FIFcolor.Red) red + adjustment else red
    val newGreen = if (chosenColor == FIFcolor.Green) green + adjustment else green
    val newBlue = if (chosenColor == FIFcolor.Blue) blue + adjustment else blue

    (newRed, newGreen, newBlue)
  }
}
