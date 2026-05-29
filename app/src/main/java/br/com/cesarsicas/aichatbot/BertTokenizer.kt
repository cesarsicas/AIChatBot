package br.com.cesarsicas.aichatbot

import android.content.Context
import java.text.Normalizer

class BertTokenizer(context: Context) {

    private val vocab: Map<String, Int>
    private val maxLength = 256

    private val unkId = 100
    private val clsId = 101
    private val sepId = 102

    init {
        val lines = context.assets.open("vocab.txt").bufferedReader().readLines()
        vocab = HashMap<String, Int>(lines.size * 2).apply {
            lines.forEachIndexed { i, token -> put(token, i) }
        }
    }

    data class Encoding(
        val inputIds: LongArray,
        val attentionMask: LongArray,
        val tokenTypeIds: LongArray
    )

    fun encode(text: String): Encoding {
        val tokens = tokenize(text)
        val maxTokens = maxLength - 2
        val truncated = if (tokens.size > maxTokens) tokens.subList(0, maxTokens) else tokens

        val ids = mutableListOf(clsId)
        truncated.mapTo(ids) { vocab[it] ?: unkId }
        ids.add(sepId)

        val seqLen = ids.size
        return Encoding(
            inputIds = LongArray(seqLen) { ids[it].toLong() },
            attentionMask = LongArray(seqLen) { 1L },
            tokenTypeIds = LongArray(seqLen) { 0L }
        )
    }

    private fun tokenize(text: String): List<String> {
        val result = mutableListOf<String>()
        for (token in basicTokenize(text)) {
            result.addAll(wordPieceTokenize(token))
        }
        return result
    }

    private fun basicTokenize(text: String): List<String> {
        val cleaned = Normalizer.normalize(text.trim().lowercase(), Normalizer.Form.NFD)
            .filter { it.category != CharCategory.NON_SPACING_MARK }

        val tokens = mutableListOf<String>()
        val current = StringBuilder()

        for (char in cleaned) {
            when {
                char.isWhitespace() -> {
                    if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }
                }
                isPunctuation(char) || isCjkChar(char) -> {
                    if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }
                    tokens.add(char.toString())
                }
                else -> current.append(char)
            }
        }
        if (current.isNotEmpty()) tokens.add(current.toString())
        return tokens
    }

    private fun wordPieceTokenize(token: String): List<String> {
        if (token.length > 200) return listOf("[UNK]")
        val subTokens = mutableListOf<String>()
        var start = 0

        while (start < token.length) {
            var end = token.length
            var match: String? = null
            while (start < end) {
                val candidate = if (start == 0) token.substring(start, end)
                                else "##${token.substring(start, end)}"
                if (vocab.containsKey(candidate)) { match = candidate; break }
                end--
            }
            if (match == null) return listOf("[UNK]")
            subTokens.add(match)
            start = end
        }
        return subTokens
    }

    private fun isPunctuation(c: Char): Boolean {
        val cp = c.code
        if ((cp in 33..47) || (cp in 58..64) || (cp in 91..96) || (cp in 123..126)) return true
        return c.category.name.startsWith("P")
    }

    private fun isCjkChar(c: Char): Boolean {
        val cp = c.code
        return (cp in 0x4E00..0x9FFF) || (cp in 0x3400..0x4DBF) ||
               (cp in 0xF900..0xFAFF) || (cp in 0x2F800..0x2FA1F)
    }
}
