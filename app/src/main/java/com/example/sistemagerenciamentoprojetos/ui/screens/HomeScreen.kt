package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGestao: () -> Unit,
    onNavigateToTarefas: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gerenciamento de Projetos") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo ao Sistema!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão já existente
            Button(
                onClick = onNavigateToGestao,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58)
                )
            ) {
                Text("Gerenciar Membros", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão novo
            Button(
                onClick = onNavigateToTarefas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                )
            ) {
                Text("Gerenciar Tarefas", color = Color.White)
            }
        }
    }
}