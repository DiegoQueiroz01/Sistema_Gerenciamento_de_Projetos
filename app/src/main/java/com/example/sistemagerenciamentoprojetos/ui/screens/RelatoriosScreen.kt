package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatoriosScreen(
    onNavigateBack: () -> Unit,
    tarefaViewModel: TarefaViewModel = viewModel(),
    membrosViewModel: MembrosViewModel = viewModel(),
    projetosViewModel: ProjetosViewModel = viewModel()
) {
    val tarefas by tarefaViewModel.tarefas.collectAsState()
    val membros by membrosViewModel.membros.collectAsState()
    val projetos by projetosViewModel.projetos.collectAsState()
    var projetoFiltroId by remember { mutableStateOf(0) }
    var expandirProjeto by remember { mutableStateOf(false) }

    val tarefasFiltradas = tarefas.filtrarComListaDinamica { projetoFiltroId == 0 || it.idProjeto == projetoFiltroId }
    val agora = System.currentTimeMillis()
    val pendentes = tarefasFiltradas.contarComListaDinamica { it.status == "Nao_Iniciada" }
    val andamento = tarefasFiltradas.contarComListaDinamica { it.status == "Em_Andamento" }
    val concluidas = tarefasFiltradas.contarComListaDinamica { it.status == "Concluida" }
    val atrasadas = tarefasFiltradas.contarComListaDinamica { it.status != "Concluida" && it.prazo < agora }
    val total = tarefasFiltradas.size
    val taxaConclusao = if (total == 0) 0f else concluidas.toFloat() / total
    val tarefasNoPrazo = tarefasFiltradas.contarComListaDinamica { it.status == "Concluida" && it.prazo >= agora }
    val taxaNoPrazo = if (concluidas == 0) 0f else tarefasNoPrazo.toFloat() / concluidas
    val tempoEstimado = tarefasFiltradas.sumOf { it.tempoEstimado.toDouble() }.toFloat()
    val tempoEfetivo = tarefasFiltradas.sumOf { it.tempoEfetivo.toDouble() }.toFloat()
    val eficienciaTempo = if (tempoEstimado == 0f || tempoEfetivo == 0f) 0f else (tempoEstimado / tempoEfetivo).coerceAtMost(1.5f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF0F9D58))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Relatórios")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expandirProjeto,
                    onExpandedChange = { expandirProjeto = !expandirProjeto }
                ) {
                    TextField(
                        value = if (projetoFiltroId == 0) "Todos os projetos" else projetos.firstOrNull { it.idProjeto == projetoFiltroId }?.nome.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Projeto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirProjeto) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF0F9D58),
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(expanded = expandirProjeto, onDismissRequest = { expandirProjeto = false }) {
                        DropdownMenuItem(
                            text = { Text("Todos os projetos") },
                            onClick = {
                                projetoFiltroId = 0
                                expandirProjeto = false
                            }
                        )
                        projetos.forEach { projeto ->
                            DropdownMenuItem(
                                text = { Text(projeto.nome) },
                                onClick = {
                                    projetoFiltroId = projeto.idProjeto
                                    expandirProjeto = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("Pendentes", "$pendentes", Color.Gray, Modifier.weight(1f))
                    MetricCard("Em andamento", "$andamento", Color(0xFF1565C0), Modifier.weight(1f))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("Concluídas", "$concluidas", Color(0xFF2E7D32), Modifier.weight(1f))
                    MetricCard("Atrasadas", "$atrasadas", Color(0xFFD32F2F), Modifier.weight(1f))
                }
            }

            item {
                ReportProgressCard("Taxa de conclusão", taxaConclusao, "${(taxaConclusao * 100).toInt()}%")
            }

            item {
                ReportProgressCard("Tarefas concluídas no prazo", taxaNoPrazo, "${(taxaNoPrazo * 100).toInt()}%")
            }

            item {
                ReportProgressCard(
                    "Eficiência de tempo",
                    eficienciaTempo.coerceIn(0f, 1f),
                    "Estimado ${tempoEstimado.toInt()}h / Efetivo ${tempoEfetivo.toInt()}h"
                )
            }

            item {
                Text(
                    "Progresso por membro",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(membros) { membro ->
                val tarefasMembro = tarefasFiltradas.filtrarComListaDinamica { it.idMembro == membro.idMembro }
                val concluidasMembro = tarefasMembro.contarComListaDinamica { it.status == "Concluida" }
                val progresso = if (tarefasMembro.isEmpty()) 0f else concluidasMembro.toFloat() / tarefasMembro.size
                MembroReportCard(
                    nome = membro.nome,
                    cargo = membro.cargo,
                    progresso = progresso,
                    resumo = "$concluidasMembro/${tarefasMembro.size} tarefas concluídas"
                )
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun ReportProgressCard(label: String, progress: Float, detail: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(detail, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF0F9D58),
                trackColor = Color(0xFFEEEEEE)
            )
        }
    }
}

@Composable
private fun MembroReportCard(nome: String, cargo: String, progresso: Float, resumo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(nome, fontWeight = FontWeight.Bold)
                    Text(cargo, fontSize = 12.sp, color = Color.Gray)
                }
                Text(resumo, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = progresso.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF1565C0),
                trackColor = Color(0xFFEEEEEE)
            )
        }
    }
}
