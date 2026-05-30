#include <jni.h>
#include <string>
#include <vector>
#include <cstring>
#include <android/log.h>

#include "sqlite3.h"
#include "sqlite-vec.h"

#define LOG_TAG "VectorSearch"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_br_com_cesarsicas_aichatbot_data_local_VectorDatabase_nativeSearch(
    JNIEnv *env,
    jclass,
    jstring dbPath,
    jbyteArray embeddingBytes,
    jstring characterId,
    jint topK
) {
    const char *dbPathStr = env->GetStringUTFChars(dbPath, nullptr);
    const char *charIdStr = env->GetStringUTFChars(characterId, nullptr);
    jbyte *embBytes = env->GetByteArrayElements(embeddingBytes, nullptr);
    jsize embLen = env->GetArrayLength(embeddingBytes);

    std::vector<std::string> results;

    auto doSearch = [&]() {
        sqlite3 *db = nullptr;
        int rc = sqlite3_open_v2(dbPathStr, &db, SQLITE_OPEN_READONLY, nullptr);
        if (rc != SQLITE_OK) {
            LOGE("sqlite3_open failed: %s", sqlite3_errmsg(db));
            sqlite3_close(db);
            return;
        }

        rc = sqlite3_vec_init(db, nullptr, nullptr);
        if (rc != SQLITE_OK) {
            LOGE("sqlite3_vec_init failed: %s", sqlite3_errmsg(db));
            sqlite3_close(db);
            return;
        }

        const char *sql =
            "SELECT content, character_id "
            "FROM character_knowledge "
            "WHERE embedding MATCH ? AND k = ? "
            "ORDER BY distance";

        sqlite3_stmt *stmt = nullptr;
        rc = sqlite3_prepare_v2(db, sql, -1, &stmt, nullptr);
        if (rc != SQLITE_OK) {
            LOGE("sqlite3_prepare_v2 failed: %s", sqlite3_errmsg(db));
            sqlite3_close(db);
            return;
        }

        sqlite3_bind_blob(stmt, 1, embBytes, embLen, SQLITE_STATIC);
        sqlite3_bind_int(stmt, 2, topK * 10);

        int found = 0;
        while (sqlite3_step(stmt) == SQLITE_ROW && found < topK) {
            const char *content = reinterpret_cast<const char *>(sqlite3_column_text(stmt, 0));
            const char *charId  = reinterpret_cast<const char *>(sqlite3_column_text(stmt, 1));
            if (content && charId && std::strcmp(charId, charIdStr) == 0) {
                results.emplace_back(content);
                found++;
            }
        }

        sqlite3_finalize(stmt);
        sqlite3_close(db);
    };

    doSearch();

    env->ReleaseStringUTFChars(dbPath, dbPathStr);
    env->ReleaseStringUTFChars(characterId, charIdStr);
    env->ReleaseByteArrayElements(embeddingBytes, embBytes, JNI_ABORT);

    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray resultArray = env->NewObjectArray(
        static_cast<jsize>(results.size()), stringClass, nullptr);
    for (int i = 0; i < static_cast<int>(results.size()); i++) {
        jstring str = env->NewStringUTF(results[i].c_str());
        env->SetObjectArrayElement(resultArray, i, str);
        env->DeleteLocalRef(str);
    }
    return resultArray;
}
