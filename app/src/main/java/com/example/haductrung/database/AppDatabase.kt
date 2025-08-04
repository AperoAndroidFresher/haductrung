package com.example.haductrung.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.haductrung.database.DAO.PlaylistDao
import com.example.haductrung.database.DAO.SongDao
import com.example.haductrung.database.DAO.UserDao
import com.example.haductrung.database.entity.PlaylistEntity
import com.example.haductrung.database.entity.SongEntity
import com.example.haductrung.database.entity.UserEntity


@Database(
    entities = [UserEntity::class, SongEntity::class, PlaylistEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDAO(): UserDao
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_app_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
