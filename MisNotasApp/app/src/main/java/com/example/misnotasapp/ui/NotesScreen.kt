package com.example.misnotasapp.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.misnotasapp.model.Note
import com.example.misnotasapp.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel = viewModel()) {
    val notes = viewModel.notes

    // Estado del diálogo: null = cerrado, Note = editando, Note(id=0) = creando nuevo.
    var noteBeingEdited by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Disparamos el diálogo en modo "crear" con una nota vacía.
                noteBeingEdited = Note(id = 0L, title = "", content = "", date = "")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir nota")
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            // Estado vacío: mensaje centrado para que el usuario sepa qué hacer.
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay notas todavía.\nToca + para crear una.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(
                        note = note,
                        onEdit = { noteBeingEdited = note },
                        onDelete = { noteToDelete = note }
                    )
                }
            }
        }
    }

    // Diálogo de crear/editar.
    noteBeingEdited?.let { note ->
        NoteEditDialog(
            note = note,
            onDismiss = { noteBeingEdited = null },
            onSave = { title, content ->
                if (note.id == 0L) {
                    viewModel.addNote(title, content)
                } else {
                    viewModel.updateNote(note.id, title, content)
                }
                noteBeingEdited = null
            }
        )
    }

    // Diálogo de confirmación de borrado.
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Seguro que querés eliminar \"${note.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(note.id)
                    noteToDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Tap simple = editar; long-press también dispara editar (consistente con el ícono).
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onEdit
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                // Botones de acción: editar y eliminar.
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    maxLines = 2,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.date,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun NoteEditDialog(
    note: Note,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String) -> Unit
) {
    // Estado local del formulario, inicializado con los valores actuales.
    var title by remember(note.id) { mutableStateOf(note.title) }
    var content by remember(note.id) { mutableStateOf(note.content) }

    val isCreating = note.id == 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isCreating) "Nueva nota" else "Editar nota") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, content) },
                // Evitamos crear notas totalmente vacías.
                enabled = title.isNotBlank() || content.isNotBlank()
            ) {
                Text(if (isCreating) "Crear" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
