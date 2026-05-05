package com.example.sistemagerenciamentoprojetos.domain.projetos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sistemagerenciamentoprojetos.domain.membros.Membro;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface ProjetoDao {

    @Insert
    long inserirRetornandoId(Projeto projeto);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void vincularMembro(ProjetoMembro projetoMembro);

    @Update
    void atualizar(Projeto projeto);

    @Query("SELECT * FROM projetos ORDER BY dataFim ASC, nome ASC")
    Flow<List<Projeto>> listarTodosFlow();

    @Query("SELECT * FROM projetos ORDER BY dataFim ASC, nome ASC")
    List<Projeto> listarTodos();

    @Query("SELECT * FROM projetos WHERE idProjeto = :id LIMIT 1")
    Projeto buscarPorId(int id);

    @Query("SELECT COUNT(*) FROM projetos WHERE lower(trim(nome)) = lower(trim(:nome))")
    int contarPorNome(String nome);

    @Query("SELECT COUNT(*) FROM projetos WHERE lower(trim(nome)) = lower(trim(:nome)) AND idProjeto != :idProjeto")
    int contarPorNomeExceto(String nome, int idProjeto);

    @Query("SELECT COUNT(*) FROM projeto_membros WHERE idProjeto = :idProjeto AND idMembro = :idMembro")
    int contarVinculo(int idProjeto, int idMembro);

    @Query("DELETE FROM projeto_membros WHERE idProjeto = :idProjeto AND idMembro = :idMembro")
    void desvincularMembro(int idProjeto, int idMembro);

    @Query("DELETE FROM projeto_membros WHERE idMembro = :idMembro")
    void desvincularMembroDeTodosProjetos(int idMembro);

    @Query(
            "SELECT p.idProjeto AS idProjeto, p.nome AS nome, p.descricao AS descricao, " +
            "p.dataInicio AS dataInicio, p.dataFim AS dataFim, p.limiteTarefas AS limiteTarefas, " +
            "COUNT(t.idTarefa) AS totalTarefas, " +
            "COALESCE(SUM(CASE WHEN t.status != 'Concluida' THEN 1 ELSE 0 END), 0) AS tarefasAtivas, " +
            "COALESCE(SUM(CASE WHEN t.status = 'Concluida' THEN 1 ELSE 0 END), 0) AS tarefasConcluidas, " +
            "COALESCE(SUM(CASE WHEN t.status = 'Em_Andamento' THEN 1 ELSE 0 END), 0) AS tarefasEmAndamento, " +
            "COALESCE(SUM(CASE WHEN t.status != 'Concluida' AND t.prazo < :agora THEN 1 ELSE 0 END), 0) AS tarefasAtrasadas, " +
            "(SELECT COUNT(*) FROM projeto_membros pm WHERE pm.idProjeto = p.idProjeto) AS totalMembros " +
            "FROM projetos p " +
            "LEFT JOIN tarefas t ON t.idProjeto = p.idProjeto " +
            "GROUP BY p.idProjeto " +
            "ORDER BY p.dataFim ASC, p.nome ASC"
    )
    Flow<List<ProjetoResumo>> listarResumoFlow(long agora);

    @Query(
            "SELECT DISTINCT m.* FROM membros m " +
            "INNER JOIN projeto_membros pm ON pm.idMembro = m.idMembro " +
            "WHERE pm.idProjeto = :idProjeto " +
            "ORDER BY m.nome ASC"
    )
    Flow<List<Membro>> listarMembrosPorProjetoFlow(int idProjeto);

    @Query(
            "SELECT m.* FROM membros m " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 FROM projeto_membros pm " +
            "WHERE pm.idProjeto = :idProjeto AND pm.idMembro = m.idMembro" +
            ") " +
            "ORDER BY m.nome ASC"
    )
    Flow<List<Membro>> listarMembrosDisponiveisProjetoFlow(int idProjeto);
}
