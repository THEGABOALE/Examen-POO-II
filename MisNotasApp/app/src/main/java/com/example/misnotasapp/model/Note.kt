package com.example.misnotasapp.model

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val date: String,
    val isPinned: Boolean = false
)
