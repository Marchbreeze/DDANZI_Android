package co.orange.ddanzi.di.module

import co.orange.domain.repository.AuthRepository
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.AuthChangeTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Singleton
    @Provides
    fun provideAuthChangeTokenUseCase(
        authRepository: AuthRepository,
        userRepository: UserRepository
    ): AuthChangeTokenUseCase {
        return AuthChangeTokenUseCase(authRepository, userRepository)
    }
}