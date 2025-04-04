
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.random.Random


fun main() {
//    val roundCount = 32
//
//    val source = "hello world, helaasdasdasdasassahdashdashdashdashdashdahsdhasadasdasdsadasdsadasdasdasdsadasdasdas"
//
//    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DEFAULT CIPHER:")
//
//    val feistelNetwork = FeistelNetwork(
//        source = source,
//        key = FeistelKey.generate(),
//        roundCount = roundCount
//    )
//    val encrypted = feistelNetwork.encrypt()
//    val decrypted = feistelNetwork.decrypt(encrypted)
//
//    println("Source text: $source")
//    println("Decrypted text default: $decrypted")
//
//    println()
//    println()
//    println()
//    println()
//    println()
//    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CBC MODE:")
//
//    val feistelNetworkCBC = FeistelNetworkCBC(
//        source = source,
//        key = FeistelKey.generate(),
//        roundCount = roundCount,
//        initVector = initVector()
//    )
//    val encryptedCBC = feistelNetworkCBC.encrypt()
//    val decryptedCBC = feistelNetworkCBC.decrypt(encryptedCBC)
//
//    println("Source text: $source")
//    println("Decrypted text CBC: $decryptedCBC")
//
//    println()
//    println()
//    println()
//    println()
//    println()
//    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CFB MODE:")
//
//    val feistelNetworkCFB = FeistelNetworkCFB(
//        source = source,
//        key = FeistelKey.generate(),
//        roundCount = roundCount,
//        initVector = initVector()
//    )
//    val encryptedCFB = feistelNetworkCFB.encrypt()
//    val decryptedCFB = feistelNetworkCFB.decrypt(encryptedCFB)
//
//    println("Source text: $source")
//    println("Decrypted text CFB: $decryptedCFB")
//
//
//    val p = 11979 // prime number
//    val M = 430 // multiplier
//    val C = 1 // increment
//    val U0 = 10000 // initial value
//    val generator = LinearCongruentialGenerator(M, C, p)
//    val seq = generator.generateSequence(U0, 10_000)
//
//    val tester = LinearCongruentialGeneratorTest(generator)
//
//    tester.`should be near the real PI`()
//    tester.`coefficient of correlation should be greater than 0,75`()
//
//    println("end")

//    val n = BigInteger("889577666850907")
//    val e = BigInteger("13971")
//    val encrypted = BigInteger("403013074606912545180648978557219641194372024501606729868202878976557455422")
//
//    val rsaDecryptor = RsaDecryptor(
//        n,
//        e,
//        encrypted
//    )
//
//    println(rsaDecryptor.decrypt())


    val source = File("CesarSourceInput.txt").readText()
    val frequencyMap = readFrequencyMap()

    val decryptor = CesarFrequencyAnalysisDecryptor(
        source,
        frequencyMap
    )

    val decrypted = decryptor.execute()

    File("CesarSourceOutput.txt").writeText(decrypted)

    decryptImage()
}

private fun decryptImage() {
    val text = "Slavik napisla etot text esli ne zarabotaet on vinovat".toByteArray(charset("IBM866"))
    val L = 0.5
    val sigma = 4
    val K0 = 123

    // Загрузка изображения
    var image = ImageIO.read(File("Huhr7x8-Q10.jpg"))
    val width = image.width
    val height = image.height

    // Преобразование текста в биты
    val bitChars = mutableListOf<List<Int>>()
    for (char in text) {
        val bits = char.toUByte().toString(2).padStart(8, '0').map { it.digitToInt() }
        bitChars.add(bits)
    }

    // Установка seed для генератора случайных чисел
    var random = Random(K0)

    // Встраивание сообщения в изображение
    for (i in bitChars.indices) {
        for (j in bitChars[i].indices) {
            val x = random.nextInt(sigma, width - sigma)
            val y = random.nextInt(sigma, height - sigma)

            val rgb = image.getRGB(x, y)
            val r = (rgb shr 16) and 0xFF
            val g = (rgb shr 8) and 0xFF
            val b = rgb and 0xFF
            val power = (0.299 * r + 0.589 * g + 0.114 * b).toInt()

            var newB = b + ((2 * bitChars[i][j] - 1) * L * power).roundToInt()
            newB = newB.coerceIn(0, 255)

            val newColor = (r shl 16) or (g shl 8) or newB
            image.setRGB(x, y, newColor)
        }
    }

    // Сохранение зашифрованного изображения
    ImageIO.write(image, "png", File("encrypted.png"))

    // Чтение зашифрованного изображения
    image = ImageIO.read(File("encrypted.png"))
    random = Random(K0) // Сброс seed для воспроизводимости

    val resultBits = mutableListOf<Boolean>()
    repeat(text.size * 8) {
        val x = random.nextInt(sigma, width - sigma)
        val y = random.nextInt(sigma, height - sigma)

        val b = image.getRGB(x, y) and 0xFF
        var tempB = 0.0

        for (k in 1..sigma) {
            // Проверка границ изображения перед доступом к пикселям
            val bt = if (y + k < height) image.getRGB(x, y + k) and 0xFF else 0
            val bd = if (y - k >= 0) image.getRGB(x, y - k) and 0xFF else 0
            val bl = if (x - k >= 0) image.getRGB(x - k, y) and 0xFF else 0
            val br = if (x + k < width) image.getRGB(x + k, y) and 0xFF else 0

            tempB += bt + bd + bl + br
        }
        tempB /= (4 * sigma)

        resultBits.add(tempB < b)
    }

    // Преобразование битов в текст
    val resultBytes = resultBits
        .chunked(8)
        .map { byteBits ->
            byteBits.fold(0) { acc, bit -> (acc shl 1) or if (bit) 1 else 0 }.toByte()
        }.toByteArray()

    val decodedText = String(resultBytes, charset("IBM866"))
    println("Результат расшифровывания: $decodedText")

    // Подсчет ошибок
    val originalBits = text.flatMap { byte ->
        (0..7).map { bit -> ((byte.toInt() ushr (7 - bit)) and 1) == 1 }
    }
    val errorCount = originalBits.zip(resultBits)
        .count { (orig, dec) -> orig != dec }

    println("Ошибки расшифровывания: $errorCount")
}

private fun readFrequencyMap() =
    File("RussianFrequencyMap.txt")
        .readLines()
        .associate { raw ->
            val details = raw.split("=")
            val letter = details[0]
            val frequency = details[1].toDouble()
            letter.toCharArray()[0] to frequency
        }