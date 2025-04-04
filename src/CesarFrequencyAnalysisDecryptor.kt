import java.util.Locale

class CesarFrequencyAnalysisDecryptor(
    private val encrypted: String,
    private val alphabetFrequencyMap: Map<Char, Double>
) {
    private val encryptedFrequencyMap =
        encrypted.toEncryptedFrequencyMap()

    fun execute(): String {
        println(encryptedFrequencyMap)

        val pairs = encryptedFrequencyMap
            .keys
            .zip(
                alphabetFrequencyMap
                    .toList()
                    .sortedByDescending { it.second }
                    .also {
                        println(it)
                    }
                    .map { it.first }
            )
            .toMap()

        println("resultedzippedmap: $pairs")

        return encrypted.map {
            pairs[it.lowercaseChar()] ?: it
        }.joinToString(separator = "")
    }


    private fun String.toEncryptedFrequencyMap(): Map<Char, Double> {
        val textContainsOnlyLetters = this.lowercase(Locale.getDefault()).filter(Char::isLetter)

        return textContainsOnlyLetters
            .groupBy { it }
            .map { it.key to it.value.count().toDouble() * 100 / textContainsOnlyLetters.length }
            .sortedByDescending { it.second }
            .toMap()
    }
}