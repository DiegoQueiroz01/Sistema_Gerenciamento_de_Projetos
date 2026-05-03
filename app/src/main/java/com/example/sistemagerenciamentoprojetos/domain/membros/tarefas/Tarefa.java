package com.example.sistemagerenciamentoprojetos.domain.membros.tarefas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tarefas")
public class Tarefa {

    @PrimaryKey(autoGenerate = true)
    private int idTarefa;

    private int idProjeto;
    private int idMembro;
    private String titulo;
    private String descricao;
    private String prioridade;
    private String status;
    private long prazo;
    private float tempoEstimado; //
    private float tempoEfetivo;  //

    // Construtor
    public Tarefa(int idProjeto, String titulo, String descricao,
                  String prioridade, long prazo, float tempoEstimado) {
        this.idProjeto = idProjeto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.prazo = prazo;
        this.tempoEstimado = tempoEstimado;
        // Valores padrão ao criar
        this.tempoEfetivo = 0f;
        this.status = "Nao_Iniciada";
        this.idMembro = 0;
    }

    // Getters e Setters
    public int getIdTarefa() { return idTarefa; }
    public void setIdTarefa(int idTarefa) { this.idTarefa = idTarefa; }

    public int getIdProjeto() { return idProjeto; }
    public void setIdProjeto(int idProjeto) { this.idProjeto = idProjeto; }

    public int getIdMembro() { return idMembro; }
    public void setIdMembro(int idMembro) { this.idMembro = idMembro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getPrazo() { return prazo; }
    public void setPrazo(long prazo) { this.prazo = prazo; }

    public float getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(float tempoEstimado) { this.tempoEstimado = tempoEstimado; }

    public float getTempoEfetivo() { return tempoEfetivo; }
    public void setTempoEfetivo(float tempoEfetivo) { this.tempoEfetivo = tempoEfetivo; }
}