package com.mnmyounus.yfdw.di

import android.content.Context
import com.mnmyounus.yfdw.data.local.DatabaseKeyProvider
import com.mnmyounus.yfdw.data.local.DownloadDao
import com.mnmyounus.yfdw.data.local.YfdwDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseKeyProvider(@ApplicationContext context: Context) = DatabaseKeyProvider(context)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keyProvider: DatabaseKeyProvider
    ): YfdwDatabase = YfdwDatabase.build(context, keyProvider)

    @Provides
    fun provideDownloadDao(db: YfdwDatabase): DownloadDao = db.downloadDao()
}
