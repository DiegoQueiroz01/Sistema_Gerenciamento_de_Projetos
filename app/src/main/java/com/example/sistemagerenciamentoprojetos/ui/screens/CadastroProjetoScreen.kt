package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProjetoScreen(
    onNavigateBack: () -> Unit,
    projetoId: Int? = null,
    viewModel: ProjetosViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var dataInicioMillis by remember { mutableStateOf(0L) }
    var dataFimMillis by remember { mutableStateOf(0L) }
    var dataInicioTexto by remember { mutableStateOf("Selecionar data") }
    var dataFimTexto by remember { mutableStateOf("Selecionar prazo") }
    var limiteTarefas by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var tentouCriar by remember { mutableStateOf(false) }
    val editando = projetoId != null && projetoId != 0

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendarInicio = Calendar.getInstance()
    val calendarFim = Calendar.getInstance()

    val datePickerInicio = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendarInicio.set(year, month, day)
            dataInicioMillis = calendarInicio.timeInMillis
            dataInicioTexto = dateFormat.format(calendarInicio.time)
        },
        calendarInicio.get(Calendar.YEAR),
        calendarInicio.get(Calendar.MONTH),
        calendarInicio.get(Calendar.DAY_OF_MONTH)
    )

    val datePickerFim = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendarFim.set(year, month, day)
            dataFimMillis = calendarFim.timeInMillis
            dataFimTexto = dateFormat.format(calendarFim.time)
        },
        calendarFim.get(Calendar.YEAR),
        calendarFim.get(Calendar.MONTH),
        calendarFim.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(projetoId) {
        if (editando) {
            val projeto = viewModel.getProjetoById(projetoId ?: 0)
            projeto?.let {
                nome = it.nome
                descricao = it.descricao
                dataInicioMillis = it.dataInicio
                dataFimMillis = it.dataFim
                dataInicioTexto = dateFormat.format(Date(it.dataInicio))
                dataFimTexto = dateFormat.format(Date(it.dataFim))
                limiteTarefas = it.limiteTarefas.toString()
            }
        }
    }

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
                        if (editando) "Editar Projeto" else "Novo Projeto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.Black)
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
            Text("NOME DO PROJETO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                placeholder = { Text("Ex: Aplicativo de Gestão Interna", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            WarningMsg("Nome é obrigatório", tentouCriar && nome.isBlank())

            Text("DESCRIÇÃO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(top = 4.dp),
                placeholder = { Text("Descreva o objetivo do projeto...", color = Color.Gray) },
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

            Text("DATA DE INÍCIO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedButton(
                onClick = { datePickerInicio.show() },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(dataInicioTexto, color = Color.Gray)
            }
            WarningMsg("Selecione a data de início", tentouCriar && dataInicioMillis == 0L)

            Text("PRAZO FINAL *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedButton(
                onClick = { datePickerFim.show() },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(dataFimTexto, color = Color.Gray)
            }
            WarningMsg("Selecione o prazo final", tentouCriar && dataFimMillis == 0L)

            Text("LIMITE DE TAREFAS ATIVAS *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = limiteTarefas,
                onValueChange = { limiteTarefas = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                placeholder = { Text("Ex: 25", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            WarningMsg("Informe um limite maior que zero", tentouCriar && (limiteTarefas.toIntOrNull() ?: 0) <= 0)

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("sucesso") || mensagem.contains("atualizado")) Color(0xFF2E7D32) else Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    tentouCriar = true
                    val limite = limiteTarefas.toIntOrNull() ?: 0
                    if (nome.isBlank() || descricao.isBlank() || dataInicioMillis == 0L ||
                        dataFimMillis == 0L || limite <= 0) return@Button

                    scope.launch {
                        val resultado = if (editando) {
                            viewModel.atualizar(
                                idProjeto = projetoId ?: 0,
                                nome = nome,
                                descricao = descricao,
                                dataInicio = dataInicioMillis,
                                dataFim = dataFimMillis,
                                limiteTarefas = limite
                            )
                        } else {
                            viewModel.cadastrar(
                                nome = nome,
                                descricao = descricao,
                                dataInicio = dataInicioMillis,
                                dataFim = dataFimMillis,
                                limiteTarefas = limite
                            )
                        }
                        mensagem = resultado
                        if (!editando && resultado.contains("sucesso")) {
                            nome = ""
                            descricao = ""
                            dataInicioMillis = 0L
                            dataFimMillis = 0L
                            dataInicioTexto = "Selecionar data"
                            dataFimTexto = "Selecionar prazo"
                            limiteTarefas = ""
                            tentouCriar = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (editando) "Salvar alterações" else "Cadastrar Projeto", color = Color.White, fontWeight = FontWeight.Bold)
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
