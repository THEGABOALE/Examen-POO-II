package com.example.misnotasapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.misnotasapp.viewmodel.NotesViewModel

@Composable
fun AddEditNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel,
    noteId: Long
) {
    val noteToEdit = viewModel.notes.find { it.id == noteId }

    var title by remember {
        mutableStateOf(noteToEdit?.title ?: "")
    }

    var content by remember {
        mutableStateOf(noteToEdit?.content ?: "")
    }

    val isEditing = noteId != 0L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8ED))
            .padding(16.dp)
    ) {

        Row {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = if (isEditing) "Editar Nota" else "Nueva Nota",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Título") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text("Contenido") },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${content.length} caracteres",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (isEditing) {
                    viewModel.updateNote(
                        id = noteId,
                        title = title,
                        content = content
                    )
                } else {
                    viewModel.addNote(
                        title = title,
                        content = content
                    )
                }

                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF34A853),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = if (isEditing) "Actualizar Nota" else "Guardar Nota",
                fontWeight = FontWeight.Bold
            )
        }
    }
}