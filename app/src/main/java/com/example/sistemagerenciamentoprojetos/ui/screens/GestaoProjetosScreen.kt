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
import androidx.compose.material.icons.filled.Folder
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
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoResumo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestaoProjetosScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCadastro: () -> Unit,
    onNavigateToDetalhes: (Int) -> Unit,
    viewModel: ProjetosViewModel = viewModel()
) {
    val projetos by viewModel.projetosResumo.collectAsState()
    var pesquisa by remember { mutableStateOf("") }
    var filtroSituacao by remember { mutableStateOf("Todos") }

    val projetosOrdenados = viewModel.filtrarProjetos(projetos, pesquisa, filtroSituacao)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PROJETOS", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Folder,
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
                Icon(Icons.Default.Add, contentDescription = "Adicionar Projeto")
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
                    modifier = Modifier.height(48.dp).width(160.dp),
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Gray,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            ProjectFilterMenu(
                label = "Situação",
                value = filtroSituacao,
                options = listOf("Todos", "Atrasados", "No limite", "Em andamento", "Concluídos"),
                modifier = Modifier.fillMaxWidth()
            ) {
                filtroSituacao = it
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("↑ ORDENAR POR: PRIORIDADE E PRAZO", fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            if (projetosOrdenados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum projeto encontrado.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(projetosOrdenados) { projeto ->
                        ProjetoCard(
                            projeto = projeto,
                            onClick = { onNavigateToDetalhes(projeto.idProjeto) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectFilterMenu(
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
fun ProjetoCard(projeto: ProjetoResumo, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val progresso = if (projeto.totalTarefas == 0) 0f else {
        (projeto.tarefasConcluidas.toFloat() / projeto.totalTarefas).coerceIn(0f, 1f)
    }
    val limiteCheio = projeto.limiteTarefas > 0 && projeto.tarefasAtivas >= projeto.limiteTarefas

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF0F9D58))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = projeto.nome,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                        if (limiteCheio || projeto.tarefasAtrasadas > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Projeto com atenção",
                                tint = if (limiteCheio) Color.Red else Color(0xFFF57C00),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "Prazo final: ${dateFormat.format(Date(projeto.dataFim))}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("ATIVAS", fontSize = 10.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${projeto.tarefasAtivas}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/${projeto.limiteTarefas}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = progresso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (limiteCheio) Color.Red else Color(0xFF0F9D58),
                trackColor = Color(0xFFEEEEEE)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProjetoMiniBadge(text = "${(progresso * 100).toInt()}% concluído", color = Color(0xFF0F9D58))
                Spacer(modifier = Modifier.width(8.dp))
                ProjetoMiniBadge(text = "${projeto.totalMembros} membros", color = Color(0xFF1565C0))
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(projeto.totalMembros.coerceAtMost(3)) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjetoMiniBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
