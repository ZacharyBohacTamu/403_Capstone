import java.io.File
import javax.imageio.ImageIO
import java.io.FileOutputStream
import java.awt.Color
import java.awt.image.BufferedImage

//image processing Demo 1 - 4/23/2023

//a data class that is called Pixel for future use
data class Pixel(val x: Int, val y: Int)
//counter made to keep track of how many images are being created

//function that takes in an image and converts it to grayscale
fun imageToBlackAndWhite(inputFile: File, outputFile: File){
    val inputImage = ImageIO.read(inputFile)
    val outputImage = BufferedImage(inputImage.width, inputImage.height, BufferedImage.TYPE_BYTE_GRAY) //allows for output image to be grayscale
    //for loop that goes through each pixel and converts it to grayscale
    for(x in 0 until inputImage.width){
        for(y in 0 until inputImage.height){
            //grabs the rgb value of the pixel
            val rgb = inputImage.getRGB(x,y)
            val color = Color(rgb)
            //converts the rgb value to grayscale
            val average = (color.red + color.green + color.blue) / 3
            val gray = Color(average, average, average)
            //sets the pixel to the grayscale value
            outputImage.setRGB(x, y, gray.rgb)
        }
    }
    ImageIO.write(outputImage, "jpg", outputFile)
    println("${inputFile} has been converted to black and white")
}


fun imageComparision(file1: File, file2: File):List<List<Pixel>>{
    //setting read images to a variable
    val image1 = ImageIO.read(file1)
    val image2 = ImageIO.read(file2)

    //sets the width and height of the image to be read in sections
    val divider = 9 //5
    val sectionWidth = image1.width/divider
    val sectionHeight = image1.height/divider

    //creates a list of lists of pixels
    val differentPixelsList = mutableListOf<List<Pixel>>()

    //for loop for sectioning the image
    for (row in 0 until divider){
        for (col in 0 until divider){
            val differentPixels = mutableListOf<Pixel>()
            //for loop for comparing the pixels in the section
            for(y in row * sectionHeight until (row + 1) * sectionHeight){
                for(x in col * sectionWidth until (col + 1) * sectionWidth){
                    val p1 = Color(image1.getRGB(x,y)) //grabs image1 pixel of (x,y) location
                    val p2 = Color(image2.getRGB(x,y)) //grabs image2 pixel of (x,y) location
                    //sets the difference between the two pixels R,G,B value to a variable
                    val redDiff = Math.abs(p1.red - p2.red) 
                    val greenDiff = Math.abs(p1.green - p2.green)
                    val blueDiff = Math.abs(p1.blue - p2.blue)
                    //compares the R,G,B value to THRESHOLD
                    val low = 80 //threshold low
                    val high = 120 //threshold high 
                    if(((redDiff > low) && (redDiff < high)) || ((greenDiff > low) && (greenDiff < high)) || ((blueDiff > low) && (blueDiff < high))){ // want to add threshold_red that wont affect keeping lines out
                        differentPixels.add(Pixel(x,y))
                    }
                }
            }
            //adds differntPixels to differentPixelsList
            differentPixelsList.add(differentPixels)
        }
    }
        return differentPixelsList
}

//finds the section with the most pixels and returns it
fun findMax(differentPixelsList: List<List<Pixel>>): List<Pixel>{
    var max = 0
    var maxIndex = 0
    for (differentPixels in differentPixelsList.withIndex()){
        if(differentPixels.value.size > max){
            max = differentPixels.value.size
            maxIndex = differentPixels.index
        }
    }
    println("Max section is ${maxIndex}")
    return differentPixelsList[maxIndex]
}

//finds the center of the bullet shot in the max section
fun findCenter(maxDifferentPixels: List<Pixel>): Pixel{
    val centerX = maxDifferentPixels.map { it.x}.average().toInt()
    val centerY = maxDifferentPixels.map { it.y}.average().toInt()
    return Pixel(centerX, centerY)
}

 //code for testing and validation
 fun generateWhiteImage(width: Int, height: Int): File{
    //creates a white image that is able to be colored
    val whtImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB) 
    //creates a graphics object
    val create = whtImage.createGraphics()
    //sets the image to white
    create.color = Color.WHITE
    create.fillRect(0, 0, width, height)
    create.dispose()
    val fileName = "white"
    val temp = File(fileName)
    ImageIO.write(whtImage, "jpg", temp)
    return temp
 }

//function that takes in a file and a list of pixels and colors a white image with the pixels
 fun pixelMarking(fileWht: File ,differentPixelsList: List<List<Pixel>>, pixelColor: Int){
    val image: BufferedImage = ImageIO.read(fileWht)
    var counter = 0
    for (differentPixels in differentPixelsList.withIndex()){
        for(pixel in differentPixels.value){
            val x: Int = pixel.x
            val y: Int = pixel.y 
            image.setRGB(x, y, pixelColor)
        }
        val outputFile: File = File("colored_${fileWht.name}_${counter}.jpg")
        ImageIO.write(image, "jpg", outputFile)
        counter++
    }
 }



fun main() {
    //grabs files used
    val file1 = File("Target_Test_1.jpg")// change names based off image u are testing
    val file2 = File("Target_Test_Shot_1.jpg")
    /*
    other files used for testing
    Target_Clear.jpg
    First_Shot.jpg
    Target_Test_1.jpg
    Target_Test_Shot_1.jpg
    */

    //mainly for debuging; reads file to show width, height, and total pixels
    val image = ImageIO.read(file2)
    val width = image.width
    val height = image.height
    val totalPixel = width * height
    //mainly for debug and validation prints in output width, height, and total pixel
    println("Total number of Pixels for Width: ${width}")
    println("Total number of Pixels for Height: ${height}")
    println("Total number of Pixels: ${totalPixel}")


    //variables for grayscale
    val filebw1 = File("Target_Test_1bw.jpg")
    val filebw2 = File("Target_Test_Shot_1bw.jpg") 
    //calls upon module to conver called files and turn them grayscale
    imageToBlackAndWhite(file1, filebw1)
    imageToBlackAndWhite(file2, filebw2)

    //for testing purposes 
    val white1 = generateWhiteImage(width, height)


    //set differentPixels as the function imagecomparision and runs module
    val differentPixelsList = imageComparision(filebw1, filebw2) 


    //functions from above are called to find statistics
        var total = 0
        for (i in 0 until differentPixelsList.size){
            val differentPixels = differentPixelsList[i]
            println("Amount of Different Pixels in section $i: ${differentPixels.size}")
            total = total + differentPixels.size
        }
        println("total amount of Different Pixels: $total")
        val pixelColor = 0x000000   //hexadecimal for black
        pixelMarking(white1, differentPixelsList, pixelColor) //for testing purposes marks colored pixels on white image
        var max = findMax(differentPixelsList) // finds the section with the most pixels
        var center = findCenter(max) // finds the center of the bullet shot 
        println("Center of the bullet is at $center")
}
  
