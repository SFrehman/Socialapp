package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.R
import com.example.data.DownloadItem
import com.example.data.GeminiMediaItem
import com.example.data.GeminiMediaResponse
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ChatViewModel

@Composable
fun MainAppScreen(
    appViewModel: AppViewModel,
    chatViewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val downloads by appViewModel.downloadsFlow.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // Header Block
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Agent Button (Top-Left)
                    IconButton(
                        onClick = { appViewModel.showAiAgentOverlay = true },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("ai_agent_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Agent Assistant",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Branding Title
                    Text(
                        text = "FlyMedia",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp
                    )

                    // Theme Toggle Button (Top-Right)
                    IconButton(
                        onClick = { appViewModel.toggleTheme() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (appViewModel.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Light/Dark Theme",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            bottomBar = {
                Column {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        NavigationBarItem(
                            selected = appViewModel.currentScreen == 0,
                            onClick = { appViewModel.currentScreen = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home Downloader") },
                            label = { Text("Downloader", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemColors(),
                            modifier = Modifier.testTag("tab_home")
                        )
                        NavigationBarItem(
                            selected = appViewModel.currentScreen == 1,
                            onClick = { appViewModel.currentScreen = 1 },
                            icon = { Icon(Icons.Default.Download, contentDescription = "Downloads Manager") },
                            label = { Text("Downloads", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemColors(),
                            modifier = Modifier.testTag("tab_downloads")
                        )
                        NavigationBarItem(
                            selected = appViewModel.currentScreen == 2,
                            onClick = { appViewModel.currentScreen = 2 },
                            icon = { Icon(Icons.Default.Info, contentDescription = "About Developer") },
                            label = { Text("About", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemColors(),
                            modifier = Modifier.testTag("tab_about")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Content Switcher
                when (appViewModel.currentScreen) {
                    0 -> MediaDownloaderScreen(appViewModel)
                    1 -> DownloadsManagerScreen(appViewModel, downloads)
                    2 -> AboutAppScreen(appViewModel)
                }

                // Global Signature Footer: subtly positioned just above the bottom bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "JaniDev",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                }

                // Smooth Custom Option Card / Bottom Sheet Overlay
                AnimatedOptionCard(
                    visible = appViewModel.showBottomSheet,
                    onDismiss = { appViewModel.showBottomSheet = false },
                    response = appViewModel.processedMedia,
                    viewModel = appViewModel
                )
            }
        }

        // Dedicated AI Chat Agent Overlay Page
        AnimatedVisibility(
            visible = appViewModel.showAiAgentOverlay,
            enter = fadeIn(animationSpec = tween(250)) + slideInVertically(initialOffsetY = { it }, animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200))
        ) {
            AiAgentOverlay(
                onDismiss = { appViewModel.showAiAgentOverlay = false },
                viewModel = chatViewModel
            )
        }
    }
}

@Composable
fun NavigationBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
)

// --- Page 1: Media Downloader (Home) ---
@Composable
fun MediaDownloaderScreen(viewModel: AppViewModel) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Icon / Banner Setup
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF5722), Color(0xFFFF9800))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "FlyMedia App Icon Logo",
                modifier = Modifier
                    .size(108.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "Stream & Download",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste any video or audio playlist URL to begin",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // State 1: Input & Processing Layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.inputUrl,
                onValueChange = { viewModel.inputUrl = it },
                label = { Text("Media URL or Playlist Link") },
                placeholder = { Text("https://www.youtube.com/watch?v=...") },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val clipText = clipboardManager.getText()?.text
                            if (!clipText.isNullOrBlank()) {
                                viewModel.inputUrl = clipText
                                Toast.makeText(context, "Pasted from clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste from Clipboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (viewModel.inputUrl.isNotBlank() && !viewModel.isProcessing) {
                            viewModel.processUrl(viewModel.inputUrl)
                        }
                    }
                ),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("url_input")
            )

            Button(
                onClick = { viewModel.processUrl(viewModel.inputUrl) },
                enabled = viewModel.inputUrl.isNotBlank() && !viewModel.isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("process_button"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                if (viewModel.isProcessing) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Analyzing with Gemini...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Text(
                        text = "Process Link",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- Page 1: State 2: Custom Sliding Animated Option Card (Bottom Sheet) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimatedOptionCard(
    visible: Boolean,
    onDismiss: () -> Unit,
    response: GeminiMediaResponse?,
    viewModel: AppViewModel
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // Dim Background Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { onDismiss() }
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(350))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sliding Handle Bar
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 16.dp)
                        .size(36.dp, 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )

                response?.let { res ->
                    // Media Header Section
                    val isPlaylist = res.isPlaylist
                    val headerTitle = if (isPlaylist) res.playlistTitle ?: "Playlist" else res.mediaItems.firstOrNull()?.title ?: "Unknown Title"
                    val sourceStr = res.mediaItems.firstOrNull()?.source ?: "Web Stream"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Media Thumbnail (Stylized glowing gradient thumbnail)
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondary)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaylist) Icons.Default.MusicNote else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = headerTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isPlaylist) "Playlist • ${res.mediaItems.size} items" else "Single Track",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Text(
                                    text = "Source: $sourceStr",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Segmented Control / Tabs (Video / Audio toggle)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val isVideoTab = viewModel.selectedMediaTab == "Video"
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isVideoTab) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.selectedMediaTab = "Video"
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (isVideoTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Video",
                                fontSize = 14.sp,
                                fontWeight = if (isVideoTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (isVideoTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (!isVideoTab) MaterialTheme.colorScheme.surface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.selectedMediaTab = "Audio"
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (!isVideoTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Audio",
                                fontSize = 14.sp,
                                fontWeight = if (!isVideoTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isVideoTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Options Grid View
                    val firstItem = res.mediaItems.firstOrNull()
                    if (firstItem != null) {
                        if (viewModel.selectedMediaTab == "Video") {
                            // Video resolutions
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                firstItem.videoOptions.forEach { opt ->
                                    val isSelected = viewModel.selectedVideoQuality == opt.resolution
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                                                    alpha = 0.5f
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                viewModel.selectedVideoQuality = opt.resolution
                                            }
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = opt.resolution,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isPlaylist) "~ ${opt.size} / track" else opt.size,
                                            fontSize = 10.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.7f
                                            ) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            // Audio options
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                firstItem.audioOptions.forEach { opt ->
                                    val isSelected = viewModel.selectedAudioQuality == opt.bitrate
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                                                    alpha = 0.5f
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                viewModel.selectedAudioQuality = opt.bitrate
                                            }
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = opt.bitrate,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isPlaylist) "~ ${opt.size} / track" else opt.size,
                                            fontSize = 10.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.7f
                                            ) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Download Trigger Button
                    val isTabVideo = viewModel.selectedMediaTab == "Video"
                    val isOptionSelected = if (isTabVideo) viewModel.selectedVideoQuality != null else viewModel.selectedAudioQuality != null

                    Button(
                        onClick = { viewModel.startDownloadSelection() },
                        enabled = isOptionSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("download_button"),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isPlaylist) "Download Playlist" else "Download Now",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// --- Page 2: Downloads Manager ---
@Composable
fun DownloadsManagerScreen(
    viewModel: AppViewModel,
    downloads: List<DownloadItem>
) {
    val context = LocalContext.current
    val activeDownloads = downloads.filter { it.status == "DOWNLOADING" || it.status == "PENDING" }
    val completedDownloads = downloads.filter { it.status == "COMPLETED" }

    // Dictionary of which playlists are expanded
    val expandedPlaylists = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-Selection Action Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel.isSelectionModeActive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.clearSelection() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Selection Mode",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "${viewModel.selectedDownloadIds.size} Selected",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.deleteSelectedDownloads() },
                            enabled = viewModel.selectedDownloadIds.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Active & Library",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (completedDownloads.isNotEmpty()) {
                            Text(
                                text = "Select Multiple",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable { viewModel.isSelectionModeActive = true }
                                    .padding(6.dp)
                            )
                        }

                        if (downloads.isNotEmpty()) {
                            Text(
                                text = "Clear History",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { viewModel.clearAllHistory() }
                                    .padding(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Downloads Section
        item {
            Text(
                text = "Active Downloads (${activeDownloads.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (activeDownloads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active downloads currently",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(activeDownloads, key = { it.id }) { item ->
                ActiveDownloadRow(item, onDelete = { viewModel.deleteDownload(item.id) })
            }
        }

        // Completed Downloads Section
        item {
            Text(
                text = "Completed Downloads (${completedDownloads.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (completedDownloads.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No completed media in library",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            // Group completed downloads
            val singleItems = completedDownloads.filter { it.playlistTitle == null }
            val playlistGroups = completedDownloads.filter { it.playlistTitle != null }.groupBy { it.playlistTitle!! }

            // Display grouped playlists
            playlistGroups.forEach { (playlistTitle, tracks) ->
                item {
                    val isExpanded = expandedPlaylists[playlistTitle] ?: false
                    val allTracksSelected = tracks.all { viewModel.selectedDownloadIds.contains(it.id) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (allTracksSelected && viewModel.isSelectionModeActive) 
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (allTracksSelected && viewModel.isSelectionModeActive) 1.5.dp else 1.dp,
                                color = if (allTracksSelected && viewModel.isSelectionModeActive) 
                                            MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (viewModel.isSelectionModeActive) {
                                        viewModel.selectPlaylist(tracks, !allTracksSelected)
                                    } else {
                                        expandedPlaylists[playlistTitle] = !isExpanded
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // If selection mode is active, show checkbox for the entire folder
                                if (viewModel.isSelectionModeActive) {
                                    Checkbox(
                                        checked = allTracksSelected,
                                        onCheckedChange = { viewModel.selectPlaylist(tracks, !allTracksSelected) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }

                                // Playlist High-Fidelity Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFF8BC34A), Color(0xFF009688))
                                            )
                                        )
                                ) {
                                    val playlistThumbnail = getThumbnailUrl(playlistTitle, isAudio = false, isPlaylist = true)
                                    AsyncImage(
                                        model = playlistThumbnail,
                                        contentDescription = "Playlist Thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = playlistTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${tracks.size} tracks • Folder: /Playlist/$playlistTitle",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            IconButton(onClick = { expandedPlaylists[playlistTitle] = !isExpanded }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse Playlist" else "Expand Playlist"
                                )
                            }
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                                tracks.forEach { track ->
                                    val isTrackSelected = viewModel.selectedDownloadIds.contains(track.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = if (isTrackSelected && viewModel.isSelectionModeActive)
                                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                                        else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                if (viewModel.isSelectionModeActive) {
                                                    viewModel.toggleSelection(track.id)
                                                } else {
                                                    saveToGalleryAndPlay(context, track, playImmediately = true)
                                                }
                                            }
                                            .padding(vertical = 6.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            if (viewModel.isSelectionModeActive) {
                                                Checkbox(
                                                    checked = isTrackSelected,
                                                    onCheckedChange = { viewModel.toggleSelection(track.id) },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = MaterialTheme.colorScheme.primary,
                                                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                )
                                            }

                                            // Track Visual Cover Thumbnail
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        brush = Brush.linearGradient(
                                                            colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                                                        )
                                                    )
                                            ) {
                                                val trackThumbnail = getThumbnailUrl(track.title, track.isAudio, isPlaylist = false)
                                                AsyncImage(
                                                    model = trackThumbnail,
                                                    contentDescription = "Track Thumbnail",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Black.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (track.isAudio) Icons.Default.MusicNote else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = track.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${track.quality} • ${track.totalSize} • Duration: ${track.duration}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (!viewModel.isSelectionModeActive) {
                                            IconButton(
                                                onClick = { viewModel.deleteDownload(track.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Delete Track",
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Display non-grouped (single) downloads
            items(singleItems, key = { it.id }) { item ->
                CompletedDownloadRow(
                    item = item,
                    isSelectionMode = viewModel.isSelectionModeActive,
                    isSelected = viewModel.selectedDownloadIds.contains(item.id),
                    onToggleSelection = { viewModel.toggleSelection(item.id) },
                    onDelete = { viewModel.deleteDownload(item.id) },
                    onClickPlay = { saveToGalleryAndPlay(context, item, playImmediately = true) }
                )
            }
        }

        // Space at bottom for subtle JaniDev footer
        item {
            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

@Composable
fun ActiveDownloadRow(
    item: DownloadItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isAudio) Icons.Default.MusicNote else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${item.quality} • Source: ${item.source}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Download",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Real-time progress bar
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

            // Progress text metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.downloadedSize} / ${item.totalSize}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ETA: ${item.eta}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(item.progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedDownloadRow(
    item: DownloadItem,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onDelete: () -> Unit,
    onClickPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isSelectionMode) {
                    onToggleSelection()
                } else {
                    onClickPlay()
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) 
                             else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.5.dp, 
            MaterialTheme.colorScheme.primary
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // If in selection mode, show a Checkbox
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelection() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // Media Thumbnail with play badge or cover art
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                            )
                        )
                ) {
                    val thumbnailUrl = getThumbnailUrl(item.title, item.isAudio, isPlaylist = false)
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = "Thumbnail for ${item.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isAudio) Icons.Default.MusicNote else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.quality} • ${item.totalSize} • Duration: ${item.duration}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isSelectionMode) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Completed Download",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// --- Page 3: About App ---
@Composable
fun AboutAppScreen(viewModel: AppViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Info Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FlyMedia",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Version 1.0.0",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "FlyMedia is a sleek and modern media downloader designed under clean Material Design 3 paradigms. Seamlessly download single audio/video files or complete playlists with intelligent subfolder structures that keep your system neat and organized.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        // Developer Profile Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Developer Profile",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Developer Avatar image (loaded safely from resources)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_avatar),
                        contentDescription = "Developer Avatar image of Saif Ur Rehman",
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Saif Ur Rehman",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Lead Android Architect & Developer",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clickable Email Support Mailto Link
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:docterpakistani@gmail.com")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast
                                    .makeText(
                                        context,
                                        "No Email app installed. Contact: docterpakistani@gmail.com",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email developer",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "docterpakistani@gmail.com",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// --- Page 4: AI Agent Chat Page Overlay ---
@Composable
fun AiAgentOverlay(
    onDismiss: () -> Unit,
    viewModel: ChatViewModel
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to App",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "FlyMedia AI Agent",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Always active to support you",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Chat History",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .imePadding() // CRITICAL UI CONSTRAINT: push input field above virtual keyboard
        ) {
            // Chat Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                reverseLayout = false
            ) {
                items(viewModel.messages) { (text, isUser) ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .background(
                                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.8f
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isUser) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.15f
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = text,
                                fontSize = 13.sp,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                if (viewModel.isThinking) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "AI Agent is typing...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Input Field Panel at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        value = viewModel.inputText,
                        onValueChange = { viewModel.inputText = it },
                        placeholder = { Text("Ask FlyMedia Assistant...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (viewModel.inputText.isNotBlank() && !viewModel.isThinking) {
                                    viewModel.sendMessage()
                                }
                            }
                        )
                    )

                    Button(
                        onClick = { viewModel.sendMessage() },
                        enabled = viewModel.inputText.isNotBlank() && !viewModel.isThinking,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chat_send_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Helper Functions for Media Playback & Gallery Saving ---

fun getThumbnailUrl(title: String, isAudio: Boolean, isPlaylist: Boolean): String {
    val lower = title.lowercase()
    return when {
        isPlaylist -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=200&auto=format&fit=crop"
        lower.contains("tropical") || lower.contains("chill") -> "https://images.unsplash.com/photo-1506157786151-b8491531f063?q=80&w=200&auto=format&fit=crop"
        lower.contains("beats") || lower.contains("deep") -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=200&auto=format&fit=crop"
        lower.contains("sunset") || lower.contains("golden") -> "https://images.unsplash.com/photo-1518173946687-a4c8a383392c?q=80&w=200&auto=format&fit=crop"
        !isAudio -> "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?q=80&w=200&auto=format&fit=crop"
        else -> "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=200&auto=format&fit=crop"
    }
}

fun saveToGalleryAndPlay(context: android.content.Context, item: DownloadItem, playImmediately: Boolean) {
    try {
        val resolver = context.contentResolver
        val filename = "${item.title.replace("[^a-zA-Z0-9]".toRegex(), "_")}_${item.quality}"
        
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
            if (item.isAudio) {
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_MUSIC + "/FlyMedia")
                }
            } else {
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_MOVIES + "/FlyMedia")
                }
            }
        }
        
        val collectionUri = if (item.isAudio) {
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        } else {
            android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        
        val itemUri = resolver.insert(collectionUri, contentValues)
        
        if (itemUri != null) {
            resolver.openOutputStream(itemUri)?.use { outputStream ->
                val dummyBytes = ByteArray(1024)
                outputStream.write(dummyBytes)
            }
            
            Toast.makeText(context, "Saved directly to Gallery / Media Folder!", Toast.LENGTH_SHORT).show()
            
            if (playImmediately) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(itemUri, if (item.isAudio) "audio/*" else "video/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        } else {
            val directory = if (item.isAudio) {
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC)
            } else {
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES)
            }
            val extension = if (item.isAudio) ".mp3" else ".mp4"
            val localFile = java.io.File(directory, "$filename$extension")
            if (!localFile.exists()) {
                localFile.createNewFile()
                localFile.writeBytes(ByteArray(1024))
            }
            
            val oldPolicy = android.os.StrictMode.getVmPolicy()
            val builder = android.os.StrictMode.VmPolicy.Builder()
            android.os.StrictMode.setVmPolicy(builder.build())
            
            val fileUri = Uri.fromFile(localFile)
            
            Toast.makeText(context, "Saved to Gallery folder in Internal Storage!", Toast.LENGTH_SHORT).show()
            
            if (playImmediately) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, if (item.isAudio) "audio/*" else "video/*")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            
            android.os.StrictMode.setVmPolicy(oldPolicy)
        }
    } catch (e: Exception) {
        android.util.Log.e("GalleryHelper", "Failed to save or play: ${e.message}", e)
        Toast.makeText(context, "Failed to register with Gallery, opening standard streaming player", Toast.LENGTH_SHORT).show()
        
        try {
            val webUri = Uri.parse(item.url)
            val intent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(context, "No compatible media player found!", Toast.LENGTH_LONG).show()
        }
    }
}
// Missing send icon in Default Icons, let's substitute with a custom icon or custom design, oh, Icons.Default.Send might be missing from standard icon-set or we can use standard Vector icons or ArrowForward! Let's import Icons.AutoMirrored.Filled.Send or standard custom drawings or ArrowBack/Icons.Default.Send! Actually, is Send available? Let's check: Icons.Default.Send is inside material-icons-extended, which we have added, or let's use a nice custom drawing or standard up arrow. Standard arrow works perfectly. Let's make sure it compiles. In Compose material-icons-extended, Icons.Default.Send is indeed fully present! Let's also import it: `import androidx.compose.material.icons.filled.Send` is correct.
