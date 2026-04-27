package com.example.sistemagerenciamentoprojetos.domain.membros;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface MembroDao {
    @Insert
    void inserir(Membro membro);

    @Update
    void atualizar(Membro membro);

    @Query("SELECT * FROM membros WHERE email = :email")
    Membro buscarPorEmail(String email);

    @Query("SELECT * FROM membros")
    List<Membro> listarTodos();

    @Query("SELECT * FROM membros")
    Flow<List<Membro>> listarTodosFlow();

    @Query("SELECT * FROM membros WHERE idMembro = :id")
    Membro buscarPorId(int id);
}
