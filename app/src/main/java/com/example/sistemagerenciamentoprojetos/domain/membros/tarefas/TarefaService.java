package com.example.sistemagerenciamentoprojetos.domain.membros.tarefas;

import android.content.Context;

import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TarefaService {

    private final TarefaDao tarefaDao;
    private static final int LIMITE_TAREFAS_MEMBRO = 3;

    public TarefaService(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.tarefaDao = db.tarefaDao();
    }

    public String cadastrarTarefa(int idProjeto, int limiteTarefasProjeto,
                                  String titulo, String descricao,
                                  String prioridade, long prazo,
                                  float tempoEstimado) {

        if (titulo == null || titulo.trim().isEmpty()) {
            return "Erro: O título da tarefa é obrigatório.";
        }

        int ativasProjeto = tarefaDao.contarTarefasAtivasProjeto(idProjeto);
        if (ativasProjeto >= limiteTarefasProjeto) {
            return "Erro: Projeto atingiu o limite de "
                    + limiteTarefasProjeto + " tarefas ativas.";
        }

        Tarefa nova = new Tarefa(
                idProjeto, titulo.trim(), descricao,
                prioridade, prazo, tempoEstimado
        );
        tarefaDao.inserir(nova);
        return "Tarefa cadastrada com sucesso!";
    }


    public String atribuirMembro(int idTarefa, int idMembro) {

        int ativasMembro = tarefaDao.contarTarefasAtivasMembro(idMembro);

        if (ativasMembro >= LIMITE_TAREFAS_MEMBRO) {
            return "Erro: Membro já possui "
                    + LIMITE_TAREFAS_MEMBRO + " tarefas em andamento.";
        }

        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        tarefa.setIdMembro(idMembro);
        tarefaDao.atualizar(tarefa);
        return "Responsável atribuído com sucesso!";
    }


    public String atualizarStatus(int idTarefa, String novoStatus) {
        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        tarefa.setStatus(novoStatus);
        tarefaDao.atualizar(tarefa);
        return "Status atualizado para: " + novoStatus;
    }


    public List<Tarefa> ordenarTarefas(List<Tarefa> tarefas) {

        PriorityQueue<Tarefa> fila = new PriorityQueue<>(
                tarefas.size() > 0 ? tarefas.size() : 1,
                Comparator
                        .comparingInt((Tarefa t) -> getPrioridadeValor(t.getPrioridade()))
                        .thenComparingLong(Tarefa::getPrazo)
        );

        fila.addAll(tarefas);

        List<Tarefa> ordenadas = new ArrayList<>();
        while (!fila.isEmpty()) {
            ordenadas.add(fila.poll());
        }

        return ordenadas;
    }

    private int getPrioridadeValor(String prioridade) {
        switch (prioridade) {
            case "Alta":  return 1;
            case "Media": return 2;
            case "Baixa": return 3;
            default:      return 4;
        }
    }
}