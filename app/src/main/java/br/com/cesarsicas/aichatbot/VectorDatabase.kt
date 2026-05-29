package br.com.cesarsicas.aichatbot

import android.content.Context
import android.util.Log
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VectorDatabase(private val context: Context) {

    companion object {
        private const val TAG = "VectorDatabase"
        private const val DB_NAME = "characters_rag.db"

        init {
            System.loadLibrary("vectorsearch")
        }

        @JvmStatic
        external fun nativeSearch(
            dbPath: String,
            embeddingBytes: ByteArray,
            characterId: String,
            topK: Int
        ): Array<String>
    }

    private val dbFile: File by lazy {
        File(context.filesDir, DB_NAME).also { file ->
            if (!file.exists()) {
                Log.i(TAG, "Copying database from assets…")
                context.assets.open(DB_NAME).use { it.copyTo(file.outputStream()) }
                Log.i(TAG, "Database ready at ${file.absolutePath}")
            }
        }
    }

    fun search(queryEmbedding: FloatArray, characterId: String, topK: Int = 3): List<String> {
        val bytes = floatArrayToBytes(queryEmbedding)
        return nativeSearch(dbFile.absolutePath, bytes, characterId, topK).toList()
    }

    private fun floatArrayToBytes(floats: FloatArray): ByteArray {
        val buf = ByteBuffer.allocate(floats.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        floats.forEach { buf.putFloat(it) }
        return buf.array()
    }
}
