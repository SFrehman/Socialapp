package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "download_items")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val title: String,
    val source: String,
    val duration: String,
    val isPlaylist: Boolean,
    val playlistTitle: String? = null,
    val status: String, // PENDING, DOWNLOADING, COMPLETED, FAILED
    val progress: Float = 0f,
    val downloadedSize: String = "0 MB",
    val totalSize: String = "0 MB",
    val eta: String = "--",
    val isAudio: Boolean = false,
    val quality: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class GeminiMediaOption(
    val resolution: String,
    val size: String
)

@JsonClass(generateAdapter = true)
data class GeminiAudioOption(
    val bitrate: String,
    val size: String
)

@JsonClass(generateAdapter = true)
data class GeminiMediaItem(
    val id: String,
    val title: String,
    val source: String,
    val duration: String,
    val videoOptions: List<GeminiMediaOption>,
    val audioOptions: List<GeminiAudioOption>
)

@JsonClass(generateAdapter = true)
data class GeminiMediaResponse(
    val isPlaylist: Boolean,
    val playlistTitle: String? = null,
    val mediaItems: List<GeminiMediaItem>
)
