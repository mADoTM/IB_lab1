import kotlin.random.Random

class FeistelNetwork(
    private val source: String,
    private val key: FeistelKey,
    private val roundCount: Int,
) {

    fun encrypt(): List<Pair<IntArray, IntArray>> {
        return source.toByteArray().toList().chunked(64).map {
            encryptParts(String(it.toByteArray()))
        }
    }

    private fun encryptParts(part: String): Pair<IntArray, IntArray> {
        val arrays = part.toArrays()

        var left = arrays.first
        var right = arrays.second
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

        return Pair(left, right)
    }

    fun decrypt(blocks: List<Pair<IntArray, IntArray>>): String {
        return blocks.joinToString(separator = "") {
            decryptParts(it.first, it.second)
        }
    }

    private fun decryptParts(leftBlock: IntArray, rightArray: IntArray): String {

        var left = leftBlock
        var right = rightArray
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
        return toPlainText(left, right)
    }

    private fun toPlainText(leftBlock: IntArray, rightBlock: IntArray): String {
        val byteArray = leftBlock.map {
            it.toByte()
        }.plus(rightBlock.map {
            it.toByte()
        }
        ).toByteArray()


        return String(byteArray)
    }

    private fun String.toArrays(): Pair<IntArray, IntArray> {
        val left = this.substring(0, this.length / 2)
        val right = this.substring(this.length / 2)

        val leftIntArray = left.toByteArray().map {
            it.toInt()
        }.toIntArray()

        val rightIntArray = right.toByteArray().map {
            it.toInt()
        }.toIntArray()

        return Pair(leftIntArray, rightIntArray)
    }

    // F_2(L, K) = ((L or K) xor (L << 7)) not (K << 1)
    private fun encryptElement(element: Int, roundNumber: Int): Int {
        val k = key.computeRoundKey(roundNumber)

        return ((element or k) xor (element shl 7)) and (k shl 1).inv()
    }

}

data class FeistelKey(
    val value: Long
) {

    fun computeRoundKey(round: Int) =
        (this.value shr (round * 3)).toInt()

    companion object {
        fun generate() = FeistelKey(Random.nextLong())
    }
}

fun initVector() =
    (1..64).map { Random.nextInt() }.toIntArray()