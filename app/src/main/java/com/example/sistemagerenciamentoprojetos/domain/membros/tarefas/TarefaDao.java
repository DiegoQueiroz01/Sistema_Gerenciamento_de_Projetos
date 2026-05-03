package com.example.sistemagerenciamentoprojetos.domain.membros.tarefas;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface TarefaDao {

    @Insert
    void inserir(Tarefa tarefa);

    // Atualiza uma tarefa já existente
    @Update
    void atualizar(Tarefa tarefa);

    // Busca todas as tarefas
    @Query("SELECT * FROM tarefas")
    Flow<List<Tarefa>> listarTodas();

    // Busca as tarefas de um projeto específico
    @Query("SELECT * FROM tarefas WHERE idProjeto = :idProjeto")
    Flow<List<Tarefa>> listarPorProjeto(int idProjeto);

    // Busca uma tarefa pelo ID
    @Query("SELECT * FROM tarefas WHERE idTarefa = :id LIMIT 1")
    Tarefa buscarPorId(int id);

    //conta quantas tarefas "Em_Andamento" um membro tem

    @Query("SELECT COUNT(*) FROM tarefas WHERE idMembro = :idMembro AND status = 'Em_Andamento'")
    int contarTarefasAtivasMembro(int idMembro);

    // conta quantas tarefas ativas um projeto tem

    @Query("SELECT COUNT(*) FROM tarefas WHERE idProjeto = :idProjeto AND status != 'Concluida'")
    int contarTarefasAtivasProjeto(int idProjeto);
}