package co.orange.ddanzi.di

import co.orange.data.dataSource.AuthDataSource
import co.orange.data.dataSource.HomeDataSource
import co.orange.data.dataSourceImpl.AuthDataSourceImpl
import co.orange.data.dataSourceImpl.HomeDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource = authDataSourceImpl

    @Provides
    @Singleton
    fun provideHomeDataSource(homeDataSourceImpl: HomeDataSourceImpl): HomeDataSource = homeDataSourceImpl
}
