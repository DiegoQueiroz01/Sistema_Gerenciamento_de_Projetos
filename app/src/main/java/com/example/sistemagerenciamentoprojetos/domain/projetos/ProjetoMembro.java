package com.example.sistemagerenciamentoprojetos.domain.projetos;

import androidx.room.Entity;

@Entity(tableName = "projeto_membros", primaryKeys = {"idProjeto", "idMembro"})
public class ProjetoMembro {
    private int idProjeto;
    private int idMembro;
    private long dataVinculo;

    public ProjetoMembro(int idProjeto, int idMembro) {
        this.idProjeto = idProjeto;
        this.idMembro = idMembro;
        this.dataVinculo = System.currentTimeMillis();
    }

    public int getIdProjeto() { return idProjeto; }
    public void setIdProjeto(int idProjeto) { this.idProjeto = idProjeto; }

    public int getIdMembro() { return idMembro; }
    public void setIdMembro(int idMembro) { this.idMembro = idMembro; }

    public long getDataVinculo() { return dataVinculo; }
    public void setDataVinculo(long dataVinculo) { this.dataVinculo = dataVinculo; }
}
