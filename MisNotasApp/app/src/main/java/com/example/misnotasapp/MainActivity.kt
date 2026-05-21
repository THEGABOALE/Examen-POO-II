package com.example.misnotasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.misnotasapp.ui.NotesListScreen
import com.example.misnotasapp.ui.AddEditNoteScreen
import com.example.misnotasapp.ui.theme.MisNotasAppTheme
import com.example.misnotasapp.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisNotasAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Instanciamos el ViewModel aquí para compartirlo entre pantallas
    val viewModel: NotesViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        // Pantalla Principal (Lista)
        composable("home") {
            NotesListScreen(navController = navController, viewModel = viewModel)
        }
        // Pantalla de Creación/Edición
        composable(
            route = "addEdit?noteId={noteId}",
            arguments = listOf(navArgument("noteId") {
                type = NavType.LongType
                defaultValue = 0L
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            AddEditNoteScreen(
                navController = navController,
                viewModel = viewModel,
                noteId = noteId
            )
        }
    }
}