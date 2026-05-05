package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import com.example.sistemagerenciamentoprojetos.domain.tarefas.TarefaService
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto
import com.example.sistemagerenciamentoprojetos.domain.servicos.GerenciadorTarefas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import kotlinx.coroutines.flow.Flow

class TarefaViewModel(application: Application) : AndroidViewModel(application) {


    private val db = AppDatabase.getInstance(application)
    private val tarefaDao = db.tarefaDao()
    private val tarefaService = TarefaService(application)
    private val gerenciadorTarefas = GerenciadorTarefas()

    val tarefas: StateFlow<List<Tarefa>> = tarefaDao.listarTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Lista reativa de membros para popular o dropdown
    val membros: StateFlow<List<Membro>> = db.membroDao().listarTodosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()

        )

    suspend fun cadastrar(
        idProjeto: Int,
        limiteProjeto: Int,
        titulo: String,
        descricao: String,
        prioridade: String,
        prazo: Long,
        tempoEstimado: Float,
        idMembro: Int
    ): String {
        return withContext(Dispatchers.IO) {
            tarefaService.cadastrarTarefa(
                idProjeto, limiteProjeto, titulo, descricao,
                prioridade, prazo, tempoEstimado, idMembro
            )
        }
    }


    suspend fun atualizarStatus(idTarefa: Int, novoStatus: String): String {
        return withContext(Dispatchers.IO) {
            tarefaService.atualizarStatus(idTarefa, novoStatus)
        }
    }

    suspend fun atualizar(
        idTarefa: Int,
        idProjeto: Int,
        titulo: String,
        descricao: String,
        prioridade: String,
        prazo: Long,
        tempoEstimado: Float,
        idMembro: Int
    ): String {
        return withContext(Dispatchers.IO) {
            tarefaService.atualizarTarefa(
                idTarefa, idProjeto, titulo, descricao,
                prioridade, prazo, tempoEstimado, idMembro
            )
        }
    }

    suspend fun registrarTempoEfetivo(idTarefa: Int, tempoEfetivo: Float): String {
        return withContext(Dispatchers.IO) {
            tarefaService.registrarTempoEfetivo(idTarefa, tempoEfetivo)
        }
    }

    suspend fun getTarefaById(id: Int): Tarefa? {
        return withContext(Dispatchers.IO) {
            tarefaDao.buscarPorId(id)
        }
    }
    suspend fun deletar(idTarefa: Int) {
        withContext(Dispatchers.IO) {
            tarefaService.deletarTarefa(idTarefa)
        }
    }
    fun ordenar(lista: List<Tarefa>): List<Tarefa> {
        return tarefaService.ordenarTarefas(lista)
    }

    fun filtrarGestao(
        tarefas: List<Tarefa>,
        projetos: List<Projeto>,
        membros: List<Membro>,
        pesquisa: String,
        prioridade: String,
        status: String,
        idProjeto: Int,
        idMembro: Int,
        ordenacao: String,
        agora: Long
    ): List<Tarefa> {
        val filtradas = gerenciadorTarefas.filtrarTarefasGestao(
            tarefas, projetos, membros, pesquisa, prioridade, status, idProjeto, idMembro, agora
        )
        return gerenciadorTarefas.ordenarTarefasParaGestao(filtradas, projetos, membros, ordenacao)
    }

    fun listarPendentes(tarefas: List<Tarefa>): List<Tarefa> {
        return gerenciadorTarefas.listarTarefasPendentes(tarefas)
    }

    fun listarAtrasadas(tarefas: List<Tarefa>, agora: Long): List<Tarefa> {
        return gerenciadorTarefas.listarTarefasAtrasadas(tarefas, agora)
    }

    fun filtrarPorPrioridade(tarefas: List<Tarefa>, prioridade: String): List<Tarefa> {
        return gerenciadorTarefas.filtrarTarefasPorPrioridade(tarefas, prioridade)
    }

    fun filtrarPorStatus(tarefas: List<Tarefa>, status: String): List<Tarefa> {
        return gerenciadorTarefas.filtrarTarefasPorStatus(tarefas, status)
    }

    fun filtrarPorProjeto(tarefas: List<Tarefa>, idProjeto: Int): List<Tarefa> {
        return gerenciadorTarefas.filtrarTarefasPorProjeto(tarefas, idProjeto)
    }

    fun filtrarPorMembro(tarefas: List<Tarefa>, idMembro: Int): List<Tarefa> {
        return gerenciadorTarefas.filtrarTarefasPorMembro(tarefas, idMembro)
    }

    fun contarPorStatus(tarefas: List<Tarefa>, status: String): Int {
        return gerenciadorTarefas.contarTarefasPorStatus(tarefas, status)
    }

    fun contarAtivas(tarefas: List<Tarefa>): Int {
        return gerenciadorTarefas.contarTarefasAtivas(tarefas)
    }

    fun contarAtrasadas(tarefas: List<Tarefa>, agora: Long): Int {
        return gerenciadorTarefas.contarTarefasAtrasadas(tarefas, agora)
    }

    fun contarConcluidasNoPrazo(tarefas: List<Tarefa>, agora: Long): Int {
        return gerenciadorTarefas.contarTarefasConcluidasNoPrazo(tarefas, agora)
    }

    fun contarDoMembroPorStatus(tarefas: List<Tarefa>, idMembro: Int, status: String): Int {
        return gerenciadorTarefas.contarTarefasDoMembroPorStatus(tarefas, idMembro, status)
    }
    fun tarefasDoProjeto(idProjeto: Int): Flow<List<Tarefa>> {
        return tarefaDao.listarPorProjeto(idProjeto)
    }
    fun tarefasDoMembro(idMembro: Int): Flow<List<Tarefa>> {
        return tarefaDao.listarPorMembro(idMembro)
    }
    suspend fun atribuirMembro(idTarefa: Int, idMembro: Int): String {
        return withContext(Dispatchers.IO) {
            tarefaService.atribuirMembro(idTarefa, idMembro)
        }
    }
}
