package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestaoMembrosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    onNavigateToDetalhes: (Int) -> Unit,
    viewModel: MembrosViewModel = viewModel(),
    tarefaViewModel: TarefaViewModel = viewModel()
) {
    val membros by viewModel.membros.collectAsState()
    val tarefas by tarefaViewModel.tarefas.collectAsState()
    var pesquisa by remember { mutableStateOf("") }
    var filtroCargo by remember { mutableStateOf("Todos") }
    var filtroCarga by remember { mutableStateOf("Todos") }
    val termoBusca = pesquisa.normalizarBusca()
    val cargos = listOf("Todos") + membros
        .map { it.cargo.orEmpty() }
        .filtrarComListaDinamica { it.isNotBlank() }
        .distinct()
        .sortedBy { it.normalizarBusca() }

    fun tarefasAtivasDoMembro(idMembro: Int): Int {
        return tarefas.contarComListaDinamica { it.idMembro == idMembro && it.status == "Em_Andamento" }
    }

    val membrosFiltrados = membros
        .filtrarComListaDinamica { membro ->
            val tarefasAtivas = tarefasAtivasDoMembro(membro.idMembro)
            val passaBusca = termoBusca.isBlank() ||
                    membro.idMembro.toString().contains(termoBusca) ||
                    membro.nome.orEmpty().normalizarBusca().contains(termoBusca) ||
                    membro.cargo.orEmpty().normalizarBusca().contains(termoBusca) ||
                    membro.email.orEmpty().normalizarBusca().contains(termoBusca)
            val passaCargo = filtroCargo == "Todos" || membro.cargo.orEmpty() == filtroCargo
            val passaCarga = when (filtroCarga) {
                "Disponíveis" -> tarefasAtivas < 3
                "Com tarefas" -> tarefasAtivas > 0
                "No limite" -> tarefasAtivas >= 3
                else -> true
            }

            passaBusca && passaCargo && passaCarga
        }
        .sortedBy { it.nome.lowercase() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("MEMBROS", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .padding(4.dp)
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
                Icon(Icons.Default.Add, contentDescription = "Adicionar Membro")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Filtro e Busca
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Text(
                    " FILTRAR POR:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                TextField(
                    value = pesquisa,
                    onValueChange = { pesquisa = it },
                    placeholder = { Text("Pesquisar...") },
                    modifier = Modifier.height(48.dp).width(150.dp),
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                MemberFilterMenu(
                    label = "Cargo",
                    value = filtroCargo,
                    options = cargos,
                    modifier = Modifier.weight(1f)
                ) {
                    filtroCargo = it
                }
                MemberFilterMenu(
                    label = "Carga",
                    value = filtroCarga,
                    options = listOf("Todos", "Disponíveis", "Com tarefas", "No limite"),
                    modifier = Modifier.weight(1f)
                ) {
                    filtroCarga = it
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("↑ ORDENAR POR: A-Z", fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            if (membrosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum membro encontrado.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(membrosFiltrados) { membro ->
                        val ativas = tarefasAtivasDoMembro(membro.idMembro)
                        MembroCard(
                            membro = membro,
                            tarefasAtivas = ativas,
                            onClick = { onNavigateToDetalhes(membro.idMembro) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberFilterMenu(
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
                focusedIndicatorColor = Color.Gray,
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
fun MembroCard(membro: Membro, tarefasAtivas: Int = membro.tarefasAtivas, onClick: () -> Unit) {
    val maxTarefas = 3
    val progress = (tarefasAtivas.toFloat() / maxTarefas).coerceAtMost(1f)
    val isFull = tarefasAtivas >= maxTarefas

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
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = membro.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Gray
                    )
                    if (isFull) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Limite atingido",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = membro.cargo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isFull) Color.Red else Color(0xFF0F9D58),
                    trackColor = Color(0xFFEEEEEE)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text("ATIVAS", fontSize = 10.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$tarefasAtivas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "/$maxTarefas",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}
