package com.example.sistemagerenciamentoprojetos.domain.projetos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projetos")
public class Projeto {

    @PrimaryKey(autoGenerate = true)
    private int idProjeto;

    private String nome;
    private String descricao;
    private long dataInicio;
    private long dataFim;
    private int limiteTarefas;
    private long dataCadastro;

    public Projeto(String nome, String descricao, long dataInicio, long dataFim, int limiteTarefas) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.limiteTarefas = limiteTarefas;
        this.dataCadastro = System.currentTimeMillis();
    }

    public int getIdProjeto() { return idProjeto; }
    public void setIdProjeto(int idProjeto) { this.idProjeto = idProjeto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public long getDataInicio() { return dataInicio; }
    public void setDataInicio(long dataInicio) { this.dataInicio = dataInicio; }

    public long getDataFim() { return dataFim; }
    public void setDataFim(long dataFim) { this.dataFim = dataFim; }

    public int getLimiteTarefas() { return limiteTarefas; }
    public void setLimiteTarefas(int limiteTarefas) { this.limiteTarefas = limiteTarefas; }

    public long getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(long dataCadastro) { this.dataCadastro = dataCadastro; }
}
