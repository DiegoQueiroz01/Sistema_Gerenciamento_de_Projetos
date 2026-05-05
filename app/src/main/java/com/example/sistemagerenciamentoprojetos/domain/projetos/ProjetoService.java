package com.example.sistemagerenciamentoprojetos.domain.projetos;

import android.content.Context;

import com.example.sistemagerenciamentoprojetos.domain.membros.AppDatabase;
import com.example.sistemagerenciamentoprojetos.domain.servicos.GerenciadorProjetos;
import com.example.sistemagerenciamentoprojetos.domain.tarefas.TarefaDao;

import java.util.List;

public class ProjetoService {

    private final ProjetoDao projetoDao;
    private final TarefaDao tarefaDao;
    private final GerenciadorProjetos gerenciadorProjetos;

    public ProjetoService(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.projetoDao = db.projetoDao();
        this.tarefaDao = db.tarefaDao();
        this.gerenciadorProjetos = new GerenciadorProjetos();
    }

    public String cadastrarProjeto(String nome, String descricao, long dataInicio,
                                   long dataFim, int limiteTarefas) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Erro: O nome do projeto é obrigatório.";
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return "Erro: A descrição do projeto é obrigatória.";
        }

        if (dataInicio <= 0 || dataFim <= 0) {
            return "Erro: Informe a data de início e o prazo final.";
        }

        if (dataFim < dataInicio) {
            return "Erro: O prazo final não pode ser anterior à data de início.";
        }

        if (limiteTarefas <= 0) {
            return "Erro: O limite de tarefas deve ser maior que zero.";
        }

        if (projetoDao.contarPorNome(nome) > 0) {
            return "Erro: Já existe um projeto cadastrado com este nome.";
        }

        Projeto projeto = new Projeto(
                nome.trim(),
                descricao.trim(),
                dataInicio,
                dataFim,
                limiteTarefas
        );
        long id = projetoDao.inserirRetornandoId(projeto);

        return "sucesso:" + id;
    }

    public Projeto buscarPorId(int idProjeto) {
        return projetoDao.buscarPorId(idProjeto);
    }

    public String atualizarProjeto(int idProjeto, String nome, String descricao, long dataInicio,
                                   long dataFim, int limiteTarefas) {
        Projeto projeto = projetoDao.buscarPorId(idProjeto);
        if (projeto == null) return "Erro: Projeto não encontrado.";

        if (nome == null || nome.trim().isEmpty()) {
            return "Erro: O nome do projeto é obrigatório.";
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            return "Erro: A descrição do projeto é obrigatória.";
        }

        if (dataInicio <= 0 || dataFim <= 0) {
            return "Erro: Informe a data de início e o prazo final.";
        }

        if (dataFim < dataInicio) {
            return "Erro: O prazo final não pode ser anterior à data de início.";
        }

        if (limiteTarefas <= 0) {
            return "Erro: O limite de tarefas deve ser maior que zero.";
        }

        int tarefasAtivas = tarefaDao.contarTarefasAtivasProjeto(idProjeto);
        if (limiteTarefas < tarefasAtivas) {
            return "Erro: O limite não pode ser menor que as " + tarefasAtivas + " tarefas ativas atuais.";
        }

        if (projetoDao.contarPorNomeExceto(nome, idProjeto) > 0) {
            return "Erro: Já existe um projeto cadastrado com este nome.";
        }

        projeto.setNome(nome.trim());
        projeto.setDescricao(descricao.trim());
        projeto.setDataInicio(dataInicio);
        projeto.setDataFim(dataFim);
        projeto.setLimiteTarefas(limiteTarefas);
        projetoDao.atualizar(projeto);

        return "Projeto atualizado com sucesso!";
    }

    public String vincularMembro(int idProjeto, int idMembro) {
        if (projetoDao.buscarPorId(idProjeto) == null) return "Erro: Projeto não encontrado.";
        if (idMembro <= 0) return "Erro: Membro inválido.";
        if (projetoDao.contarVinculo(idProjeto, idMembro) > 0) {
            return "Membro já está vinculado ao projeto.";
        }

        projetoDao.vincularMembro(new ProjetoMembro(idProjeto, idMembro));
        return "Membro vinculado ao projeto com sucesso!";
    }

    public String desvincularMembro(int idProjeto, int idMembro) {
        tarefaDao.removerResponsavelDoProjeto(idProjeto, idMembro);
        projetoDao.desvincularMembro(idProjeto, idMembro);
        return "Membro removido do projeto.";
    }

    public List<ProjetoResumo> ordenarProjetosPorPrioridade(List<ProjetoResumo> projetos) {
        return gerenciadorProjetos.ordenarProjetosPorPrioridade(projetos);
    }
}
