package br.com.cesarsicas.aichatbot

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import java.nio.LongBuffer
import kotlin.math.sqrt

class EmbeddingModel(context: Context) : AutoCloseable {

    private val tokenizer = BertTokenizer(context)
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        val bytes = context.assets.open("all-MiniLM-L6-v2.onnx").readBytes()
        session = env.createSession(bytes, OrtSession.SessionOptions())
    }

    fun embed(text: String): FloatArray {
        val enc = tokenizer.encode(text)
        val seqLen = enc.inputIds.size
        val shape = longArrayOf(1, seqLen.toLong())

        val inputIds = OnnxTensor.createTensor(env, LongBuffer.wrap(enc.inputIds), shape)
        val attMask = OnnxTensor.createTensor(env, LongBuffer.wrap(enc.attentionMask), shape)
        val tokTypeIds = if ("token_type_ids" in session.inputNames)
            OnnxTensor.createTensor(env, LongBuffer.wrap(enc.tokenTypeIds), shape) else null

        val inputs = buildMap<String, OnnxTensor> {
            put("input_ids", inputIds)
            put("attention_mask", attMask)
            tokTypeIds?.let { put("token_type_ids", it) }
        }

        val outputs = session.run(inputs)
        return try {
            val outputName = if ("last_hidden_state" in session.outputNames) "last_hidden_state"
                             else session.outputNames.first()
            @Suppress("UNCHECKED_CAST")
            val hiddenState = (outputs.get(outputName).get() as OnnxTensor).value as Array<Array<FloatArray>>
            l2Normalize(meanPool(hiddenState[0], enc.attentionMask))
        } finally {
            inputIds.close(); attMask.close(); tokTypeIds?.close(); outputs.close()
        }
    }

    private fun meanPool(tokenEmbs: Array<FloatArray>, attMask: LongArray): FloatArray {
        val dim = tokenEmbs[0].size
        val result = FloatArray(dim)
        var count = 0
        for (i in tokenEmbs.indices) {
            if (attMask[i] == 1L) {
                for (j in 0 until dim) result[j] += tokenEmbs[i][j]
                count++
            }
        }
        if (count > 0) for (j in 0 until dim) result[j] /= count
        return result
    }

    private fun l2Normalize(v: FloatArray): FloatArray {
        var norm = 0f
        for (x in v) norm += x * x
        norm = sqrt(norm.toDouble()).toFloat()
        return if (norm > 0f) FloatArray(v.size) { v[it] / norm } else v
    }

    override fun close() {
        session.close()
    }
}
