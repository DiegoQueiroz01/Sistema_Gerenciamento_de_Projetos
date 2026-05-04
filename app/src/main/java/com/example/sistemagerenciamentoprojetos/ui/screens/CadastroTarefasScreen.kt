package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroTarefaScreen(
    onNavigateBack: () -> Unit,
    viewModel: TarefaViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf("Media") }
    var prazoMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var prazoTexto by remember { mutableStateOf("Selecionar data") }
    var tempoEstimado by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    val membros by viewModel.membros.collectAsStateWithLifecycle()
    var membroSelecionado by remember { mutableStateOf<com.example.sistemagerenciamentoprojetos.domain.membros.Membro?>(null) }
    var dropdownExpandido by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Nova Tarefa",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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

            // Título
            Text(
                "TÍTULO *",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
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


            Text(
                "DESCRIÇÃO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            TextField(
                value = descricao,
                onValueChange = { descricao = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 4.dp, bottom = 16.dp),
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

            Text(
                "PRIORIDADE *",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
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
            Text(
                "RESPONSÁVEL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            if (membros.isEmpty()) {
                // Aviso quando não há membros cadastrados
                Text(
                    "⚠ Nenhum membro cadastrado. Cadastre um membro primeiro.",
                    color = Color(0xFFB26A00),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp)
                ) {
                    TextField(
                        value = membroSelecionado?.nome ?: "Selecionar responsável",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpandido = true },
                        enabled = false,
                        textStyle = TextStyle(
                            color = if (membroSelecionado != null) Color.Black else Color.Gray
                        ),
                        trailingIcon = {
                            Icon(
                                if (dropdownExpandido) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.clickable { dropdownExpandido = !dropdownExpandido }
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF5F5F5),
                            disabledTextColor = if (membroSelecionado != null) Color.Black else Color.Gray,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    DropdownMenu(
                        expanded = dropdownExpandido,
                        onDismissRequest = { dropdownExpandido = false }
                    ) {
                        membros.forEach { membro ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(membro.nome, fontWeight = FontWeight.Medium)
                                        Text(membro.cargo, fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    membroSelecionado = membro
                                    dropdownExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                "PRAZO *",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(prazoTexto, color = Color.Gray)
            }

            Text(
                "TEMPO ESTIMADO (horas)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            TextField(
                value = tempoEstimado,
                onValueChange = { tempoEstimado = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
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

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("sucesso"))
                        Color(0xFF2E7D32) else Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        val tempo = tempoEstimado.toFloatOrNull() ?: 0f
                        val resultado = viewModel.cadastrar(
                            idProjeto = 1,
                            limiteProjeto = 25,
                            titulo = titulo,
                            descricao = descricao,
                            prioridade = prioridade,
                            prazo = prazoMillis,
                            tempoEstimado = tempo
                        )
                        // resultado agora vem como "sucesso:<id>" ou "Erro: ..."
                        if (resultado.startsWith("sucesso:")) {
                            val idTarefa = resultado.removePrefix("sucesso:").toIntOrNull() ?: 0
                            // Atribui o responsável selecionado à tarefa recém-criada
                            if (idTarefa > 0 && membroSelecionado != null) {
                                val resultadoAtribuicao = viewModel.atribuirMembro(idTarefa, membroSelecionado!!.idMembro)
                                mensagem = if (resultadoAtribuicao.contains("sucesso", ignoreCase = true))
                                    "Tarefa cadastrada com sucesso!"
                                else
                                    "Tarefa criada, mas: $resultadoAtribuicao"
                            } else {
                                mensagem = "Tarefa cadastrada com sucesso!"
                            }
                            titulo = ""
                            descricao = ""
                            tempoEstimado = ""
                            prazoTexto = "Selecionar data"
                            membroSelecionado = null
                        } else {
                            mensagem = resultado
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Criar tarefa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }
}