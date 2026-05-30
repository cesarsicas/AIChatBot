package br.com.cesarsicas.aichatbot.di

import br.com.cesarsicas.aichatbot.data.repository.ChatRepositoryImpl
import br.com.cesarsicas.aichatbot.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {

    @Binds
    @ViewModelScoped
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
