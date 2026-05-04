package com.example.sistemagerenciamentoprojetos.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemagerenciamentoprojetos.domain.membros.cadastrarMembro.CadastroMembroService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroMembroScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cadastroService = remember { CadastroMembroService(context) }

    var nome by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Novo Membro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Nome do Projeto", fontSize = 12.sp, color = Color.Gray)
                    }
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
        ) {
            Text("NOME *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = nome,
                onValueChange = { nome = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                placeholder = { Text("Ex: João Silva", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text("CARGO *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = cargo,
                onValueChange = { cargo = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                placeholder = { Text("Ex: Desenvolvedor Mobile", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text("E-MAIL *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                placeholder = { Text("exemplo@email.com", color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color(0xFF0F9D58),
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            )

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.contains("sucesso")) Color(0xFF2E7D32) else Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            cadastroService.cadastrarMembro(nome, cargo, email)
                        }
                        mensagem = result
                        if (result.contains("sucesso")) {
                            nome = ""
                            cargo = ""
                            email = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cadastrar Membro", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.LightGray))
            ) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }
}
