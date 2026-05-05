package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTarefaScreen(
    tarefaId: Int,
    onNavigateBack: () -> Unit,
    tarefaViewModel: TarefaViewModel = viewModel(),
    projetosViewModel: ProjetosViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val projetos by projetosViewModel.projetos.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf("Media") }
    var prazoMillis by remember { mutableStateOf(0L) }
    var prazoTexto by remember { mutableStateOf("Selecionar data") }
    var tempoEstimado by remember { mutableStateOf("") }
    var projetoSelecionadoId by remember { mutableStateOf(0) }
    var projetoSelecionadoNome by remember { mutableStateOf("") }
    var membroSelecionadoId by remember { mutableStateOf(0) }
    var membroSelecionadoNome by remember { mutableStateOf("") }
    var expandirProjetos by remember { mutableStateOf(false) }
    var expandirMembros by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    val membrosFlow = remember(projetoSelecionadoId) {
        if (projetoSelecionadoId == 0) flowOf(emptyList<Membro>()) else projetosViewModel.membrosDoProjeto(projetoSelecionadoId)
    }
    val membros by membrosFlow.collectAsState(initial = emptyList())

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            prazoMillis = calendar.timeInMillis
            prazoTexto = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(tarefaId) {
        val tarefa = tarefaViewModel.getTarefaById(tarefaId)
        tarefa?.let {
            titulo = it.titulo
            descricao = it.descricao
            prioridade = it.prioridade
            prazoMillis = it.prazo
            prazoTexto = dateFormat.format(Date(it.prazo))
            tempoEstimado = it.tempoEstimado.toString()
            projetoSelecionadoId = it.idProjeto
            projetoSelecionadoNome = projetosViewModel.getProjetoById(it.idProjeto)?.nome ?: "Projeto ${it.idProjeto}"
            membroSelecionadoId = it.idMembro
        }
    }

    LaunchedEffect(membroSelecionadoId, membros) {
        if (membroSelecionadoId != 0) {
            membroSelecionadoNome = membros.firstOrNull { it.idMembro == membroSelecionadoId }?.nome ?: membroSelecionadoNome
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarefa", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CampoTextoTarefa("TÍTULO *", titulo, { titulo = it }, "Título da tarefa")
            CampoTextoTarefa("DESCRIÇÃO *", descricao, { descricao = it }, "Descrição", altura = 100)

            Text("PROJETO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            ExposedDropdownMenuBox(
                expanded = expandirProjetos,
                onExpandedChange = { expandirProjetos = !expandirProjetos },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)
            ) {
                TextField(
                    value = projetoSelecionadoNome,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecionar projeto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirProjetos) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color(0xFF0F9D58),
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(expanded = expandirProjetos, onDismissRequest = { expandirProjetos = false }) {
                    projetos.forEach { projeto ->
                        DropdownMenuItem(
                            text = { Text(projeto.nome) },
                            onClick = {
                                projetoSelecionadoId = projeto.idProjeto
                                projetoSelecionadoNome = projeto.nome
                                membroSelecionadoId = 0
                                membroSelecionadoNome = ""
                                expandirProjetos = false
                            }
                        )
                    }
                }
            }

            Text("PRIORIDADE *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Baixa", "Media", "Alta").forEach { opcao ->
                    FilterChip(
                        selected = prioridade == opcao,
                        onClick = { prioridade = opcao },
                        label = { Text(opcao) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F9D58),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Text("PRAZO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(prazoTexto, color = Color.Gray)
            }

            CampoTextoTarefa("TEMPO ESTIMADO (horas) *", tempoEstimado, { tempoEstimado = it }, "Ex: 8")

            Text("RESPONSÁVEL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            ExposedDropdownMenuBox(
                expanded = expandirMembros,
                onExpandedChange = { expandirMembros = !expandirMembros },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)
            ) {
                TextField(
                    value = if (membroSelecionadoId == 0) "" else membroSelecionadoNome,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Adicionar um responsável") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMembros) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color(0xFF0F9D58),
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(expanded = expandirMembros, onDismissRequest = { expandirMembros = false }) {
                    DropdownMenuItem(
                        text = { Text("Nenhum") },
                        onClick = {
                            membroSelecionadoId = 0
                            membroSelecionadoNome = ""
                            expandirMembros = false
                        }
                    )
                    membros.forEach { membro ->
                        DropdownMenuItem(
                            text = { Text("${membro.nome} — ${membro.cargo}") },
                            onClick = {
                                membroSelecionadoId = membro.idMembro
                                membroSelecionadoNome = membro.nome
                                expandirMembros = false
                            }
                        )
                    }
                }
            }

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("Erro")) Color.Red else Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        val resultado = tarefaViewModel.atualizar(
                            idTarefa = tarefaId,
                            idProjeto = projetoSelecionadoId,
                            titulo = titulo,
                            descricao = descricao,
                            prioridade = prioridade,
                            prazo = prazoMillis,
                            tempoEstimado = tempoEstimado.toFloatOrNull() ?: 0f,
                            idMembro = membroSelecionadoId
                        )
                        mensagem = resultado
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Salvar alterações", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CampoTextoTarefa(
    label: String,
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String,
    altura: Int? = null
) {
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    TextField(
        value = valor,
        onValueChange = onChange,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (altura != null) Modifier.height(altura.dp) else Modifier)
            .padding(top = 4.dp, bottom = 16.dp),
        placeholder = { Text(placeholder, color = Color.Gray) },
        textStyle = TextStyle(color = Color.Black),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color(0xFF0F9D58),
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
