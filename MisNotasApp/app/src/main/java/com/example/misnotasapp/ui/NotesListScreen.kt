package com.example.misnotasapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.misnotasapp.model.Note
import com.example.misnotasapp.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

// Colores pastel inspirados en la imagen de notas
private val FondoPastel = Color(0xFFFFF8ED)
private val RosaNota = Color(0xFFF8BBD0)
private val AmarilloNota = Color(0xFFFFECB3)
private val MoradoTexto = Color(0xFF5D4B73)
private val RosaFuerte = Color(0xFFE96C9A)
private val AmarilloFuerte = Color(0xFFFFC857)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes = viewModel.notes
    val filteredNotes = viewModel.filteredNotes
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = FondoPastel,

        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Notas",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Organiza tus ideas de forma simple",
                            fontSize = 13.sp,
                            color = MoradoTexto.copy(alpha = 0.75f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoPastel,
                    titleContentColor = MoradoTexto
                )
            )
        },

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        floatingActionButton = {
            if (!viewModel.isLoading) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addEdit?noteId=0")
                    },
                    containerColor = RosaFuerte,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir nota")
                }
            }
        }
    ) { innerPadding ->

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = RosaFuerte)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(FondoPastel)
            ) {

                if (notes.isNotEmpty()) {
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = { Text("Buscar notas...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosaFuerte,
                            unfocusedBorderColor = AmarilloFuerte,
                            focusedLabelColor = RosaFuerte,
                            cursorColor = RosaFuerte
                        )
                    )
                }

                if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay notas todavía.\nToca + para crear una.",
                            fontSize = 17.sp,
                            color = MoradoTexto
                        )
                    }
                } else if (filteredNotes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron notas.",
                            fontSize = 17.sp,
                            color = MoradoTexto
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredNotes, key = { it.id }) { note ->
                            NoteItem(
                                note = note,
                                onEdit = {
                                    navController.navigate("addEdit?noteId=${note.id}")
                                },
                                onDelete = {
                                    noteToDelete = note
                                },
                                onTogglePin = {
                                    viewModel.togglePinNote(note.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = {
                Text("Eliminar nota", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("¿Seguro que querés eliminar \"${note.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(note.id)
                        noteToDelete = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Nota eliminada correctamente")
                        }
                    }
                ) {
                    Text("Eliminar", color = RosaFuerte)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
    val colorNota = if (note.isPinned) AmarilloNota else RosaNota

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onEdit
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorNota
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoradoTexto,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onTogglePin) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Fijar nota",
                        tint = if (note.isPinned) AmarilloFuerte else MoradoTexto.copy(alpha = 0.35f)
                    )
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MoradoTexto
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = RosaFuerte
                    )
                }
            }

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note.content,
                    maxLines = 3,
                    fontSize = 15.sp,
                    color = MoradoTexto.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note.date,
                fontSize = 12.sp,
                color = MoradoTexto.copy(alpha = 0.65f)
            )
        }
    }
}