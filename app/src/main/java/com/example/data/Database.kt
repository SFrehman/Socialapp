package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_items ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_items WHERE id = :id LIMIT 1")
    suspend fun getDownloadById(id: Int): DownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadItem): Long

    @Update
    suspend fun updateDownload(download: DownloadItem)

    @Delete
    suspend fun deleteDownload(download: DownloadItem)

    @Query("DELETE FROM download_items WHERE id = :id")
    suspend fun deleteDownloadById(id: Int)

    @Query("DELETE FROM download_items")
    suspend fun clearAllDownloads()
}

@Database(entities = [DownloadItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flymedia_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
