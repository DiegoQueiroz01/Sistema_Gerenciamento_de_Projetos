package com.example.sistemagerenciamentoprojetos.domain.tarefas;

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

    // Retorna o rowId gerado pelo Room (equivale ao idTarefa com autoGenerate)
    @Insert
    long inserirRetornandoId(Tarefa tarefa);

    @Update
    void atualizar(Tarefa tarefa);

    @Query("SELECT * FROM tarefas")
    Flow<List<Tarefa>> listarTodas();


    @Query("SELECT * FROM tarefas WHERE idProjeto = :idProjeto")
    Flow<List<Tarefa>> listarPorProjeto(int idProjeto);

    @Query("SELECT * FROM tarefas WHERE idTarefa = :id LIMIT 1")
    Tarefa buscarPorId(int id);


    @Query("SELECT COUNT(*) FROM tarefas WHERE idMembro = :idMembro AND status = 'Em_Andamento'")
    int contarTarefasAtivasMembro(int idMembro);


    @Query("SELECT COUNT(*) FROM tarefas WHERE idProjeto = :idProjeto AND status != 'Concluida'")
    int contarTarefasAtivasProjeto(int idProjeto);

    @Query("DELETE FROM tarefas WHERE idTarefa = :id")
    void deletar(int id);


}