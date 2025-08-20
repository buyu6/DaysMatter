package com.example.daysmatter.ai.network

import android.util.Log
import com.example.daysmatter.ai.model.GeminiRequestBody
import com.example.daysmatter.ai.model.GeminiResponseBody

object AInetwork {
    private val aiService = AICreator.create<AIService>()
    suspend fun generateContent(requestBody: GeminiRequestBody): Result<GeminiResponseBody> {
        return try {
            val response = aiService.generateContent(requestBody=requestBody)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "未知错误"
                Log.e("AInetwork", "数据获取失败: $errorMessage")
                Result.failure(RuntimeException("API请求失败: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("AInetwork", "网络请求失败: ${e.message}", e)
            Result.failure(e)
        }
    }
}