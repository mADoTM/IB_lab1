class FeistelNetworkCBC(
    private val source: String,
    private val key: FeistelKey,
    private val roundCount: Int,
    private val initVector: IntArray
) {

    fun encrypt(): List<IntArray> {
        val blocks64 = source.toByteArray().toList().chunked(64).map { it.toByteArray() }

        val list = mutableListOf<IntArray>()

        var previousBlock = initVector
        for (block in blocks64) {
            val chainedBlock = block xor previousBlock
            previousBlock = encryptParts(chainedBlock)
            list.add(previousBlock)
        }

        return list
    }

    private infix fun ByteArray.xor(another: IntArray) =
        this.mapIndexed { index, byte ->  byte.toInt() xor another[index] }.toIntArray()


    private fun encryptParts(block: IntArray): IntArray {
        var left = block.take(block.size / 2).toIntArray()
        var right = block.drop(block.size / 2).toIntArray()
        var temp: IntArray

        println("before encrypting")
        println("left ${left.joinToString()}")
        println("right ${right.joinToString()}")

        for (i in 0..roundCount) {
            right.forEachIndexed { j, _ ->
                left[j] = left[j] xor encryptElement(right[j], i)
            }

            temp = left
            left = right
            right = temp
        }

        println("after encrypting")
        println("left ${left.joinToString()}")
        println("right ${right.joinToString()}")

        return (left + right)
    }

    fun decrypt(blocks: List<IntArray>): String {
        val list = mutableListOf<IntArray>()

        var previousBlock = initVector
        for (block in blocks) {
            val decryptedBlock = decryptParts(block)
            list.add(decryptedBlock xor previousBlock)
            previousBlock = block
        }

        return list.joinToString(separator = "") {
            toPlainText(it)
        }
    }

    private infix fun IntArray.xor(another: IntArray) =
        this.mapIndexed { index, byte ->  byte xor another[index] }.toIntArray()

    private fun decryptParts(block: IntArray): IntArray {

        var left = block.take(block.size / 2).toIntArray()
        var right = block.drop(block.size / 2).toIntArray()
        var temp: IntArray
        println("before decrypting")
        println("left ${left.joinToString()}")
        println("right ${right.joinToString()}")

        for (i in 0..roundCount) {
            temp = left
            left = right
            right = temp

            right.forEachIndexed { j, _ ->
                left[j] = left[j] xor encryptElement(right[j], roundCount - i)
            }
        }

        println("after decrypting")
        println("left ${left.joinToString()}")
        println("right ${right.joinToString()}")
        return (left + right)
    }

    private fun toPlainText(leftBlock: IntArray): String {
        val byteArray = leftBlock.map {
            it.toByte()
        }.toByteArray()


        return String(byteArray)
    }

    // F_2(L, K) = ((L or K) xor (L << 7)) not (K << 1)
    private fun encryptElement(element: Int, roundNumber: Int): Int {
        val k = key.computeRoundKey(roundNumber)

        return ((element or k) xor (element shl 7)) and (k shl 1).inv()
    }

}

