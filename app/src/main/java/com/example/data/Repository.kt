package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(private val downloadDao: DownloadDao) {

    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads()

    suspend fun getDownloadById(id: Int): DownloadItem? = downloadDao.getDownloadById(id)

    suspend fun deleteDownloadById(id: Int) = downloadDao.deleteDownloadById(id)

    suspend fun clearAllDownloads() = downloadDao.clearAllDownloads()

    /**
     * Call Gemini to extract metadata from a URL.
     */
    suspend fun processMediaUrl(url: String): GeminiMediaResponse? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("Repository", "Gemini API key is not configured!")
            return@withContext getFallbackMetadata(url)
        }

        val prompt = """
            You are a media metadata parser for FlyMedia.
            Parse the following URL: "$url"
            
            Determine:
            1. If it is a playlist or collection (contains "playlist", "list=", "album", or multiple items context).
            2. Extract/generate realistic media items with titles, sources (YouTube, TikTok, Instagram, Facebook, etc.), and durations.
            3. Provide realistic video resolutions (1080p, 720p, 480p, 360p) with estimated file sizes (e.g., "45 MB", "28 MB").
            4. Provide realistic audio bitrates (320 kbps, 256 kbps, 128 kbps) with estimated file sizes (e.g., "10 MB", "8 MB").
            
            Return ONLY a valid JSON matching this schema:
            {
              "isPlaylist": true/false,
              "playlistTitle": "Name of playlist or null if single video",
              "mediaItems": [
                {
                  "id": "unique_string_id",
                  "title": "Title of the video/track",
                  "source": "YouTube",
                  "duration": "3:45",
                  "videoOptions": [
                    {"resolution": "1080p", "size": "45 MB"},
                    {"resolution": "720p", "size": "28 MB"},
                    {"resolution": "480p", "size": "12 MB"},
                    {"resolution": "360p", "size": "8 MB"}
                  ],
                  "audioOptions": [
                    {"bitrate": "320 kbps", "size": "8.5 MB"},
                    {"bitrate": "256 kbps", "size": "6.8 MB"},
                    {"bitrate": "128 kbps", "size": "3.4 MB"}
                  ]
                }
              ]
            }
            
            Ensure the JSON is strictly correct and has no extra markdown blocks or text.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            )
        )

        try {
            val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                val adapter = GeminiRetrofitClient.moshiInstance.adapter(GeminiMediaResponse::class.java)
                return@withContext adapter.fromJson(jsonText)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error processing URL via Gemini, using fallback: ${e.message}")
        }

        return@withContext getFallbackMetadata(url)
    }

    /**
     * Ask the AI Agent for responses.
     */
    suspend fun chatWithAgent(message: String, history: List<Pair<String, Boolean>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Hi there! I am the FlyMedia AI Agent. Currently, the Gemini API key is not configured in your Secrets panel, but I can still tell you about FlyMedia! FlyMedia is a sleek media downloader developed by Saif Ur Rehman (JaniDev). It supports offline-first Room database storage, custom playlist grouping, and direct OS share sheet target integrations. Please configure your GEMINI_API_KEY to unlock full intelligent responses!"
        }

        val systemPrompt = """
            You are FlyMedia's AI Agent assistant. 
            FlyMedia is a sleek, feature-rich media playlist and single video/audio downloader.
            Key Features of FlyMedia:
            1. Share Target Integration: Automatically launches and processes links shared from YouTube, TikTok, Instagram, Facebook, etc.
            2. Playlist Processing: Automatically groups playlist downloads into folders so they don't clutter the root directory.
            3. Custom Qualities: Downloader Bottom Sheet with Segmented tabs allowing Video/Audio selection with distinct available qualities and active/disabled action triggers.
            4. Downloads Manager: Real-time downloading progress, file sizes, percent indicators, ETAs, and collapsible playlist grouping views.
            5. Sleek Interface Theme: Applied modern Material Design 3 guidelines, custom gradients, and a Light/Dark toggle.
            
            Developer Profile:
            - Name: Saif Ur Rehman
            - Role: Developer / Lead Architect
            - Contact: docterpakistani@gmail.com (clickable mailto link in About App)
            - Signature: JaniDev (renders subtly at the bottom center of the app)
            
            Be friendly, helpful, concise, and professional. Speak with a clear, direct, and slightly engaging developer assistant tone. Keep explanations simple and visual.
        """.trimIndent()

        // Build dialogue history
        val conversationParts = mutableListOf<Part>()
        history.forEach { (msg, isUser) ->
            conversationParts.add(Part(text = "${if (isUser) "User" else "Agent"}: $msg"))
        }
        conversationParts.add(Part(text = "User: $message\nAgent:"))

        val request = GeminiRequest(
            contents = listOf(Content(parts = conversationParts)),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
            return@withContext response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize, but I couldn't generate a response right now. Please try again!"
        } catch (e: Exception) {
            Log.e("Repository", "Error calling Gemini chat: ${e.message}")
            return@withContext "It seems there was a network connection error: ${e.localizedMessage}. Please check your connection and verify your API key in the Secrets panel!"
        }
    }

    /**
     * Start downloading a media item (simulated progress updates in a background coroutine).
     */
    fun startDownload(
        mediaItem: GeminiMediaItem,
        isAudio: Boolean,
        quality: String,
        isPlaylist: Boolean,
        playlistTitle: String? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            // Retrieve size from option
            val totalSizeStr = if (isAudio) {
                mediaItem.audioOptions.find { it.bitrate == quality }?.size ?: "8.0 MB"
            } else {
                mediaItem.videoOptions.find { it.resolution == quality }?.size ?: "25.0 MB"
            }

            // Create initial database item
            val dbItem = DownloadItem(
                url = "https://example.com/download/${mediaItem.id}",
                title = mediaItem.title,
                source = mediaItem.source,
                duration = mediaItem.duration,
                isPlaylist = isPlaylist,
                playlistTitle = playlistTitle,
                status = "DOWNLOADING",
                progress = 0.0f,
                downloadedSize = "0 MB",
                totalSize = totalSizeStr,
                eta = "calculating...",
                isAudio = isAudio,
                quality = quality
            )

            val id = downloadDao.insertDownload(dbItem).toInt()
            
            // Extract numeric total size
            val totalSizeNum = try {
                totalSizeStr.replace("MB", "").trim().toFloat()
            } catch (e: Exception) {
                15.0f
            }

            var currentProgress = 0.0f
            val steps = 15 // Simulating over 15 steps
            val stepDelay = 800L // 800ms per step (approx 12s total)

            while (currentProgress < 1.0f) {
                delay(stepDelay)
                currentProgress += (1.0f / steps) + (Math.random().toFloat() * 0.05f)
                if (currentProgress > 1.0f) currentProgress = 1.0f

                val downloadedSizeStr = String.format("%.1f MB", currentProgress * totalSizeNum)
                val remainingSeconds = ((1f - currentProgress) * (steps * stepDelay / 1000f)).toInt()
                val etaStr = if (remainingSeconds > 0) "${remainingSeconds}s" else "0s"

                val updatedItem = downloadDao.getDownloadById(id)
                if (updatedItem != null) {
                    if (currentProgress >= 1.0f) {
                        downloadDao.updateDownload(
                            updatedItem.copy(
                                status = "COMPLETED",
                                progress = 1.0f,
                                downloadedSize = totalSizeStr,
                                eta = "Finished"
                            )
                        )
                    } else {
                        downloadDao.updateDownload(
                            updatedItem.copy(
                                progress = currentProgress,
                                downloadedSize = downloadedSizeStr,
                                eta = etaStr
                            )
                        )
                    }
                } else {
                    // Item was deleted during download
                    break
                }
            }
        }
    }

    /**
     * Fallback parser if Gemini API key is missing or fails.
     */
    private fun getFallbackMetadata(url: String): GeminiMediaResponse {
        val cleanUrl = url.trim().lowercase()
        val source = when {
            cleanUrl.contains("youtube") || cleanUrl.contains("youtu.be") -> "YouTube"
            cleanUrl.contains("tiktok") -> "TikTok"
            cleanUrl.contains("instagram") -> "Instagram"
            cleanUrl.contains("facebook") -> "Facebook"
            else -> "Web"
        }

        val isPlaylist = cleanUrl.contains("list=") || cleanUrl.contains("playlist") || cleanUrl.contains("album")
        val playlistTitle = if (isPlaylist) "Summer Vibes Remix Collection" else null

        val items = if (isPlaylist) {
            listOf(
                GeminiMediaItem(
                    id = "pl_1",
                    title = "Tropical Chill House Mix 2026",
                    source = source,
                    duration = "4:32",
                    videoOptions = listOf(
                        GeminiMediaOption("1080p", "52 MB"),
                        GeminiMediaOption("720p", "31 MB"),
                        GeminiMediaOption("480p", "14 MB"),
                        GeminiMediaOption("360p", "9 MB")
                    ),
                    audioOptions = listOf(
                        GeminiAudioOption("320 kbps", "10.1 MB"),
                        GeminiAudioOption("256 kbps", "8.1 MB"),
                        GeminiAudioOption("128 kbps", "4.0 MB")
                    )
                ),
                GeminiMediaItem(
                    id = "pl_2",
                    title = "Deep Beats Acoustic Summer Sessions",
                    source = source,
                    duration = "3:15",
                    videoOptions = listOf(
                        GeminiMediaOption("1080p", "38 MB"),
                        GeminiMediaOption("720p", "22 MB"),
                        GeminiMediaOption("480p", "10 MB"),
                        GeminiMediaOption("360p", "6 MB")
                    ),
                    audioOptions = listOf(
                        GeminiAudioOption("320 kbps", "7.4 MB"),
                        GeminiAudioOption("256 kbps", "5.9 MB"),
                        GeminiAudioOption("128 kbps", "2.9 MB")
                    )
                ),
                GeminiMediaItem(
                    id = "pl_3",
                    title = "Golden Hour sunset vibes",
                    source = source,
                    duration = "5:10",
                    videoOptions = listOf(
                        GeminiMediaOption("1080p", "60 MB"),
                        GeminiMediaOption("720p", "36 MB"),
                        GeminiMediaOption("480p", "16 MB"),
                        GeminiMediaOption("360p", "11 MB")
                    ),
                    audioOptions = listOf(
                        GeminiAudioOption("320 kbps", "11.8 MB"),
                        GeminiAudioOption("256 kbps", "9.4 MB"),
                        GeminiAudioOption("128 kbps", "4.7 MB")
                    )
                )
            )
        } else {
            val title = if (cleanUrl.length > 15 && cleanUrl.startsWith("http")) {
                "Amazing 4K Media Stream • " + source
            } else if (cleanUrl.isNotEmpty() && !cleanUrl.startsWith("http")) {
                url
            } else {
                "Relaxing Ambient Sunset Beats"
            }
            listOf(
                GeminiMediaItem(
                    id = "single_1",
                    title = title,
                    source = source,
                    duration = "3:50",
                    videoOptions = listOf(
                        GeminiMediaOption("1080p", "45 MB"),
                        GeminiMediaOption("720p", "28 MB"),
                        GeminiMediaOption("480p", "12 MB"),
                        GeminiMediaOption("360p", "8 MB")
                    ),
                    audioOptions = listOf(
                        GeminiAudioOption("320 kbps", "8.5 MB"),
                        GeminiAudioOption("256 kbps", "6.8 MB"),
                        GeminiAudioOption("128 kbps", "3.4 MB")
                    )
                )
            )
        }

        return GeminiMediaResponse(
            isPlaylist = isPlaylist,
            playlistTitle = playlistTitle,
            mediaItems = items
        )
    }
}
