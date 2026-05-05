package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ProjetoVerde = Color(0xFF1B7A4A)
private val ProjetoFundo = Color(0xFFF2F2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesProjetoScreen(
    projetoId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEditar: (Int) -> Unit,
    projetosViewModel: ProjetosViewModel = viewModel(),
    tarefaViewModel: TarefaViewModel = viewModel()
) {
    var projeto by remember { mutableStateOf<Projeto?>(null) }
    var abaSelecionada by remember { mutableStateOf(0) }
    var mensagem by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val tarefasFlow = remember(projetoId) { tarefaViewModel.tarefasDoProjeto(projetoId) }
    val membrosFlow = remember(projetoId) { projetosViewModel.membrosDoProjeto(projetoId) }
    val membrosDisponiveisFlow = remember(projetoId) {
        if (projetoId == 0) flowOf(emptyList<Membro>()) else projetosViewModel.membrosDisponiveisDoProjeto(projetoId)
    }
    val tarefas by tarefasFlow.collectAsState(initial = emptyList())
    val membros by membrosFlow.collectAsState(initial = emptyList())
    val membrosDisponiveis by membrosDisponiveisFlow.collectAsState(initial = emptyList())
    val tarefasOrdenadas = tarefaViewModel.ordenar(tarefas)
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(projetoId) {
        projeto = projetosViewModel.getProjetoById(projetoId)
    }

    Scaffold(
        containerColor = ProjetoFundo,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "Detalhes do Projeto",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    TextButton(onClick = { onNavigateToEditar(projetoId) }) {
                        Text("Editar", color = ProjetoVerde, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }
    ) { paddingValues ->
        projeto?.let { p ->
            val ativas = tarefaViewModel.contarAtivas(tarefas)
            val concluidas = tarefaViewModel.contarPorStatus(tarefas, "Concluida")
            val emAndamento = tarefaViewModel.contarPorStatus(tarefas, "Em_Andamento")
            val atrasadas = tarefaViewModel.contarAtrasadas(tarefas, System.currentTimeMillis())
            val progresso = if (tarefas.isEmpty()) 0f else (concluidas.toFloat() / tarefas.size).coerceIn(0f, 1f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = p.nome,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = p.descricao,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = progresso,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ProjetoVerde,
                            trackColor = Color(0xFFEEEEEE)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ProjetoStatCard("ATIVAS", "$ativas/${p.limiteTarefas}", Modifier.weight(1f))
                            ProjetoStatCard("CONCLUÍDAS", "$concluidas", Modifier.weight(1f))
                            ProjetoStatCard("ATRASADAS", "$atrasadas", Modifier.weight(1f))
                        }
                    }
                }

                TabRow(selectedTabIndex = abaSelecionada, containerColor = Color.White, contentColor = ProjetoVerde) {
                    listOf("Tarefas", "Equipe", "Cronograma").forEachIndexed { index, titulo ->
                        Tab(
                            selected = abaSelecionada == index,
                            onClick = { abaSelecionada = index },
                            text = { Text(titulo, fontSize = 13.sp) }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (abaSelecionada) {
                        0 -> ProjetoTarefasTab(tarefasOrdenadas, dateFormat, tarefaViewModel)
                        1 -> ProjetoEquipeTab(
                            membros = membros,
                            membrosDisponiveis = membrosDisponiveis,
                            tarefas = tarefas,
                            tarefaViewModel = tarefaViewModel,
                            mensagem = mensagem,
                            onVincular = { idMembro ->
                                scope.launch {
                                    mensagem = projetosViewModel.vincularMembro(projetoId, idMembro)
                                }
                            },
                            onDesvincular = { idMembro ->
                                scope.launch {
                                    mensagem = projetosViewModel.desvincularMembro(projetoId, idMembro)
                                }
                            }
                        )
                        else -> ProjetoCronogramaTab(
                            tarefas = tarefasOrdenadas,
                            dateFormat = dateFormat,
                            dataInicio = p.dataInicio,
                            dataFim = p.dataFim,
                            emAndamento = emAndamento
                        )
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ProjetoVerde)
        }
    }
}

@Composable
private fun ProjetoTarefasTab(
    tarefas: List<Tarefa>,
    dateFormat: SimpleDateFormat,
    tarefaViewModel: TarefaViewModel
) {
    if (tarefas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhuma tarefa cadastrada para este projeto.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        listOf("Alta", "Media", "Baixa").forEach { prioridade ->
            val tarefasPrioridade = tarefaViewModel.filtrarPorPrioridade(tarefas, prioridade)
            if (tarefasPrioridade.isNotEmpty()) {
                item {
                    Text(
                        text = "PRIORIDADE ${prioridade.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
                items(tarefasPrioridade) { tarefa ->
                    ProjetoTarefaLinha(tarefa, dateFormat)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjetoEquipeTab(
    membros: List<Membro>,
    membrosDisponiveis: List<Membro>,
    tarefas: List<Tarefa>,
    tarefaViewModel: TarefaViewModel,
    mensagem: String,
    onVincular: (Int) -> Unit,
    onDesvincular: (Int) -> Unit
) {
    var expandirMembros by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandirMembros,
                onExpandedChange = { expandirMembros = !expandirMembros }
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Vincular membro ao projeto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMembros) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = ProjetoVerde,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandirMembros,
                    onDismissRequest = { expandirMembros = false }
                ) {
                    if (membrosDisponiveis.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Nenhum membro disponível") },
                            onClick = { expandirMembros = false }
                        )
                    } else {
                        membrosDisponiveis.forEach { membro ->
                            DropdownMenuItem(
                                text = { Text("${membro.nome} — ${membro.cargo}") },
                                onClick = {
                                    expandirMembros = false
                                    onVincular(membro.idMembro)
                                }
                            )
                        }
                    }
                }
            }

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("Erro")) Color.Red else ProjetoVerde,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (membros.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum membro alocado neste projeto.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                items(membros) { membro ->
                    val ativas = tarefaViewModel.contarDoMembroPorStatus(tarefas, membro.idMembro, "Em_Andamento")
                    val concluidas = tarefaViewModel.contarDoMembroPorStatus(tarefas, membro.idMembro, "Concluida")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProjetoAvatar(nome = membro.nome)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(membro.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(membro.cargo, fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ATIVAS", fontSize = 10.sp, color = Color.Gray)
                                Text("$ativas/3", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("$concluidas concluídas", fontSize = 11.sp, color = Color.Gray)
                                TextButton(onClick = { onDesvincular(membro.idMembro) }) {
                                    Text("Remover", color = Color.Red, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjetoCronogramaTab(
    tarefas: List<Tarefa>,
    dateFormat: SimpleDateFormat,
    dataInicio: Long,
    dataFim: Long,
    emAndamento: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProjetoInfoLinha("Início", dateFormat.format(Date(dataInicio)))
                Divider(color = Color(0xFFEEEEEE))
                ProjetoInfoLinha("Prazo final", dateFormat.format(Date(dataFim)))
                Divider(color = Color(0xFFEEEEEE))
                ProjetoInfoLinha("Em andamento", "$emAndamento tarefas")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("ORDEM DO CRONOGRAMA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        tarefas.forEachIndexed { index, tarefa ->
            ProjetoCronogramaLinha(index + 1, tarefa, dateFormat)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProjetoTarefaLinha(tarefa: Tarefa, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tarefa.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Prazo: ${dateFormat.format(Date(tarefa.prazo))}", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                BadgePrioridade(tarefa.prioridade)
                Spacer(modifier = Modifier.height(4.dp))
                BadgeStatus(tarefa.status)
            }
        }
    }
}

@Composable
private fun ProjetoCronogramaLinha(posicao: Int, tarefa: Tarefa, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text("$posicao", color = ProjetoVerde, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tarefa.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(dateFormat.format(Date(tarefa.prazo)), fontSize = 12.sp, color = Color.Gray)
            }
            BadgePrioridade(tarefa.prioridade)
        }
    }
}

@Composable
private fun ProjetoStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = ProjetoFundo) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
        }
    }
}

@Composable
private fun ProjetoInfoLinha(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
    }
}

@Composable
private fun ProjetoAvatar(nome: String) {
    val iniciais = nome.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        if (iniciais.isBlank()) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
        } else {
            Text(iniciais, color = Color(0xFF424242), fontWeight = FontWeight.Bold)
        }
    }
}
