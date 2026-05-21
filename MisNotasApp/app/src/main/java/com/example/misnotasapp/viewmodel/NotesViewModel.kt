package com.example.misnotasapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.misnotasapp.model.Note
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel : ViewModel() {

    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> get() = _notes

    // Estado que controla si la app está cargando datos
    var isLoading by mutableStateOf(true)
        private set

    // Estado para capturar el texto del buscador
    var searchQuery by mutableStateOf("")
        private set

    init {
        simulateLoadingFromDatabase()
    }

    private fun simulateLoadingFromDatabase() {
        viewModelScope.launch {
            isLoading = true
            delay(1500) // Simula la espera de red o lectura de disco
            _notes.addAll(
                listOf(
                    Note(1L, "📌 Entrega del proyecto", "Pasarle el código limpio al diseñador.", "20/05/2026", isPinned = true),
                    Note(2L, "Ideas para el diseño", "Probar esquemas de colores pastel y bordes redondeados.", "19/05/2026"),
                    Note(3L, "Recordatorio", "Subir los cambios a GitHub con la nomenclatura correcta.", "18/05/2026"),
                )
            )
            isLoading = false
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    // Filtra y ordena: Las notas marcadas como fijadas (isPinned = true) suben automáticamente
    val filteredNotes: List<Note>
        get() {
            val baseList = if (searchQuery.isBlank()) {
                _notes
            } else {
                _notes.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.content.contains(searchQuery, ignoreCase = true)
                }
            }
            return baseList.sortedByDescending { it.isPinned }
        }

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        val newNote = Note(
            id = System.currentTimeMillis(),
            title = title.trim().ifBlank { "Sin título" },
            content = content.trim(),
            date = dateFormatter.format(Date())
        )
        _notes.add(0, newNote)
    }

    fun updateNote(id: Long, title: String, content: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index == -1) return
        _notes[index] = _notes[index].copy(
            title = title.trim().ifBlank { "Sin título" },
            content = content.trim(),
            date = dateFormatter.format(Date())
        )
    }

    // Cambia el estado de fijado de una nota
    fun togglePinNote(id: Long) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index == -1) return
        _notes[index] = _notes[index].copy(isPinned = !_notes[index].isPinned)
    }

    fun deleteNote(id: Long) {
        _notes.removeAll { it.id == id }
    }

    fun getNoteById(id: Long): Note? = _notes.firstOrNull { it.id == id }
}