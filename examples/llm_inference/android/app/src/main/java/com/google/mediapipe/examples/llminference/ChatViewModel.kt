package com.google.mediapipe.examples.llminference

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class ChatViewModel(
    private val inferenceModel: InferenceModel,
    private var toAppend : Boolean = true,
    private var tempPrompt : String = "",
) : ViewModel() {

    var isWFH : Boolean = false
    // `GemmaUiState()` is optimized for the Gemma model.
    // Replace `GemmaUiState` with `ChatUiState()` if you're using a different model
    private val _uiState: MutableStateFlow<GemmaUiState> = MutableStateFlow(GemmaUiState())
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _textInputEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val isTextInputEnabled: StateFlow<Boolean> =
        _textInputEnabled.asStateFlow()


    private val _showWFH: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    val showWFH: StateFlow<Boolean> =
        _showWFH.asStateFlow()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.addMessage(userMessage, USER_PREFIX)
            var currentMessageId: String? = _uiState.value.createLoadingMessage()
            setInputEnabled(false)
            try {
                isWFH = inferenceModel.isWFH(userMessage)
                if(isWFH){
                    Log.i("ChatViewModel", "*** WFH: $userMessage")
                    tempPrompt = Prompts.EXTRACTWFHEntityPrompt(userMessage)
                    inferenceModel.generateResponse(tempPrompt)
                    showWFH(isWFH)
//                    inferenceModel.generateResponseAsync(tempPrompt, true)
                }

                else{
                    Log.i("ChatViewModel", "***NOT WFH: $userMessage")
                    inferenceModel.generateResponseAsync(userMessage, true)

                }




                inferenceModel.partialResults
                    .collectIndexed { index, (partialResult, done) ->
                        println("Partial result: $partialResult")



                        currentMessageId?.let {
                            if (index == 0) {
                                _uiState.value.appendFirstMessage(it, partialResult)
                            } else {
                                if(toAppend){
                                    _uiState.value.appendMessage(it, partialResult, done)
                                }
                            }
                            if (done) {
                                currentMessageId = null
                                // Re-enable text input
                                setInputEnabled(true)
                            }
                            if(partialResult.contains("}")){
                                toAppend = false
                            }

                        }

                    }




            } catch (e: Exception) {
                _uiState.value.addMessage(e.localizedMessage ?: "Unknown Error", MODEL_PREFIX)
                setInputEnabled(true)
            }
        }

        toAppend = true
    }

    private fun setInputEnabled(isEnabled: Boolean) {
        _textInputEnabled.value = isEnabled
    }

    private fun showWFH(isWFH: Boolean) {
        //hardcoded
        _showWFH.value = isWFH
    }

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val inferenceModel = InferenceModel.getInstance(context)
                return ChatViewModel(inferenceModel) as T
            }
        }
    }
}
