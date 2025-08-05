package com.example.haductrung.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.util.concurrent.TimeUnit

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String,
    val durationMs: Long,
    val filePath: String,
    val albumArtUri: Uri?
)

class LibraryRepository(private val context: Context) {

    @SuppressLint("Range", "DefaultLocale")
    fun getAudioData(): List<Song> {
        val songList = mutableListOf<Song>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val durationMs = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                val songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                val mmr = MediaMetadataRetriever()
                // biến embedded dùng để chứa ảnh bìa nhúng
                var embeddedArtUri: Uri? = null
                try {
                    mmr.setDataSource(context, songUri)
                    val artBytes = mmr.embeddedPicture
                    if (artBytes != null) {
                        val tempFile = File.createTempFile("albumart_", ".jpg", context.cacheDir)
                        tempFile.writeBytes(artBytes)
                        embeddedArtUri = Uri.fromFile(tempFile)
                    }
                } catch (e: Exception) {
                    embeddedArtUri = null
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
                        albumArtUri = embeddedArtUri,
                        durationMs = durationMs,
                        filePath = path
                    )
                )
            }
        }
        return songList
    }
}
