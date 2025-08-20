package com.example.daysmatter.ai.network

import com.example.daysmatter.MyApplication
import com.example.daysmatter.ai.model.GeminiRequestBody
import com.example.daysmatter.ai.model.GeminiResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AIService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
     suspend fun generateContent(
        @Query("key") apiKey: String = MyApplication.GEMINI_API_KEY,
        @Body requestBody: GeminiRequestBody
    ) :Response<GeminiResponseBody>
}