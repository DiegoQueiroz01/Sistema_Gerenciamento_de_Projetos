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
import com.example.sistemagerenciamentoprojetos.ui.screens.CadastroProjetoScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.CadastroTarefaScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.DetalhesMembroScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.DetalhesProjetoScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.EditarTarefaScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.GestaoMembrosScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.GestaoProjetosScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.GestaoTarefasScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.HomeScreen
import com.example.sistemagerenciamentoprojetos.ui.screens.RelatoriosScreen
import com.example.sistemagerenciamentoprojetos.ui.theme.SistemaGerenciamentoProjetosTheme
import com.example.sistemagerenciamentoprojetos.ui.screens.TaskDetailScreen

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
            HomeScreen(
                onNavigateToGestao = { navController.navigate("gestao_membros") },
                onNavigateToTarefas = { navController.navigate("gestao_tarefas") },
                onNavigateToProjetos = { navController.navigate("gestao_projetos") },
                onNavigateToRelatorios = { navController.navigate("relatorios") }
            )
        }

        composable("gestao_projetos") {
            GestaoProjetosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCadastro = { navController.navigate("cadastro_projeto") },
                onNavigateToDetalhes = { id ->
                    navController.navigate("detalhes_projeto/$id")
                }
            )
        }
        composable("cadastro_projeto") {
            CadastroProjetoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "detalhes_projeto/{projetoId}",
            arguments = listOf(navArgument("projetoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val projetoId = backStackEntry.arguments?.getInt("projetoId") ?: 0
            DetalhesProjetoScreen(
                projetoId = projetoId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditar = { id -> navController.navigate("editar_projeto/$id") }
            )
        }
        composable(
            route = "editar_projeto/{projetoId}",
            arguments = listOf(navArgument("projetoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val projetoId = backStackEntry.arguments?.getInt("projetoId") ?: 0
            CadastroProjetoScreen(
                projetoId = projetoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("gestao_membros") {
            GestaoMembrosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCadastro = { navController.navigate("cadastro_membro") },
                onNavigateToDetalhes = { id ->
                    navController.navigate("detalhes_membro/$id")
                }
            )
        }
        composable("cadastro_membro") {
            CadastroMembroScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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


        composable("gestao_tarefas") {
            GestaoTarefasScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCadastro = { navController.navigate("cadastro_tarefa") },
                onNavigateToDetalhe = { id ->
                    navController.navigate("detalhe_tarefa/$id")
                }
            )
        }
        composable("cadastro_tarefa") {
            CadastroTarefaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "detalhe_tarefa/{tarefaId}",
            arguments = listOf(navArgument("tarefaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getInt("tarefaId") ?: 0
            TaskDetailScreen(
                tarefaId = tarefaId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditar = { id -> navController.navigate("editar_tarefa/$id") }
            )
        }
        composable(
            route = "editar_tarefa/{tarefaId}",
            arguments = listOf(navArgument("tarefaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getInt("tarefaId") ?: 0
            EditarTarefaScreen(
                tarefaId = tarefaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("relatorios") {
            RelatoriosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
