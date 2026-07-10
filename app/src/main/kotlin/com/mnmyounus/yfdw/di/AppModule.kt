package com.mnmyounus.yfdw.di

import android.content.Context
import com.mnmyounus.yfdw.data.download.ChunkDownloader
import com.mnmyounus.yfdw.data.security.NetworkVpnObserver
import com.mnmyounus.yfdw.data.security.SecurityGate
import com.mnmyounus.yfdw.data.security.TorManager
import com.mnmyounus.yfdw.data.settings.SettingsDataStore
import com.mnmyounus.yfdw.domain.repository.NetworkSecurityRepository
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context) = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideNetworkVpnObserver(@ApplicationContext context: Context) = NetworkVpnObserver(context)

    @Provides
    @Singleton
    fun provideTorManager(@ApplicationContext context: Context) = TorManager(context)

    @Provides
    @Singleton
    fun provideChunkDownloader(client: OkHttpClient) = ChunkDownloader(client)

    @Provides
    @Singleton
    fun provideSecurityGate(
        @ApplicationContext context: Context,
        securityRepository: NetworkSecurityRepository,
        operatingModeRepository: OperatingModeRepository,
        client: OkHttpClient
    ) = SecurityGate(context, securityRepository, operatingModeRepository, client)
}
