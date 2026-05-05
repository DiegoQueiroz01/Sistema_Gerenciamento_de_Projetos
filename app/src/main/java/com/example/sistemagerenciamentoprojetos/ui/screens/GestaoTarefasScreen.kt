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
    onNavigateToDetalhe: (Int) -> Unit,
    viewModel: TarefaViewModel = viewModel(),
    projetosViewModel: ProjetosViewModel = viewModel(),
    membrosViewModel: MembrosViewModel = viewModel()
) {
    val tarefas by viewModel.tarefas.collectAsState()
    val projetos by projetosViewModel.projetos.collectAsState()
    val membros by membrosViewModel.membros.collectAsState()
    var pesquisa by remember { mutableStateOf("") }
    var filtroPrioridade by remember { mutableStateOf("Todas") }
    var filtroStatus by remember { mutableStateOf("Todos") }
    var filtroProjetoId by remember { mutableStateOf(0) }
    var filtroMembroId by remember { mutableStateOf(0) }
    var ordenacao by remember { mutableStateOf("Prazo") }
    val scope = rememberCoroutineScope()
    val agora = System.currentTimeMillis()

    fun nomeProjeto(id: Int): String = projetos.firstOrNull { it.idProjeto == id }?.nome ?: "Projeto $id"
    fun nomeMembro(id: Int): String = membros.firstOrNull { it.idMembro == id }?.nome ?: ""

    val tarefasFiltradas = viewModel.filtrarGestao(
        tarefas = tarefas,
        projetos = projetos,
        membros = membros,
        pesquisa = pesquisa,
        prioridade = filtroPrioridade,
        status = filtroStatus,
        idProjeto = filtroProjetoId,
        idMembro = filtroMembroId,
        ordenacao = ordenacao,
        agora = agora
    )

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Text(
                        " FILTRAR POR:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextField(
                        value = pesquisa,
                        onValueChange = { pesquisa = it },
                        placeholder = { Text("Pesquisar...") },
                        modifier = Modifier.height(48.dp).width(170.dp),
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TaskFilterMenu("Prioridade", filtroPrioridade, listOf("Todas", "Alta", "Media", "Baixa"), Modifier.weight(1f)) {
                        filtroPrioridade = it
                    }
                    TaskFilterMenu("Status", filtroStatus, listOf("Todos", "Não iniciada", "Em andamento", "Concluída", "Atrasadas"), Modifier.weight(1f)) {
                        filtroStatus = it
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    TaskFilterMenu(
                        label = "Projeto",
                        value = if (filtroProjetoId == 0) "Todos" else nomeProjeto(filtroProjetoId),
                        options = listOf("Todos") + projetos.map { it.nome },
                        modifier = Modifier.weight(1f)
                    ) { escolhido ->
                        filtroProjetoId = projetos.firstOrNull { it.nome == escolhido }?.idProjeto ?: 0
                    }
                    TaskFilterMenu(
                        label = "Responsável",
                        value = if (filtroMembroId == 0) "Todos" else nomeMembro(filtroMembroId),
                        options = listOf("Todos") + membros.map { it.nome },
                        modifier = Modifier.weight(1f)
                    ) { escolhido ->
                        filtroMembroId = membros.firstOrNull { it.nome == escolhido }?.idMembro ?: 0
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("↑ ORDENAR POR:", fontSize = 10.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                listOf("Prazo", "Prioridade", "Projeto", "Responsável").forEach { opcao ->
                    FilterChip(
                        selected = ordenacao == opcao,
                        onClick = { ordenacao = opcao },
                        label = { Text(opcao, fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (tarefasFiltradas.isEmpty()) {
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
                    items(tarefasFiltradas) { tarefa ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskFilterMenu(
    label: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
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
                    progress = when (tarefa.status) {
                        "Concluida" -> 1f
                        "Em_Andamento" -> 0.5f
                        else -> 0f
                    },
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
