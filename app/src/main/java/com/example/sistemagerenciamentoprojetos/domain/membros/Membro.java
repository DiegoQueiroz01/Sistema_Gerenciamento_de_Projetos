package com.example.sistemagerenciamentoprojetos.domain.membros;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "membros")
public class Membro {
    @PrimaryKey(autoGenerate = true)
    private int idMembro;
    private String nome;
    private String cargo;
    private String email;
    private long dataCadastro;
    private int tarefasAtivas;
    private int tarefasConcluidas;

    public Membro(String nome, String cargo, String email) {
        this.nome = nome;
        this.cargo = cargo;
        this.email = email;
        this.dataCadastro = System.currentTimeMillis();
        this.tarefasAtivas = 0;
        this.tarefasConcluidas = 0;
    }

    public int getIdMembro() { return idMembro; }
    public void setIdMembro(int idMembro) { this.idMembro = idMembro; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(long dataCadastro) { this.dataCadastro = dataCadastro; }

    public int getTarefasAtivas() { return tarefasAtivas; }
    public void setTarefasAtivas(int tarefasAtivas) { this.tarefasAtivas = tarefasAtivas; }

    public int getTarefasConcluidas() { return tarefasConcluidas; }
    public void setTarefasConcluidas(int tarefasConcluidas) { this.tarefasConcluidas = tarefasConcluidas; }
}
