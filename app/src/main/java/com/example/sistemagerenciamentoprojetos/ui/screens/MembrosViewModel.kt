package com.example.sistemagerenciamentoprojetos.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class MembrosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val membroDao = db.membroDao()

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
}
