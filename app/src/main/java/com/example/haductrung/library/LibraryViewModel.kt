package com.example.haductrung.library

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.example.haductrung.repository.SongRepository
import com.example.haductrung.database.entity.SongEntity
import java.util.concurrent.TimeUnit
import com.example.haductrung.library.remote.ApiClient
import com.example.haductrung.library.remote.CachedSongMetadata
import com.example.haductrung.library.remote.RemoteSong
import com.example.haductrung.repository.LibraryRepository
import com.example.haductrung.repository.Song
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL


class LibraryViewModel(
    private val songRepository: SongRepository,
    private val mediaStoreScanner: LibraryRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LibraryEvent>()
    val event = _event.asSharedFlow()

    init {
        loadLocalSongsFromDevice()
    }

    fun processIntent(intent: LibraryIntent) {
        when (intent) {
            is LibraryIntent.OnTabSelected -> handleTabSelection(intent.tab)
            is LibraryIntent.RetryFetchRemoteSongs -> fetchRemoteSongs()
            is LibraryIntent.OnToggleViewClick -> _state.update { it.copy(isGridView = !it.isGridView) }
            is LibraryIntent.OnToggleSortClick -> _state.update { it.copy(isSortMode = !it.isSortMode) }
            is LibraryIntent.OnMoreClick -> _state.update { it.copy(
                songWithMenu = intent.song.id,
                selectedSongId = intent.song.id
            ) }
            is LibraryIntent.OnDismissMenu -> _state.update { it.copy(songWithMenu = null) }
            is LibraryIntent.CheckAndLoadSongs -> checkPermissionAndLoad()
            is LibraryIntent.OnRequestPermissionAgain -> viewModelScope.launch {
                _event.emit(
                    LibraryEvent.RequestPermission
                )
            }

            is LibraryIntent.OnAddToPlaylistClick -> {
                addSongToLibraryAndNavigate(intent.song)
            }
            is LibraryIntent.OnSongSelected -> {
                _state.update { it.copy(selectedSongId = intent.songId) }
            }
        }
    }
    private fun handleTabSelection(tab: LibraryTab) {
        _state.update { it.copy(selectedTab = tab) }
        if (tab == LibraryTab.REMOTE) {
            fetchRemoteSongs()
        } else {
            loadLocalSongsFromDevice()
        }
    }
    private suspend fun downloadAndSaveSong(songUrl: String, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            try {
                val musicDir = File(applicationContext.filesDir, "music")
                if (!musicDir.exists()) {
                    musicDir.mkdirs()
                }
                val destinationFile = File(musicDir, fileName)
                if (destinationFile.exists()) {
                    return@withContext destinationFile
                }
                URL(songUrl).openStream().use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
                destinationFile
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
    private fun extractAlbumArt(filePath: String): Uri? {
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(filePath)
            val artBytes = mmr.embeddedPicture
            if (artBytes != null) {
                val tempFile = File.createTempFile("albumart_", ".jpg", applicationContext.cacheDir)
                tempFile.writeBytes(artBytes)
                Uri.fromFile(tempFile)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            mmr.release()
        }
    }
    private fun fetchRemoteSongs() {
        if (state.value.remoteState is RemoteState.Success) return

        _state.update { it.copy(remoteState = RemoteState.Loading) }
        viewModelScope.launch {
            try {
                val remoteSongs = ApiClient.build().getRemoteSongs()
                val uiSongs = mapRemoteToUiSongs(remoteSongs)
                _state.update { it.copy(remoteState = RemoteState.Success(uiSongs)) }

            } catch (_: Exception) {
                val cachedSongs = loadSongsFromInternalCache()
                if (cachedSongs.isNotEmpty()) {
                    _state.update { it.copy(remoteState = RemoteState.Success(cachedSongs)) }
                } else {
                    _state.update { it.copy(remoteState = RemoteState.Error("No internet and no cached songs")) }
                }
            }
        }
    }
    private fun addSongToLibraryAndNavigate (song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingSong = songRepository.findSongByFilePath(song.filePath)

            val songIdToNavigate: Int

            if (existingSong != null) {
                songIdToNavigate = existingSong.songId
            } else {
                val songEntity = SongEntity(
                    title = song.title,
                    artist = song.artist,
                    durationMs = song.durationMs,
                    filePath = song.filePath,
                    albumArtUri = song.albumArtUri?.toString()
                )

                songIdToNavigate = songRepository.insertSong(songEntity).toInt()
            }
            val songWithDbId = song.copy(id = songIdToNavigate)


            _event.emit(LibraryEvent.NavigateToAddToPlaylistScreen(songWithDbId))

            _state.update { it.copy(songWithMenu = null) }
        }
    }

    private fun loadLocalSongsFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceSongs = mediaStoreScanner.getAudioData()
            _state.update { it.copy(songList = deviceSongs) }
        }
    }
    @SuppressLint("DefaultLocale")
    private fun loadSongsFromInternalCache(): List<Song> {
        val musicDir = File(applicationContext.filesDir, "music")
        if (!musicDir.exists() || !musicDir.isDirectory) return emptyList()

        val gson = Gson()
        return musicDir.listFiles { file -> file.extension.equals("mp3", ignoreCase = true) }
            ?.mapNotNull { mp3File ->
                try {
                    val metadataFile = File(musicDir, "${mp3File.nameWithoutExtension}.json")
                    if (!metadataFile.exists()) return@mapNotNull null
                    val jsonString = metadataFile.readText()
                    val metadata = gson.fromJson(jsonString, CachedSongMetadata::class.java)
                    val albumArtUri = extractAlbumArt(mp3File.absolutePath)
                    val durationFormatted = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(metadata.durationMs),
                        TimeUnit.MILLISECONDS.toSeconds(metadata.durationMs) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(metadata.durationMs))
                    )

                    Song(
                        id = mp3File.absolutePath.hashCode(),
                        title = metadata.title,
                        artist = metadata.artist,
                        duration = durationFormatted,
                        durationMs = metadata.durationMs,
                        filePath = mp3File.absolutePath,
                        albumArtUri = albumArtUri
                    )
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList()
    }
    private fun checkPermissionAndLoad() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val isGranted =
            applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        _state.update { it.copy(hasPermission = isGranted) }
        if (isGranted) {
            loadLocalSongsFromDevice()
        } else {
            viewModelScope.launch { _event.emit(LibraryEvent.RequestPermission) }
        }
    }
    @SuppressLint("DefaultLocale")
    private suspend fun mapRemoteToUiSongs(remoteSongs: List<RemoteSong>): List<Song> {
        val gson = Gson()
        return remoteSongs.mapNotNull { remote ->
            val fullUrl = remote.path
            val fileName = remote.path.substringAfterLast('/')
            val downloadedFile = downloadAndSaveSong(fullUrl, fileName)

            if (downloadedFile != null) {
                try {
                    val durationMs = remote.duration.toLongOrNull() ?: 0L
                    val metadata = CachedSongMetadata(remote.title, remote.artist, durationMs)
                    val jsonString = gson.toJson(metadata)

                    val metadataFile =
                        File(downloadedFile.parent, "${downloadedFile.nameWithoutExtension}.json")
                    metadataFile.writeText(jsonString)

                } catch (e: Exception) {
                    Log.e("RemoteDebug", "Lỗi khi lưu metadata cho file: $fileName", e)
                }
                val albumArtUri = extractAlbumArt(downloadedFile.absolutePath)
                val durationMs = remote.duration.toLongOrNull() ?: 0L
                val durationFormatted = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
                )
                Song(
                    id = remote.path.hashCode(),
                    title = remote.title,
                    artist = remote.artist,
                    duration = durationFormatted,
                    durationMs = durationMs,
                    filePath = downloadedFile.absolutePath,
                    albumArtUri = albumArtUri
                )
            } else {
                null
            }
        }
    }
}