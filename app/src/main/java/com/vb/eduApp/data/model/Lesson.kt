package com.vb.eduApp.data.model

data class Lesson(
    val id: String,
    val title: String,
    val creatorId: String,
    val creatorName: String,
    val lesson: List<String>,
    val questions: List<Question>
)
