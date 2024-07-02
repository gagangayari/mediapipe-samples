package com.google.mediapipe.examples.llminference

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InferenceModel private constructor(context: Context) {
    private var llmInference: LlmInference

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    init {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(800)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    fun generateResponseAsync(prompt: String, isValidPrompt : Boolean) {
        if(isValidPrompt){
            var prePrompt = Prompts.EXTRACTWFHEntityPrompt(prompt)
            llmInference.generateResponseAsync(prePrompt)
        }
        else{
            llmInference.generateResponseAsync(prompt)
        }
    }

    fun isRAIValid(prompt: String): Boolean{
//        return true
        return !isPromptInjection(prompt) //&& !isToxic(prompt)
    }

    //This might provide an incorrect result
    fun isToxic(prompt: String) : Boolean{
        var res = llmInference.generateResponse(Prompts.TOXICITYPROMPT(prompt))
        if(res.contains("neutral", ignoreCase = true)){
            Log.i("RAI", "TOXICITY NEUTRAL $res")
            return false
        }
        else{
            Log.i("RAI", "TOXICITY $res")
            return true
        }
    }

    //This might provide an incorrect result
    fun isPromptInjection(prompt: String) : Boolean{
        var res = llmInference.generateResponse(Prompts.INJECTIONPROMPT(prompt))

        if (res.contains("###")){
            val firstPart = res.split("###").first()

            if(firstPart.contains("injection", ignoreCase = true)){
                    Log.i("RAI","PROMPT INJECTION : $res")
                    return true
                }
                else{
                    return false
                }
            }


        else{

            if (res.contains("injection", ignoreCase = true)){
                Log.i("RAI","PROMPT INJECTION : $res")
                return true
            }
            else{
                return false
            }


        }




    }

    companion object {

        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        val file = File(downloadsDirectory, "gemma-1.1-2b-it-cpu-int4.bin")
        private const val MODEL_PATH = "/data/local/tmp/llm/gemma-1.1-2b-it-cpu-int4.bin"
//        private var MODEL_PATH = file.absolutePath
        private var instance: InferenceModel? = null


        fun getInstance(context: Context): InferenceModel {
            return if (instance != null) {
                instance!!
            } else {
                InferenceModel(context).also { instance = it }
            }
        }
    }
}
