package com.example.sistemagerenciamentoprojetos.domain.tarefas;

import android.content.Context;

import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase;
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto;
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoDao;
import com.example.sistemagerenciamentoprojetos.domain.servicos.GerenciadorTarefas;

import java.util.List;

public class TarefaService {

    private final TarefaDao tarefaDao;
    private final ProjetoDao projetoDao;
    private final GerenciadorTarefas gerenciadorTarefas;
    private static final int LIMITE_TAREFAS_MEMBRO = 3;

    public TarefaService(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.tarefaDao = db.tarefaDao();
        this.projetoDao = db.projetoDao();
        this.gerenciadorTarefas = new GerenciadorTarefas();
    }
    public String cadastrarTarefa(int idProjeto, int limiteTarefasProjeto,
                                  String titulo, String descricao,
                                  String prioridade, long prazo,
                                  float tempoEstimado, int idMembro) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return "Erro: O título da tarefa é obrigatório.";
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return "Erro: A descrição da tarefa é obrigatória.";
        }

        if (prazo <= 0) {
            return "Erro: O prazo da tarefa é obrigatório.";
        }

        if (tempoEstimado <= 0) {
            return "Erro: O tempo estimado deve ser maior que zero.";
        }

        Projeto projeto = projetoDao.buscarPorId(idProjeto);
        if (projeto == null) {
            return "Erro: Projeto não encontrado.";
        }

        int limiteRealProjeto = projeto.getLimiteTarefas();
        int ativasProjeto = tarefaDao.contarTarefasAtivasProjeto(idProjeto);
        if (ativasProjeto >= limiteRealProjeto) {
            return "Erro: Projeto atingiu o limite de " + limiteRealProjeto + " tarefas ativas.";
        }

        if (idMembro != 0) {
            if (projetoDao.contarVinculo(idProjeto, idMembro) == 0) {
                return "Erro: Membro não está vinculado a este projeto.";
            }

            int ativasMembro = tarefaDao.contarTarefasAtivasMembro(idMembro);
            if (ativasMembro >= LIMITE_TAREFAS_MEMBRO) {
                return "Erro: Membro já possui " + LIMITE_TAREFAS_MEMBRO + " tarefas em andamento.";
            }
        }

        Tarefa nova = new Tarefa(idProjeto, titulo.trim(), descricao.trim(), prioridade, prazo, tempoEstimado);
        nova.setIdMembro(idMembro);
        long id = tarefaDao.inserirRetornandoId(nova); // ← retorna o idTarefa gerado
        return "sucesso:" + id;
    }

    public void deletarTarefa(int idTarefa) {
        tarefaDao.deletar(idTarefa);
    }


    public String atribuirMembro(int idTarefa, int idMembro) {
        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        if (projetoDao.contarVinculo(tarefa.getIdProjeto(), idMembro) == 0) {
            return "Erro: Membro não está vinculado a este projeto.";
        }

        int ativasMembro = tarefaDao.contarTarefasAtivasMembroExceto(idMembro, idTarefa);
        if (tarefa.getStatus().equals("Em_Andamento") && ativasMembro >= LIMITE_TAREFAS_MEMBRO) {
            return "Erro: Membro já possui "
                    + LIMITE_TAREFAS_MEMBRO + " tarefas em andamento.";
        }

        tarefa.setIdMembro(idMembro);
        tarefaDao.atualizar(tarefa);
        return "Responsável atribuído com sucesso!";
    }


    public String atualizarStatus(int idTarefa, String novoStatus) {
        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        if (novoStatus == null || novoStatus.trim().isEmpty()) {
            return "Erro: Status inválido.";
        }

        if (novoStatus.equals("Em_Andamento")) {
            if (tarefa.getIdMembro() == 0) {
                return "Erro: A tarefa precisa de um responsável para ser iniciada.";
            }

            int ativasMembro = tarefaDao.contarTarefasAtivasMembroExceto(tarefa.getIdMembro(), idTarefa);
            if (ativasMembro >= LIMITE_TAREFAS_MEMBRO) {
                return "Erro: Membro já possui " + LIMITE_TAREFAS_MEMBRO + " tarefas em andamento.";
            }
        }

        tarefa.setStatus(novoStatus);
        tarefaDao.atualizar(tarefa);
        return "Status atualizado para: " + novoStatus;
    }

    public String registrarTempoEfetivo(int idTarefa, float tempoEfetivo) {
        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        if (tempoEfetivo < 0) {
            return "Erro: O tempo efetivo não pode ser negativo.";
        }

        tarefa.setTempoEfetivo(tempoEfetivo);
        tarefaDao.atualizar(tarefa);
        return "Tempo efetivo atualizado com sucesso!";
    }

    public String atualizarTarefa(int idTarefa, int idProjeto, String titulo, String descricao,
                                  String prioridade, long prazo, float tempoEstimado, int idMembro) {
        Tarefa tarefa = tarefaDao.buscarPorId(idTarefa);
        if (tarefa == null) return "Erro: Tarefa não encontrada.";

        if (titulo == null || titulo.trim().isEmpty()) {
            return "Erro: O título da tarefa é obrigatório.";
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return "Erro: A descrição da tarefa é obrigatória.";
        }

        if (prazo <= 0) {
            return "Erro: O prazo da tarefa é obrigatório.";
        }

        if (tempoEstimado <= 0) {
            return "Erro: O tempo estimado deve ser maior que zero.";
        }

        Projeto projeto = projetoDao.buscarPorId(idProjeto);
        if (projeto == null) {
            return "Erro: Projeto não encontrado.";
        }

        int ativasProjeto = tarefaDao.contarTarefasAtivasProjetoExceto(idProjeto, idTarefa);
        if (!tarefa.getStatus().equals("Concluida") && ativasProjeto >= projeto.getLimiteTarefas()) {
            return "Erro: Projeto atingiu o limite de " + projeto.getLimiteTarefas() + " tarefas ativas.";
        }

        if (idMembro != 0) {
            if (projetoDao.contarVinculo(idProjeto, idMembro) == 0) {
                return "Erro: Membro não está vinculado a este projeto.";
            }

            int ativasMembro = tarefaDao.contarTarefasAtivasMembroExceto(idMembro, idTarefa);
            if (tarefa.getStatus().equals("Em_Andamento") && ativasMembro >= LIMITE_TAREFAS_MEMBRO) {
                return "Erro: Membro já possui " + LIMITE_TAREFAS_MEMBRO + " tarefas em andamento.";
            }
        }

        tarefa.setIdProjeto(idProjeto);
        tarefa.setTitulo(titulo.trim());
        tarefa.setDescricao(descricao.trim());
        tarefa.setPrioridade(prioridade);
        tarefa.setPrazo(prazo);
        tarefa.setTempoEstimado(tempoEstimado);
        tarefa.setIdMembro(idMembro);
        tarefaDao.atualizar(tarefa);

        return "Tarefa atualizada com sucesso!";
    }


    public List<Tarefa> ordenarTarefas(List<Tarefa> tarefas) {
        return gerenciadorTarefas.ordenarTarefas(tarefas);
    }
}
