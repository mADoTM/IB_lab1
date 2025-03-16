package generator

import kotlin.math.hypot


class LinearCongruentialGenerator(
    private val m: Int,
    private val c: Int,
    private val p: Int,
) {

    fun generateSequence(u0: Int, length: Int): DoubleArray {
        val sequence = DoubleArray(length)
        sequence[0] = u0.toDouble()
        for (i in 1 until length) {
            sequence[i] = countNextValue(sequence[i - 1])
        }
        return sequence
    }

    private fun countNextValue(previous: Double) =
        (previous * m + c) % p

    fun getUniqueSequenceSize(from: DoubleArray) =
        from.toSet().size
}

class LinearCongruentialGeneratorTest(
    private val generator: LinearCongruentialGenerator
) {

    fun `should be near the real PI`() {
        val N = 10_000_000
        val u0 = 10_000
        val sequence = generator.generateSequence(u0, N)


        val maximumOfSequence = sequence.max()

        var i = 0
        var c = 0
        while (i < N / 2 - 1) {
            if (hypot(sequence[2 * i], sequence[2 * i + 1]) < maximumOfSequence) {
                c++
            }
            i++
        }

        val actualPi = 8.0 * c / N

        println("Actual PI - $actualPi")
        println("Real PI - ${Math.PI}")
    }

    fun `coefficient of correlation should be greater than 0,75`() {
        val N = 10_000
        val u0 = 10_000
        val sequence = generator.generateSequence(u0, N)
        val mean = sequence.average()

        sequence.forEachIndexed { index, _ ->
            val meanSqr = mean * mean
            val coeff = (autoCorr(sequence, index, mean) - meanSqr) / (autoCorr(sequence, 0, mean) - meanSqr)

            if (coeff < 0.75) {
                println("Correlation coefficient estimate: $coeff")
            }
        }
    }

    private fun autoCorr(r: DoubleArray, k: Int, mean: Double) =
        if (k == 0) {
             1.0
        } else {
            var sum = 0.0
            for (i in 0 until r.size - k) {
                sum += (r[i] - mean) * (r[i + k] - mean)
            }

            sum / (r.size - k)
        }
}