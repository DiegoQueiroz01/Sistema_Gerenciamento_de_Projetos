package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import com.example.sistemagerenciamentoprojetos.domain.servicos.GerenciadorMembros
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class MembrosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val membroDao = db.membroDao()
    private val gerenciadorMembros = GerenciadorMembros()

    val membros: StateFlow<List<Membro>> = membroDao.listarTodosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getMembroById(id: Int): Membro? {
        return withContext(Dispatchers.IO) {
            membroDao.buscarPorId(id)
        }
    }

    suspend fun atualizar(idMembro: Int, nome: String, cargo: String, email: String): String {
        return withContext(Dispatchers.IO) {
            val membro = membroDao.buscarPorId(idMembro) ?: return@withContext "Erro: Membro não encontrado."

            if (nome.isBlank() || cargo.isBlank() || email.isBlank()) {
                return@withContext "Todos os campos são obrigatórios."
            }

            val emailNormalizado = email.trim().lowercase()
            if (membroDao.contarEmailExceto(emailNormalizado, idMembro) > 0) {
                return@withContext "Erro: Já existe um membro cadastrado com este e-mail."
            }

            membro.nome = nome.trim()
            membro.cargo = cargo.trim()
            membro.email = emailNormalizado
            membroDao.atualizar(membro)
            "Membro atualizado com sucesso!"
        }
    }

    suspend fun deletar(idMembro: Int) {
        withContext(Dispatchers.IO) {
            db.tarefaDao().removerResponsavel(idMembro)
            db.projetoDao().desvincularMembroDeTodosProjetos(idMembro)
            membroDao.deletar(idMembro)
        }
    }

    fun filtrarMembros(
        membros: List<Membro>,
        tarefas: List<Tarefa>,
        pesquisa: String,
        cargo: String,
        carga: String
    ): List<Membro> {
        return gerenciadorMembros.filtrarMembros(membros, tarefas, pesquisa, cargo, carga)
    }

    fun listarCargos(membros: List<Membro>): List<String> {
        return gerenciadorMembros.listarCargos(membros)
    }

    fun contarTarefasEmAndamento(tarefas: List<Tarefa>, idMembro: Int): Int {
        return gerenciadorMembros.contarTarefasEmAndamentoDoMembro(tarefas, idMembro)
    }
}
