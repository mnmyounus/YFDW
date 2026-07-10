package com.mnmyounus.yfdw.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
abstract class YfdwDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        fun build(context: Context, keyProvider: DatabaseKeyProvider): YfdwDatabase {
            SQLiteDatabase.loadLibs(context)
            val passphrase = keyProvider.getOrCreatePassphrase()
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, YfdwDatabase::class.java, "yfdw.db")
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
