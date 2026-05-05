package com.example.sistemagerenciamentoprojetos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val Verde = Color(0xFF1B7A4A)
private val FundoCinza = Color(0xFFF2F2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    tarefaId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEditar: (Int) -> Unit,
    tarefaViewModel: TarefaViewModel = viewModel(),
    membrosViewModel: MembrosViewModel = viewModel(),
    projetosViewModel: ProjetosViewModel = viewModel()
) {
    var tarefa by remember { mutableStateOf<Tarefa?>(null) }
    var projeto by remember { mutableStateOf<Projeto?>(null) }

    // PASSO 1 — Declara o estado que vai armazenar o membro responsável
    var membro by remember { mutableStateOf<Membro?>(null) }

    var mensagemSnackbar by remember { mutableStateOf<String?>(null) }
    var tempoEfetivoTexto by remember { mutableStateOf("") }
    val todasTarefas by tarefaViewModel.tarefas.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("pt", "BR")) }

    // PASSO 2 — Ao carregar a tarefa, usa o idMembro dela para buscar o responsável no banco
    LaunchedEffect(tarefaId) {
        tarefa = tarefaViewModel.getTarefaById(tarefaId)
        tarefa?.let { t ->
            projeto = projetosViewModel.getProjetoById(t.idProjeto)
            tempoEfetivoTexto = t.tempoEfetivo.toString()
            if (t.idMembro != 0) {                                   // 0 = sem responsável
                membro = membrosViewModel.getMembroById(t.idMembro)  // consulta Room via coroutine
            } else {
                membro = null
            }
        }
    }

    LaunchedEffect(mensagemSnackbar) {
        mensagemSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            mensagemSnackbar = null
        }
    }

    Scaffold(
        containerColor = FundoCinza,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "Detalhe da tarefa",
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
                    TextButton(onClick = { onNavigateToEditar(tarefaId) }) {
                        Text("Editar", color = Verde, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { paddingValues ->

        tarefa?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                // ── Título + Badges ───────────────────────────────────────
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BadgePrioridade(t.prioridade)
                            BadgeStatus(t.status)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = t.titulo,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Descrição ─────────────────────────────────────────────
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                        OutlinedTextField(
                            value = if (t.descricao.isNullOrBlank()) "" else t.descricao,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Descrição", color = Color.Gray, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.LightGray,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            minLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Registro de Tempo ─────────────────────────────────────
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text("REGISTRO DE TEMPO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Color.Gray, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        val restante = (t.tempoEstimado - t.tempoEfetivo).coerceAtLeast(0f)
                        val efetivHoras = t.tempoEfetivo.toInt()
                        val efetivMin = ((t.tempoEfetivo - efetivHoras) * 60).toInt()
                        val efetivTexto = if (efetivMin > 0) "${efetivHoras}h ${efetivMin}m" else "${efetivHoras}h"

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CaixaTempo("ESTIMADO", "${t.tempoEstimado.toInt()}h", Modifier.weight(1f))
                            CaixaTempo("EFETIVO", efetivTexto, Modifier.weight(1f))
                            CaixaTempo("RESTANTE", "~${restante.toInt()}h", Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tempoEfetivoTexto,
                                onValueChange = { tempoEfetivoTexto = it },
                                label = { Text("Tempo efetivo (h)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        val resultado = tarefaViewModel.registrarTempoEfetivo(
                                            t.idTarefa,
                                            tempoEfetivoTexto.toFloatOrNull() ?: 0f
                                        )
                                        tarefa = tarefaViewModel.getTarefaById(t.idTarefa)
                                        mensagemSnackbar = resultado
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Verde)
                            ) {
                                Text("Salvar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── PASSO 3 — Seção: Responsável ─────────────────────────
                // Exibe os dados do membro atribuído à tarefa (nome, cargo, e-mail e badge de carga).
                // Caso nenhum responsável esteja atribuído (idMembro == 0), exibe mensagem informativa.
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                        Text("RESPONSÁVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Color.Gray, letterSpacing = 1.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        if (membro != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Avatar com iniciais do nome
                                AvatarIniciais(nome = membro!!.nome)

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = membro!!.nome,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = membro!!.cargo,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = membro!!.email,
                                        fontSize = 12.sp,
                                        color = Verde
                                    )
                                }

                                // Badge: tarefas ativas / limite
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFFFF0E0)
                                ) {
                                    val tarefasAtivasMembro = tarefaViewModel.contarDoMembroPorStatus(
                                        todasTarefas,
                                        membro!!.idMembro,
                                        "Em_Andamento"
                                    )
                                    Text(
                                        text = "$tarefasAtivasMembro / 3",
                                        color = Color(0xFFF57C00),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        } else {
                            // Nenhum responsável atribuído
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Nenhum responsável atribuído",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Prazo + Projeto ───────────────────────────────────────
                Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        InfoLinha(
                            label = "Prazo final",
                            valor = dateFormat.format(Date(t.prazo)),
                            valorColor = Color(0xFF1A1A1A)
                        )
                        Divider(color = Color(0xFFEEEEEE))
                        InfoLinha(
                            label = "Projeto",
                            valor = projeto?.nome ?: "Projeto ${t.idProjeto}",
                            valorColor = Verde
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Botões de ação ────────────────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val resultado = tarefaViewModel.atualizarStatus(t.idTarefa, "Em_Andamento")
                                tarefa = tarefaViewModel.getTarefaById(t.idTarefa)
                                mensagemSnackbar = resultado
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        enabled = t.status == "Nao_Iniciada"
                    ) {
                        Text(
                            text = if (t.status == "Em_Andamento") "Tarefa em andamento" else "Iniciar tarefa",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                val resultado = tarefaViewModel.atualizarStatus(t.idTarefa, "Concluida")
                                tarefa = tarefaViewModel.getTarefaById(t.idTarefa)
                                mensagemSnackbar = resultado
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Verde),
                        enabled = t.status != "Concluida"
                    ) {
                        Text(
                            text = if (t.status == "Concluida") "Tarefa concluída ✓" else "Marcar como concluída",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                tarefaViewModel.deletar(t.idTarefa)
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Text("Remover tarefa", fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                            color = Color(0xFFD32F2F))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Verde)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes auxiliares
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CaixaTempo(label: String, valor: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = FundoCinza) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = Color.Gray,
                fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun InfoLinha(label: String, valor: String, valorColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valorColor)
    }
}

// Gera avatar circular com as iniciais (até 2) do nome do responsável
@Composable
private fun AvatarIniciais(nome: String) {
    val iniciais = nome.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFFB2DFDB)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = iniciais, color = Color(0xFF00695C), fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

// BadgePrioridade e BadgeStatus estão definidos em GestaoTarefasScreen.kt — não redefina aqui
