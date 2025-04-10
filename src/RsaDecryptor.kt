import java.math.BigInteger

class RsaDecryptor(
    private val n: BigInteger,
    private val e: BigInteger,
    private val encrypted: BigInteger
) {

    fun decrypt(): BigInteger {
        val primeNumbers = findPrimeFactors(n)
            ?: throw IllegalArgumentException("N must be p*q, where p and q is prime numbers")

        val p = primeNumbers.first
        val q = primeNumbers.second

        println("p = $p q=$q")

        val phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE))

        val d = e.modInverse(phi)

        val blocks = encrypted.splitToBlocks()

        val result = blocks.map { it.modPow(d, n).toString() }

        return BigInteger(
            result.joinToString(
                separator = ""
            )
        )
    }

    private fun BigInteger.splitToBlocks(): List<BigInteger> {
        val result = mutableListOf<BigInteger>()

        var reminder = this.toString()

        val nLength = n.toString().length
        while (reminder.isNotEmpty()) {
            val potentialBigNumber = BigInteger(reminder.take(nLength))
            val potentialSmallNumber = BigInteger(reminder.take(nLength - 1))

            if (potentialBigNumber > n) {
                result.add(potentialSmallNumber)
                reminder = reminder.drop(nLength - 1)
            } else {
                result.add(potentialBigNumber)
                reminder = reminder.drop(nLength)
            }
        }

        return result
    }

    private fun findPrimeFactors(n: BigInteger): Pair<BigInteger, BigInteger>? {
        val sqrtN = kotlin.math.sqrt(n.toDouble()).toLong()
        for (i in 2..sqrtN) {
            val bigI = BigInteger.valueOf(i)
            if (n % bigI == BigInteger.ZERO) {
                val j = n / bigI
                if (isPrime(bigI) && isPrime(j)) {
                    return Pair(bigI, j)
                }
            }
        }
        return null
    }

    private fun isPrime(num: BigInteger): Boolean {
        if (num < BigInteger.TWO) return false
        val sqrtNum = kotlin.math.sqrt(num.toDouble()).toLong()
        for (i in 2..sqrtNum) {
            if (num % BigInteger.valueOf(i) == BigInteger.ZERO) {
                return false
            }
        }
        return true
    }
}