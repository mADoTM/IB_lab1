
import generator.LinearCongruentialGenerator
import generator.LinearCongruentialGeneratorTest

fun main() {
    val roundCount = 32

    val source = "hello world, helaasdasdasdasassahdashdashdashdashdashdahsdhasadasdasdsadasdsadasdasdasdsadasdasdas"

    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~DEFAULT CIPHER:")

    val feistelNetwork = FeistelNetwork(
        source = source,
        key = FeistelKey.generate(),
        roundCount = roundCount
    )
    val encrypted = feistelNetwork.encrypt()
    val decrypted = feistelNetwork.decrypt(encrypted)

    println("Source text: $source")
    println("Decrypted text default: $decrypted")

    println()
    println()
    println()
    println()
    println()
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CBC MODE:")

    val feistelNetworkCBC = FeistelNetworkCBC(
        source = source,
        key = FeistelKey.generate(),
        roundCount = roundCount,
        initVector = initVector()
    )
    val encryptedCBC = feistelNetworkCBC.encrypt()
    val decryptedCBC = feistelNetworkCBC.decrypt(encryptedCBC)

    println("Source text: $source")
    println("Decrypted text CBC: $decryptedCBC")

    println()
    println()
    println()
    println()
    println()
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CFB MODE:")

    val feistelNetworkCFB = FeistelNetworkCFB(
        source = source,
        key = FeistelKey.generate(),
        roundCount = roundCount,
        initVector = initVector()
    )
    val encryptedCFB = feistelNetworkCFB.encrypt()
    val decryptedCFB = feistelNetworkCFB.decrypt(encryptedCFB)

    println("Source text: $source")
    println("Decrypted text CFB: $decryptedCFB")


    val p = 11979 // prime number
    val M = 430 // multiplier
    val C = 1 // increment
    val U0 = 10000 // initial value
    val generator = LinearCongruentialGenerator(M, C, p)
    val seq = generator.generateSequence(U0, 10_000)

    val tester = LinearCongruentialGeneratorTest(generator)

    tester.`should be near the real PI`()
    tester.`coefficient of correlation should be greater than 0,75`()

    println("end")
}