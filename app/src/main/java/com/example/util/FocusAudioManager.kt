package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

enum class AmbientTrack {
    NONE,
    RAIN,
    MELODY,
    WAVES,
    CUSTOM
}

object FocusAudioManager {
    private var audioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private var currentPlaylist: List<Uri> = emptyList()
    private var currentPlaylistIndex: Int = 0

    fun startTrack(context: Context? = null, track: AmbientTrack, customUri: Uri? = null, customPlaylist: List<Uri> = emptyList()) {
        stopTrack()
        if (track == AmbientTrack.NONE) return

        val playlistToPlay = if (customPlaylist.isNotEmpty()) customPlaylist else if (customUri != null) listOf(customUri) else emptyList()

        if (track == AmbientTrack.CUSTOM && context != null && playlistToPlay.isNotEmpty()) {
            currentPlaylist = playlistToPlay
            currentPlaylistIndex = 0
            playCustomIndex(context, 0)
            return
        }

        playbackJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            val buffer = ShortArray(1024)
            var phase = 0.0
            var wavePhase = 0.0

            while (isActive) {
                for (i in buffer.indices) {
                    when (track) {
                        AmbientTrack.RAIN -> {
                            // Rain noise with soft low-pass filter and random pitter-patter drops
                            val noise = (Random.nextFloat() * 2f - 1f) * 0.15f
                            val drop = if (Random.nextFloat() > 0.998f) 0.5f else 0.0f
                            val sample = (noise + drop).coerceIn(-1.0f, 1.0f)
                            buffer[i] = (sample * 8000).toInt().toShort()
                        }
                        AmbientTrack.MELODY -> {
                            // Ambient Pentatonic chord generator (soft sine wave combination)
                            val f1 = 220.0 // A3
                            val f2 = 277.18 // C#4
                            val f3 = 329.63 // E4
                            val f4 = 440.0 // A4
                            phase += 2.0 * Math.PI / sampleRate
                            val val1 = sin(phase * f1) * 0.25
                            val val2 = sin(phase * f2 * 1.001) * 0.2
                            val val3 = sin(phase * f3 * 0.999) * 0.15
                            val val4 = sin(phase * f4) * 0.1
                            val total = (val1 + val2 + val3 + val4) * 0.3
                            buffer[i] = (total * 32767).toInt().toShort()
                        }
                        AmbientTrack.WAVES -> {
                            // Ocean tide breathing swell
                            wavePhase += 2.0 * Math.PI / (sampleRate * 6.0) // 6 second swell cycle
                            val swell = (sin(wavePhase) + 1.0) * 0.5
                            val noise = (Random.nextFloat() * 2f - 1f) * swell * 0.25f
                            buffer[i] = (noise * 16000).toInt().toShort()
                        }
                        else -> {
                            buffer[i] = 0
                        }
                    }
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
    }

    fun playSuccessSound() {
        stopTrack()
        playbackJob = scope.launch {
            val sampleRate = 22050
            val frequencies = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            val noteDurationMs = 250
            val noteSamples = (sampleRate * noteDurationMs) / 1000

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(noteSamples * 2 * frequencies.size)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.play()

            for (freq in frequencies) {
                if (!isActive) break
                val buffer = ShortArray(noteSamples)
                var phase = 0.0
                for (i in buffer.indices) {
                    phase += 2.0 * Math.PI * freq / sampleRate
                    val env = (1.0 - i.toDouble() / noteSamples) // Fade out
                    val sample = sin(phase) * env * 0.4
                    buffer[i] = (sample * 32767).toInt().toShort()
                }
                track.write(buffer, 0, buffer.size)
            }
            try {
                track.stop()
                track.release()
            } catch (e: Exception) { }
        }
    }

    fun playErrorSound() {
        stopTrack()
        playbackJob = scope.launch {
            val sampleRate = 22050
            val totalDurationMs = 600
            val totalSamples = (sampleRate * totalDurationMs) / 1000

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(totalSamples * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            track.play()

            val buffer = ShortArray(totalSamples)
            var phase = 0.0
            for (i in buffer.indices) {
                val progress = i.toDouble() / totalSamples
                val freq = 220.0 * (1.0 - progress * 0.6) // 220Hz down to 88Hz
                phase += 2.0 * Math.PI * freq / sampleRate
                // Add squareish overtone for buzzing warning feel
                val val1 = sin(phase)
                val val2 = if (val1 > 0) 0.2 else -0.2
                val env = 1.0 - progress
                val sample = (val1 * 0.3 + val2) * env * 0.5
                buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)

            try {
                track.stop()
                track.release()
            } catch (e: Exception) { }
        }
    }

    private fun playCustomIndex(context: Context, index: Int) {
        if (currentPlaylist.isEmpty()) return
        val safeIndex = index % currentPlaylist.size
        currentPlaylistIndex = safeIndex
        val uri = currentPlaylist[safeIndex]

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(context, uri)
                if (currentPlaylist.size == 1) {
                    isLooping = true
                } else {
                    isLooping = false
                    setOnCompletionListener {
                        val nextIndex = (currentPlaylistIndex + 1) % currentPlaylist.size
                        playCustomIndex(context, nextIndex)
                    }
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (currentPlaylist.size > 1) {
                val nextIndex = (currentPlaylistIndex + 1) % currentPlaylist.size
                if (nextIndex != index) {
                    playCustomIndex(context, nextIndex)
                }
            }
        }
    }

    fun stopTrack() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore cleanup exceptions
        }
        audioTrack = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignore cleanup exceptions
        }
        mediaPlayer = null
        currentPlaylist = emptyList()
        currentPlaylistIndex = 0
    }
}
