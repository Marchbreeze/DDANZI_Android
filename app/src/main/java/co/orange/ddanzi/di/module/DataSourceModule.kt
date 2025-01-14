package co.orange.ddanzi.di.module

import co.orange.data.dataSource.AuthDataSource
import co.orange.data.dataSource.BuyDataSource
import co.orange.data.dataSource.DeviceDataSource
import co.orange.data.dataSource.HomeDataSource
import co.orange.data.dataSource.IamportDataSource
import co.orange.data.dataSource.InterestDataSource
import co.orange.data.dataSource.ProfileDataSource
import co.orange.data.dataSource.SellDataSource
import co.orange.data.dataSource.SettingDataSource
import co.orange.data.dataSourceImpl.AuthDataSourceImpl
import co.orange.data.dataSourceImpl.BuyDataSourceImpl
import co.orange.data.dataSourceImpl.DeviceDataSourceImpl
import co.orange.data.dataSourceImpl.HomeDataSourceImpl
import co.orange.data.dataSourceImpl.IamportDataSourceImpl
import co.orange.data.dataSourceImpl.InterestDataSourceImpl
import co.orange.data.dataSourceImpl.ProfileDataSourceImpl
import co.orange.data.dataSourceImpl.SellDateSourceImpl
import co.orange.data.dataSourceImpl.SettingDataSourceImpl
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

    @Provides
    @Singleton
    fun provideDeviceDataSource(deviceDataSourceImpl: DeviceDataSourceImpl): DeviceDataSource = deviceDataSourceImpl

    @Provides
    @Singleton
    fun provideSellDataSource(sellDateSourceImpl: SellDateSourceImpl): SellDataSource = sellDateSourceImpl

    @Provides
    @Singleton
    fun provideBuyDataSource(buyDataSourceImpl: BuyDataSourceImpl): BuyDataSource = buyDataSourceImpl

    @Provides
    @Singleton
    fun provideProfileDataSource(profileDataSourceImpl: ProfileDataSourceImpl): ProfileDataSource = profileDataSourceImpl

    @Provides
    @Singleton
    fun providesSettingDataSource(settingDataSourceImpl: SettingDataSourceImpl): SettingDataSource = settingDataSourceImpl

    @Provides
    @Singleton
    fun providesInterestDataSource(interestDataSourceImpl: InterestDataSourceImpl): InterestDataSource = interestDataSourceImpl

    @Provides
    @Singleton
    fun provideIamportDataSource(iamportDataSourceImpl: IamportDataSourceImpl): IamportDataSource = iamportDataSourceImpl
}
