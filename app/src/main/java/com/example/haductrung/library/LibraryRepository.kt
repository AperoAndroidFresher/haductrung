package com.example.haductrung.library

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.example.haductrung.library.minicomposable.Song
import java.io.File
import java.util.concurrent.TimeUnit

class LibraryRepository(private val context: Context) {

    @SuppressLint("DefaultLocale")
    fun getAudioData(): List<Song> {
        val songList = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val durationMs = cursor.getLong(durationColumn)

                //lấy ảnh của tuwengf bài
                val songUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val mmr = MediaMetadataRetriever()
                var artUri: Uri? = null

                try {
                    mmr.setDataSource(context, songUri)
                    val artBytes = mmr.embeddedPicture
                    if (artBytes != null) {
                        val tempFile = File.createTempFile("albumart", ".jpg", context.cacheDir)
                        tempFile.writeBytes(artBytes)
                        artUri = Uri.fromFile(tempFile)
                    }
                } catch (_: Exception) {

                } finally {
                    mmr.release()
                }
                val durationFormatted = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
                )

                songList.add(
                    Song(
                        id = id.toInt(),
                        title = title,
                        artist = artist,
                        duration = durationFormatted,
                        albumArtUri = artUri
                    )
                )
            }
        }
        return songList
    }
    suspend fun getSongsByIds(ids: List<Int>): List<Song> {
        // Lấy tất cả bài hát
        val allSongs = getAudioData()
        // Lọc ra những bài hát có id nằm trong danh sách ids được truyền vào
        return allSongs.filter { song -> ids.contains(song.id) }
    }
}