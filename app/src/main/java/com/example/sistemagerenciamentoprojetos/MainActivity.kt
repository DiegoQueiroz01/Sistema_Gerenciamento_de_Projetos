package com.example.sistemagerenciamentoprojetos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sistemagerenciamentoprojetos.ui.screens.CadastroMembroScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.DetalhesMembroScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.GestaoMembrosScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.HomeScreen
import com.example.sistemagerenciamentoprojetos.ui.theme.SistemaGerenciamentoProjetosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SistemaGerenciamentoProjetosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToGestao = {
                navController.navigate("gestao_membros")
            })
        }
        composable("gestao_membros") {
            GestaoMembrosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCadastro = { navController.navigate("cadastro_membro") },
                onNavigateToDetalhes = { id -> navController.navigate("detalhes_membro/$id") }
            )
        }
        composable("cadastro_membro") {
            CadastroMembroScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
        composable(
            route = "detalhes_membro/{membroId}",
            arguments = listOf(navArgument("membroId") { type = NavType.IntType })
        ) { backStackEntry ->
            val membroId = backStackEntry.arguments?.getInt("membroId") ?: 0
            DetalhesMembroScreen(
                membroId = membroId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
