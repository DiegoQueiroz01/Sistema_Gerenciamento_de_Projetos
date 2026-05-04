package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroTarefaScreen(
    onNavigateBack: () -> Unit,
    viewModel: TarefaViewModel = viewModel(),
    membrosViewModel: MembrosViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf("Media") }
    var prazoMillis by remember { mutableStateOf(0L) }
    var prazoTexto by remember { mutableStateOf("Selecionar data") }
    var tempoEstimado by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var membroSelecionadoId by remember { mutableStateOf(0) }
    var membroSelecionadoNome by remember { mutableStateOf("") }
    var expandirMembros by remember { mutableStateOf(false) }
    var tentouCriar by remember { mutableStateOf(false) }

    val membros by membrosViewModel.membros.collectAsState()

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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

    @Composable
    fun WarningMsg(texto: String, mostrar: Boolean) {
        if (mostrar) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF57C00),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(texto, color = Color(0xFFF57C00), fontSize = 12.sp)
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nova Tarefa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
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
        ) {
            Text("TÍTULO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                placeholder = { Text("Ex: Implementar autenticação JWT", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            WarningMsg("Título é obrigatório", tentouCriar && titulo.isBlank())

            Text("DESCRIÇÃO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 4.dp),
                placeholder = { Text("Descreva o que precisa ser feito...", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            WarningMsg("Descrição é obrigatória", tentouCriar && descricao.isBlank())

            Text("PRIORIDADE *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
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
            Spacer(modifier = Modifier.height(16.dp))

            Text("PRAZO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(prazoTexto, color = Color.Gray)
            }
            WarningMsg("Selecione um prazo", tentouCriar && prazoMillis == 0L)

            Text("TEMPO ESTIMADO (horas)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = tempoEstimado,
                onValueChange = { tempoEstimado = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                placeholder = { Text("Ex: 8", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            WarningMsg("Tempo estimado é obrigatório", tentouCriar && tempoEstimado.isBlank())

            Text("RESPONSÁVEL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            ExposedDropdownMenuBox(
                expanded = expandirMembros,
                onExpandedChange = { expandirMembros = !expandirMembros },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                TextField(
                    value = if (membroSelecionadoId == 0) "" else membroSelecionadoNome,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Adicionar um responsável", color = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMembros) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color(0xFF0F9D58),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandirMembros,
                    onDismissRequest = { expandirMembros = false }
                ) {
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
            WarningMsg("Nenhum responsável atribuído", tentouCriar && membroSelecionadoId == 0)

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("sucesso")) Color(0xFF2E7D32) else Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    tentouCriar = true
                    if (titulo.isBlank() || prazoMillis == 0L ||
                        tempoEstimado.isBlank() || membroSelecionadoId == 0) return@Button
                    scope.launch {
                        val tempo = tempoEstimado.toFloatOrNull() ?: 0f
                        val resultado = viewModel.cadastrar(
                            idProjeto = 1,
                            limiteProjeto = 25,
                            titulo = titulo,
                            descricao = descricao,
                            prioridade = prioridade,
                            prazo = prazoMillis,
                            tempoEstimado = tempo,
                            idMembro = membroSelecionadoId
                        )
                        mensagem = resultado
                        if (resultado.contains("sucesso")) {
                            titulo = ""
                            descricao = ""
                            tempoEstimado = ""
                            prazoTexto = "Selecionar data"
                            prazoMillis = 0L
                            membroSelecionadoId = 0
                            membroSelecionadoNome = ""
                            tentouCriar = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Criar tarefa", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }
}