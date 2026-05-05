package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoResumo
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeFundo = Color(0xFFF4F3EF)
private val HomeVerde = Color(0xFF159E73)
private val HomeCinza = Color(0xFFE4E2DE)
private val HomeTextoCinza = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGestao: () -> Unit,
    onNavigateToTarefas: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToRelatorios: () -> Unit,
    projetosViewModel: ProjetosViewModel = viewModel(),
    tarefaViewModel: TarefaViewModel = viewModel()
) {
    val projetos by projetosViewModel.projetosResumo.collectAsState()
    val tarefas by tarefaViewModel.tarefas.collectAsState()
    var menuAberto by remember { mutableStateOf(false) }

    val hoje = System.currentTimeMillis()
    val tarefasOrdenadas = tarefaViewModel.ordenar(tarefas)
    val tarefasPendentes = tarefaViewModel.listarPendentes(tarefas)
    val tarefasAtrasadas = tarefaViewModel.listarAtrasadas(tarefasPendentes, hoje)
    val tarefasDoDia = tarefasOrdenadas
        .let { tarefaViewModel.listarPendentes(it) }
        .take(2)
    val projetosOrdenados = projetosViewModel.ordenarPorPrioridade(projetos)
    val projetoContinuar = projetosOrdenados.firstOrNull()
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale("pt", "BR")) }

    Scaffold(
        containerColor = HomeFundo,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HomeFundo),
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuAberto = true }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = menuAberto,
                            onDismissRequest = { menuAberto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Projetos") },
                                onClick = {
                                    menuAberto = false
                                    onNavigateToProjetos()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Tarefas") },
                                onClick = {
                                    menuAberto = false
                                    onNavigateToTarefas()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Membros") },
                                onClick = {
                                    menuAberto = false
                                    onNavigateToGestao()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Relatórios") },
                                onClick = {
                                    menuAberto = false
                                    onNavigateToRelatorios()
                                }
                            )
                        }
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bem-vindo, usuario",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = Color.Gray,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                DashboardDivider()
                SectionLabel(
                    text = projetoContinuar?.let { "CONTINUAR ${it.nome.uppercase()}:" } ?: "CONTINUAR PROJETO:"
                )
                ContinueProjectCard(
                    projeto = projetoContinuar,
                    onClick = onNavigateToProjetos
                )
                DashboardDivider(modifier = Modifier.padding(top = 14.dp))
            }

            item {
                DelayCard(
                    quantidade = tarefasAtrasadas.size,
                    onClick = onNavigateToTarefas
                )
            }

            item {
                SectionLabel(text = "TAREFAS DO DIA")
            }

            if (tarefasDoDia.isEmpty()) {
                item {
                    EmptyHomeCard(
                        text = "Nenhuma tarefa pendente para acompanhar.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(tarefasDoDia) { tarefa ->
                    val projetoNome = projetos.firstOrNull { it.idProjeto == tarefa.idProjeto }?.nome
                        ?: "Projeto ${tarefa.idProjeto}"
                    HomeTaskCard(
                        tarefa = tarefa,
                        projetoNome = projetoNome,
                        prazoTexto = dateFormat.format(Date(tarefa.prazo)),
                        onClick = onNavigateToTarefas
                    )
                }
            }

            item {
                DashboardDivider(modifier = Modifier.padding(top = 14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToProjetos)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MEUS PROJETOS:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextoCinza
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = HomeTextoCinza,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (projetosOrdenados.isEmpty()) {
                item {
                    EmptyHomeCard(
                        text = "Nenhum projeto cadastrado ainda.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(projetosOrdenados.take(3)) { projeto ->
                    HomeProjectCard(
                        projeto = projeto,
                        onClick = onNavigateToProjetos
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = HomeTextoCinza,
        modifier = Modifier.padding(start = 28.dp, top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun DashboardDivider(modifier: Modifier = Modifier) {
    Divider(
        color = Color(0xFFD2D0CA),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ContinueProjectCard(
    projeto: ProjetoResumo?,
    onClick: () -> Unit
) {
    val tarefasRestantes = projeto?.tarefasAtivas ?: 0
    val progresso = projeto?.progresso() ?: 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$tarefasRestantes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TAREFAS A CONCLUIR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextoCinza
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = HomeTextoCinza,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = progresso,
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = HomeVerde,
                    trackColor = HomeCinza
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${(progresso * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextoCinza
                )
            }
        }
    }
}

@Composable
private fun DelayCard(
    quantidade: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$quantidade",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD75A5A)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TAREFAS ATRASADAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextoCinza
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = HomeTextoCinza,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                HomePill(text = "Alta", textColor = Color(0xFFD75A5A), background = Color(0xFFFFE5E8))
                Spacer(modifier = Modifier.height(6.dp))
                HomePill(text = "Atrasada", textColor = Color(0xFFD75A5A), background = Color(0xFFFFDDE2))
            }
        }
    }
}

@Composable
private fun HomeTaskCard(
    tarefa: Tarefa,
    projetoNome: String,
    prazoTexto: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = projetoNome,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tarefa.titulo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Prazo: $prazoTexto",
                    fontSize = 11.sp,
                    color = HomeTextoCinza
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                HomePill(
                    text = tarefa.prioridade,
                    textColor = prioridadeColor(tarefa.prioridade),
                    background = prioridadeColor(tarefa.prioridade).copy(alpha = 0.12f)
                )
                Spacer(modifier = Modifier.height(7.dp))
                HomePill(
                    text = statusTexto(tarefa.status),
                    textColor = Color(0xFF1565C0),
                    background = Color(0xFFE3F2FD)
                )
            }
        }
    }
}

@Composable
private fun HomeProjectCard(
    projeto: ProjetoResumo,
    onClick: () -> Unit
) {
    val progresso = projeto.progresso()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFBDBDBD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(
                text = projeto.nome,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${projeto.tarefasAtivas}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "/${projeto.limiteTarefas}",
                    fontSize = 20.sp,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = progresso,
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = HomeVerde,
                    trackColor = HomeCinza
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${(progresso * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextoCinza
                )
            }
        }
    }
}

@Composable
private fun HomePill(text: String, textColor: Color, background: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptyHomeCard(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = text,
            color = HomeTextoCinza,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun ProjetoResumo.progresso(): Float {
    return if (totalTarefas == 0) 0f else {
        (tarefasConcluidas.toFloat() / totalTarefas).coerceIn(0f, 1f)
    }
}

private fun prioridadeColor(prioridade: String): Color {
    return when (prioridade) {
        "Alta" -> Color(0xFFD75A5A)
        "Media" -> Color(0xFFF57C00)
        else -> Color(0xFF159E73)
    }
}

private fun statusTexto(status: String): String {
    return when (status) {
        "Em_Andamento" -> "Em andamento"
        "Concluida" -> "Concluída"
        else -> "Não iniciada"
    }
}
