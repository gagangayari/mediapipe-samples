/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mediapipe.examples.audioembedder

import android.content.Context
import android.media.AudioFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.audio.audioembedder.AudioEmbedder
import com.google.mediapipe.tasks.audio.audioembedder.AudioEmbedder.AudioEmbedderOptions
import com.google.mediapipe.tasks.audio.core.RunningMode
import com.google.mediapipe.tasks.components.containers.AudioData
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioEmbedderHelper(
    private val context: Context,
    var currentDelegate: Int = DELEGATE_CPU,
    var listener: EmbedderListener? = null
) {
    private var audioEmbedder: AudioEmbedder? = null

    init {
        setupAudioEmbedder()
    }

    fun setupAudioEmbedder() {
        val baseOptionsBuilder = BaseOptions.builder()
        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionsBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                baseOptionsBuilder.setDelegate(Delegate.GPU)
            }
        }

        baseOptionsBuilder.setModelAssetPath(MODEL_PATH)

        try {
            val baseOptions = baseOptionsBuilder.build()
            val optionsBuilder =
                AudioEmbedderOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setRunningMode(RunningMode.AUDIO_CLIPS)
            val options = optionsBuilder.build()
            audioEmbedder = AudioEmbedder.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError(
                "Audio embedder failed to initialize. See error logs for " +
                        "details"
            )
            Log.e(
                TAG,
                "Audio embedder failed to load model with error: " + e.message
            )
        } catch (e: RuntimeException) {
            // This occurs if the model being used does not support GPU
            listener?.onError(
                "Audio embedder failed to initialize. See error logs for " +
                        "details", GPU_ERROR
            )
            Log.e(
                TAG,
                "Audio embedder failed to load model with error: " + e.message
            )
        }
    }

    //  If both the vectors are aligned, the angle between them
    //  will be 0. cos 0 = 1. So, mathematically, this distance metric will
    //  be used to find the most similar audio.
    fun compare(
        firstAudioData: AudioData,
        secondAudioData: AudioData
    ): ResultBundle? {
        // Inference time is the difference between the system time at the start and finish of the
        // process
        val startTime = SystemClock.uptimeMillis()
        audioEmbedder?.let {
            val firstEmbed =
                it.embed(firstAudioData).embeddingResultList().get().first()
                    .embeddings()
                    .first()
            val secondEmbed =
                it.embed(secondAudioData).embeddingResultList().get().first()
                    .embeddings()
                    .first()
            val inferenceTimeMs = SystemClock.uptimeMillis() - startTime
            return ResultBundle(
                AudioEmbedder.cosineSimilarity(firstEmbed, secondEmbed),
                inferenceTimeMs
            )
        }
        return null
    }

    fun clearAudioEmbedder() {
        audioEmbedder?.close()
        audioEmbedder = null
    }

    // Wraps results from inference, the time it takes for inference to be
    // performed.
    data class ResultBundle(
        val similarity: Double,
        val inferenceTime: Long,
    )

    companion object {
        const val MODEL_PATH = "yamnet_embedding_metadata.tflite"
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        const val YAMNET_EXPECTED_INPUT_LENGTH = 0.975F
        private const val TAG = "AudioEmbedderHelper"
    }

    interface EmbedderListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
    }
}

fun Uri.createAudioData(context: Context): AudioData {
    val inputStream = context.contentResolver.openInputStream(this)
    val dataInputStream = DataInputStream(inputStream)
    val targetArray = ByteArray(dataInputStream.available())
    dataInputStream.read(targetArray)
    val audioFloatArrayData = targetArray.toShortArray()

    // get audio's duration
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(context, this)
    val durationStr =
        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val audioDuration = durationStr!!.toInt()

    // calculate the sample rate
    val expectedSampleRate =
        audioFloatArrayData.size / (audioDuration / 1000F / AudioEmbedderHelper.YAMNET_EXPECTED_INPUT_LENGTH)

    // create audio data
    val audioData = AudioData.create(
        AudioData.AudioDataFormat.builder().setNumOfChannels(
            AudioFormat.CHANNEL_IN_DEFAULT
        ).setSampleRate(expectedSampleRate).build(), audioFloatArrayData.size
    )
    audioData.load(audioFloatArrayData)
    return audioData
}

fun ByteArray.toShortArray(): ShortArray {
    val result = ShortArray(this.size / 2)
    ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        .get(result)
    return result
}
