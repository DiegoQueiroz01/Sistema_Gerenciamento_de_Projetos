package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestaoTarefasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    onNavigateToDetalhe: (Int) -> Unit,   // <-- adicionar isso
    viewModel: TarefaViewModel = viewModel()
) {
    val tarefas by viewModel.tarefas.collectAsState()
    val tarefasOrdenadas = viewModel.ordenar(tarefas)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TAREFAS", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCadastro,
                containerColor = Color(0xFF0F9D58),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Text(
                    " FILTRAR POR: PROJETO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("↑ ORDENAR POR: PRAZO", fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            if (tarefasOrdenadas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma tarefa cadastrada.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tarefasOrdenadas) { tarefa ->
                        TarefaCard(
                            tarefa = tarefa,
                            onClick = {onNavigateToDetalhe(tarefa.idTarefa)},
                            onDeletar = {
                                scope.launch {
                                    viewModel.deletar(tarefa.idTarefa)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarefaCard(tarefa: Tarefa, onClick: () -> Unit, onDeletar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarefa.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Prazo: ${java.text.SimpleDateFormat("dd/MM/yyyy")
                        .format(java.util.Date(tarefa.prazo))}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (tarefa.status == "Concluida") 1f else 0.5f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF0F9D58),
                    trackColor = Color(0xFFEEEEEE)
                )
                // Botão remover
                TextButton(onClick = onDeletar) {
                    Text("Remover", color = Color.Red, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                BadgePrioridade(tarefa.prioridade)
                Spacer(modifier = Modifier.height(4.dp))
                BadgeStatus(tarefa.status)
            }
        }
    }
}

@Composable
fun BadgePrioridade(prioridade: String) {
    val cor = when (prioridade) {
        "Alta"  -> Color(0xFFD32F2F)
        "Media" -> Color(0xFFF57C00)
        else    -> Color(0xFF388E3C)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cor.copy(alpha = 0.1f)
    ) {
        Text(
            text = prioridade,
            color = cor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BadgeStatus(status: String) {
    val (cor, texto) = when (status) {
        "Em_Andamento" -> Pair(Color(0xFF1565C0), "Em andamento")
        "Concluida"    -> Pair(Color(0xFF2E7D32), "Concluída")
        else           -> Pair(Color.Gray, "Não iniciada")
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cor.copy(alpha = 0.1f)
    ) {
        Text(
            text = texto,
            color = cor,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}