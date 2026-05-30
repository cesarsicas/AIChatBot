package br.com.cesarsicas.aichatbot.di

import android.content.Context
import br.com.cesarsicas.aichatbot.data.local.EmbeddingModel
import br.com.cesarsicas.aichatbot.data.local.VectorDatabase
import br.com.cesarsicas.aichatbot.data.repository.RagRepositoryImpl
import br.com.cesarsicas.aichatbot.domain.repository.RagRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RagModule {

    @Binds
    @Singleton
    abstract fun bindRagRepository(impl: RagRepositoryImpl): RagRepository

    companion object {
        @Provides
        @Singleton
        fun provideEmbeddingModel(@ApplicationContext context: Context): EmbeddingModel =
            EmbeddingModel(context)

        @Provides
        @Singleton
        fun provideVectorDatabase(@ApplicationContext context: Context): VectorDatabase =
            VectorDatabase(context)
    }
}
