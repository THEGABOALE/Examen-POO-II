package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Note

class NotesViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes

    fun addNote(title: String, content: String) {
        val newNote = Note(
            id = if (_notes.isEmpty()) 1 else _notes.maxOf { it.id } + 1,
            title = title,
            content = content
        )
        _notes.add(newNote)
    }

    fun deleteNote(note: Note) {
        _notes.remove(note)
    }
}
