package com.mnmyounus.yfdw.di

import com.mnmyounus.yfdw.data.download.DownloadRepositoryImpl
import com.mnmyounus.yfdw.data.security.NetworkSecurityRepositoryImpl
import com.mnmyounus.yfdw.data.settings.OperatingModeRepositoryImpl
import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import com.mnmyounus.yfdw.domain.repository.NetworkSecurityRepository
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindOperatingModeRepository(impl: OperatingModeRepositoryImpl): OperatingModeRepository
    @Binds abstract fun bindNetworkSecurityRepository(impl: NetworkSecurityRepositoryImpl): NetworkSecurityRepository
    @Binds abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
}
