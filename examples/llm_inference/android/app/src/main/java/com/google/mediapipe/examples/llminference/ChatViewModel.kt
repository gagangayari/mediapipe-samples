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
    private var isRAIvalid : Boolean = false
) : ViewModel() {

    // `GemmaUiState()` is optimized for the Gemma model.
    // Replace `GemmaUiState` with `ChatUiState()` if you're using a different model
    private val _uiState: MutableStateFlow<GemmaUiState> = MutableStateFlow(GemmaUiState())
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _textInputEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val isTextInputEnabled: StateFlow<Boolean> =
        _textInputEnabled.asStateFlow()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.addMessage(userMessage, USER_PREFIX)
            var currentMessageId: String? = _uiState.value.createLoadingMessage()
            setInputEnabled(false)
            try {
                isRAIvalid = inferenceModel.isRAIValid(userMessage)
                if(isRAIvalid){
                    val fullPrompt = _uiState.value.fullPrompt
                    tempPrompt = userMessage
                }

                else{
                    Log.i("ChatViewModel", "INVALID RAI: $userMessage")
                    tempPrompt = "Respond to the user saying that the input needs to be more ethical, appropriate and non-malicious.\n" +
                            "Response:"
                }


                inferenceModel.generateResponseAsync(tempPrompt, isRAIvalid)
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

    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val inferenceModel = InferenceModel.getInstance(context)
                return ChatViewModel(inferenceModel) as T
            }
        }
    }
}
