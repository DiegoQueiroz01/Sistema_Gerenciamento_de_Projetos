package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa
import com.example.sistemagerenciamentoprojetos.domain.tarefas.TarefaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import kotlinx.coroutines.flow.MutableStateFlow

class TarefaViewModel(application: Application) : AndroidViewModel(application) {


    private val db = AppDatabase.getInstance(application)
    private val tarefaDao = db.tarefaDao()
    private val tarefaService = TarefaService(application)

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
        tempoEstimado: Float
    ): String {
        return withContext(Dispatchers.IO) {
            tarefaService.cadastrarTarefa(
                idProjeto, limiteProjeto,
                titulo, descricao,
                prioridade, prazo, tempoEstimado
            )
        }
    }


    suspend fun atualizarStatus(idTarefa: Int, novoStatus: String): String {
        return withContext(Dispatchers.IO) {
            tarefaService.atualizarStatus(idTarefa, novoStatus)
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
    suspend fun atribuirMembro(idTarefa: Int, idMembro: Int): String {
        return withContext(Dispatchers.IO) {
            tarefaService.atribuirMembro(idTarefa, idMembro)
        }
    }
}