package com.example.misnotasapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.misnotasapp.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel que mantiene la lista de notas en memoria y expone
 * las operaciones del CRUD (Create, Read, Update, Delete).
 *
 * Los datos se pierden al cerrar la app. Para persistencia,
 * migrar a Room (SQLite) o DataStore.
 */
class NotesViewModel : ViewModel() {

    // mutableStateListOf permite que Compose reaccione automáticamente
    // a cambios (add/remove/set) sin necesidad de StateFlow.
    private val _notes = mutableStateListOf(
        Note(1L, "Lista de compras", "Leche, pan, huevos, café", "20/05/2026"),
        Note(2L, "Ideas para el blog", "Revisión de Android Studio, Tips de Compose", "19/05/2026"),
        Note(3L, "Recordatorio", "Llamar al médico a las 3 PM", "18/05/2026"),
    )
    val notes: List<Note> get() = _notes

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // CREATE: agrega una nueva nota con id único basado en el tiempo actual.
    fun addNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        val newNote = Note(
            id = System.currentTimeMillis(),
            title = title.trim().ifBlank { "Sin título" },
            content = content.trim(),
            date = dateFormatter.format(Date())
        )
        _notes.add(0, newNote) // insertamos al inicio para que aparezca arriba
    }

    // UPDATE: reemplaza la nota con el mismo id.
    fun updateNote(id: Long, title: String, content: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index == -1) return
        _notes[index] = _notes[index].copy(
            title = title.trim().ifBlank { "Sin título" },
            content = content.trim(),
            date = dateFormatter.format(Date()) // actualizamos la fecha al editar
        )
    }

    // DELETE: elimina la nota por id.
    fun deleteNote(id: Long) {
        _notes.removeAll { it.id == id }
    }

    // READ auxiliar: obtener una nota específica (útil para la pantalla de edición).
    fun getNoteById(id: Long): Note? = _notes.firstOrNull { it.id == id }
}
