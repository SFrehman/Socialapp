package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Repository
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database.downloadDao())

    // Message list of Pair(MessageText, IsUserMessage)
    var messages by mutableStateOf(listOf<Pair<String, Boolean>>(
        Pair("Hello! I am FlyMedia's AI Assistant. How can I help you today with your media downloads, playlists, or app features?", false)
    ))
        private set

    var inputText by mutableStateOf("")
    var isThinking by mutableStateOf(false)
        private set

    fun sendMessage() {
        val query = inputText.trim()
        if (query.isEmpty() || isThinking) return

        // Clear input box
        inputText = ""

        // Add to history
        val updatedMessages = messages.toMutableList()
        updatedMessages.add(Pair(query, true))
        messages = updatedMessages

        isThinking = true

        viewModelScope.launch {
            val response = repository.chatWithAgent(query, messages.dropLast(1))
            val nextMessages = messages.toMutableList()
            nextMessages.add(Pair(response, false))
            messages = nextMessages
            isThinking = false
        }
    }

    fun clearChat() {
        messages = listOf(
            Pair("Hello! I am FlyMedia's AI Assistant. How can I help you today with your media downloads, playlists, or app features?", false)
        )
    }
}
