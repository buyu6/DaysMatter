package com.example.daysmatter.ai


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daysmatter.ai.model.Content
import com.example.daysmatter.ai.model.GeminiRequestBody
import com.example.daysmatter.ai.model.Part
import com.example.daysmatter.ai.network.AInetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AIViewModel : ViewModel() {

    // 消息列表
    private val _messages = MutableLiveData<List<Msg>>()
    val messages: LiveData<List<Msg>> = _messages

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 错误状态
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // 内部消息列表
    private val messageList = mutableListOf<Msg>()

    init {
        _messages.value = messageList
        _isLoading.value = false
        _error.value = null
    }

    /**
     * 发送消息
     */
    fun sendMessage(content: String) {
        if (content.isBlank()) return

        // 添加用户消息
        addMessage(Msg(content, Msg.TYPE_SENT))

        // 请求AI回复
        requestAIResponse(content)
    }

    /**
     * 请求AI回复
     */
    private fun requestAIResponse(question: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // 添加加载提示
                addMessage(Msg("正在思考中...", Msg.TYPE_RESERVED))

                // 在IO线程中执行网络请求
                val result = withContext(Dispatchers.IO) {
                    val requestBody = GeminiRequestBody(
                        contents = listOf(Content(parts = listOf(Part(text = question))))
                    )
                    AInetwork.generateContent(requestBody)
                }

                // 移除加载提示
                removeLastMessage()

                // 处理结果
                result.onSuccess { response ->
                    val aiResponse = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (aiResponse.isNullOrEmpty()) {
                        addMessage(Msg("抱歉，我暂时无法回答您的问题。", Msg.TYPE_RESERVED))
                    } else {
                        addMessage(Msg(aiResponse, Msg.TYPE_RESERVED))
                    }
                }.onFailure { exception ->
                    val errorMsg = "网络请求失败: ${exception.message}"
                    addMessage(Msg(errorMsg, Msg.TYPE_RESERVED))
                    _error.value = errorMsg
                }

            } catch (e: Exception) {
                removeLastMessage()
                val errorMsg = "发生未知错误，请稍后重试。"
                addMessage(Msg(errorMsg, Msg.TYPE_RESERVED))
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 添加消息到列表
     */
    private fun addMessage(message: Msg) {
        messageList.add(message)
        _messages.value = messageList.toList()
    }

    /**
     * 移除最后一条消息
     */
    private fun removeLastMessage() {
        if (messageList.isNotEmpty()) {
            messageList.removeAt(messageList.size - 1)
            _messages.value = messageList.toList()
        }
    }
}