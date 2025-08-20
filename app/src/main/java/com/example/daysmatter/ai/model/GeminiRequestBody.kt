package com.example.daysmatter.ai.model




data class GeminiRequestBody(val contents:List<Content>)
data class GeminiResponseBody(val candidates: List<Candidate>)
data class Candidate(val content: Content)
data class Content(val parts:List<Part>)
data class Part(val text:String)