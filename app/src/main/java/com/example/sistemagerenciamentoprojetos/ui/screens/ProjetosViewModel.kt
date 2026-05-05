package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoResumo
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoService
import com.example.sistemagerenciamentoprojetos.domain.servicos.GerenciadorProjetos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class ProjetosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val projetoDao = db.projetoDao()
    private val projetoService = ProjetoService(application)
    private val gerenciadorProjetos = GerenciadorProjetos()

    val projetos: StateFlow<List<Projeto>> = projetoDao.listarTodosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val projetosResumo: StateFlow<List<ProjetoResumo>> = projetoDao.listarResumoFlow(System.currentTimeMillis())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun cadastrar(
        nome: String,
        descricao: String,
        dataInicio: Long,
        dataFim: Long,
        limiteTarefas: Int
    ): String {
        return withContext(Dispatchers.IO) {
            projetoService.cadastrarProjeto(nome, descricao, dataInicio, dataFim, limiteTarefas)
        }
    }

    suspend fun getProjetoById(id: Int): Projeto? {
        return withContext(Dispatchers.IO) {
            projetoService.buscarPorId(id)
        }
    }

    fun ordenarPorPrioridade(lista: List<ProjetoResumo>): List<ProjetoResumo> {
        return projetoService.ordenarProjetosPorPrioridade(lista)
    }

    fun filtrarProjetos(lista: List<ProjetoResumo>, pesquisa: String, situacao: String): List<ProjetoResumo> {
        return gerenciadorProjetos.filtrarProjetos(lista, pesquisa, situacao)
    }

    fun membrosDoProjeto(idProjeto: Int): Flow<List<Membro>> {
        return projetoDao.listarMembrosPorProjetoFlow(idProjeto)
    }

    fun membrosDisponiveisDoProjeto(idProjeto: Int): Flow<List<Membro>> {
        return projetoDao.listarMembrosDisponiveisProjetoFlow(idProjeto)
    }

    suspend fun atualizar(
        idProjeto: Int,
        nome: String,
        descricao: String,
        dataInicio: Long,
        dataFim: Long,
        limiteTarefas: Int
    ): String {
        return withContext(Dispatchers.IO) {
            projetoService.atualizarProjeto(idProjeto, nome, descricao, dataInicio, dataFim, limiteTarefas)
        }
    }

    suspend fun vincularMembro(idProjeto: Int, idMembro: Int): String {
        return withContext(Dispatchers.IO) {
            projetoService.vincularMembro(idProjeto, idMembro)
        }
    }

    suspend fun desvincularMembro(idProjeto: Int, idMembro: Int): String {
        return withContext(Dispatchers.IO) {
            projetoService.desvincularMembro(idProjeto, idMembro)
        }
    }
}
