package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DownloadItem
import com.example.data.GeminiMediaItem
import com.example.data.GeminiMediaResponse
import com.example.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database.downloadDao())

    // --- UI Theme ---
    var isDarkTheme by mutableStateOf(true)
        private set

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    // --- Navigation ---
    // 0: Media Downloader, 1: Downloads Manager, 2: About App, 3: AI Agent Page (as an overlay, but we can manage it via state!)
    var currentScreen by mutableStateOf(0)
    var showAiAgentOverlay by mutableStateOf(false)

    // --- Media Downloader Screen State ---
    var inputUrl by mutableStateOf("")
    var isProcessing by mutableStateOf(false)
    var processedMedia: GeminiMediaResponse? by mutableStateOf(null)
    var showBottomSheet by mutableStateOf(false)

    // Bottom Sheet tab & selection state
    var selectedMediaTab by mutableStateOf("Video") // "Video" or "Audio"
    var selectedVideoQuality by mutableStateOf<String?>(null)
    var selectedAudioQuality by mutableStateOf<String?>(null)

    // --- Database List State ---
    val downloadsFlow: StateFlow<List<DownloadItem>> = repository.allDownloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun processUrl(url: String) {
        if (url.isBlank()) return
        inputUrl = url
        viewModelScope.launch {
            isProcessing = true
            val response = repository.processMediaUrl(url)
            processedMedia = response
            isProcessing = false
            if (response != null && response.mediaItems.isNotEmpty()) {
                // Reset sheet selections
                selectedVideoQuality = null
                selectedAudioQuality = null
                selectedMediaTab = "Video"
                showBottomSheet = true
            }
        }
    }

    fun startDownloadSelection() {
        val media = processedMedia ?: return
        val isAudio = selectedMediaTab == "Audio"
        val quality = if (isAudio) selectedAudioQuality else selectedVideoQuality
        if (quality == null) return

        showBottomSheet = false

        viewModelScope.launch {
            // For playlists, we download each media item in the playlist
            if (media.isPlaylist) {
                media.mediaItems.forEach { item ->
                    repository.startDownload(
                        mediaItem = item,
                        isAudio = isAudio,
                        quality = quality,
                        isPlaylist = true,
                        playlistTitle = media.playlistTitle ?: "Playlist Downloads"
                    )
                }
            } else {
                // Single download
                media.mediaItems.firstOrNull()?.let { item ->
                    repository.startDownload(
                        mediaItem = item,
                        isAudio = isAudio,
                        quality = quality,
                        isPlaylist = false
                    )
                }
            }
            // Navigate to Downloads Manager page (1)
            currentScreen = 1
        }
    }

    fun deleteDownload(id: Int) {
        viewModelScope.launch {
            repository.deleteDownloadById(id)
            selectedDownloadIds.remove(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllDownloads()
            selectedDownloadIds.clear()
            isSelectionModeActive = false
        }
    }

    // --- Bulk 'Select Multiple' Feature State ---
    var isSelectionModeActive by mutableStateOf(false)
    val selectedDownloadIds = androidx.compose.runtime.mutableStateListOf<Int>()

    fun toggleSelection(id: Int) {
        if (selectedDownloadIds.contains(id)) {
            selectedDownloadIds.remove(id)
        } else {
            selectedDownloadIds.add(id)
        }
    }

    fun selectPlaylist(tracks: List<DownloadItem>, select: Boolean) {
        val ids = tracks.map { it.id }
        if (select) {
            ids.forEach { id ->
                if (!selectedDownloadIds.contains(id)) {
                    selectedDownloadIds.add(id)
                }
            }
        } else {
            selectedDownloadIds.removeAll(ids)
        }
    }

    fun deleteSelectedDownloads() {
        viewModelScope.launch {
            selectedDownloadIds.forEach { id ->
                repository.deleteDownloadById(id)
            }
            selectedDownloadIds.clear()
            isSelectionModeActive = false
        }
    }

    fun clearSelection() {
        selectedDownloadIds.clear()
        isSelectionModeActive = false
    }
}
