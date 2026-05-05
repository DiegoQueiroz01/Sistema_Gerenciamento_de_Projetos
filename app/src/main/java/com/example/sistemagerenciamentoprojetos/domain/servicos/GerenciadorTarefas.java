package com.example.sistemagerenciamentoprojetos.domain.servicos;

import com.example.sistemagerenciamentoprojetos.domain.estruturas.ListaDinamicaUtils;
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro;
import com.example.sistemagerenciamentoprojetos.domain.projetos.Projeto;
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa;

import java.text.Normalizer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import repository.dinamicas.lista.ListaDinamica;

/**
 * Concentra as operacoes de listagem, filtragem, contagem e ordenacao de tarefas.
 *
 * <p>Esta classe deixa a aplicacao da lista dinamica visivel na regra de negocio:
 * todas as colecoes recebidas da persistencia sao transformadas em
 * {@link ListaDinamica} antes de serem percorridas.</p>
 */
public class GerenciadorTarefas {

    public List<Tarefa> filtrarTarefasPorStatus(List<Tarefa> tarefas, String status) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        ListaDinamica filtradas = ListaDinamicaUtils.criarLista(origem.tamanho());

        for (int i = 0; i < origem.tamanho(); i++) {
            Tarefa tarefa = (Tarefa) origem.selecionar(i);
            if (status == null || status.isEmpty() || status.equals(tarefa.getStatus())) {
                filtradas.anexar(tarefa);
            }
        }

        return ListaDinamicaUtils.paraList(filtradas);
    }

    public List<Tarefa> listarTarefasPendentes(List<Tarefa> tarefas) {
        return filtrarPorCriterio(tarefas, tarefa -> !"Concluida".equals(tarefa.getStatus()));
    }

    public List<Tarefa> listarTarefasAtrasadas(List<Tarefa> tarefas, long agora) {
        return filtrarPorCriterio(tarefas, tarefa ->
                !"Concluida".equals(tarefa.getStatus()) && tarefa.getPrazo() < agora);
    }

    public List<Tarefa> filtrarTarefasPorProjeto(List<Tarefa> tarefas, int idProjeto) {
        return filtrarPorCriterio(tarefas, tarefa -> idProjeto == 0 || tarefa.getIdProjeto() == idProjeto);
    }

    public List<Tarefa> filtrarTarefasPorMembro(List<Tarefa> tarefas, int idMembro) {
        return filtrarPorCriterio(tarefas, tarefa -> idMembro == 0 || tarefa.getIdMembro() == idMembro);
    }

    public List<Tarefa> filtrarTarefasPorPrioridade(List<Tarefa> tarefas, String prioridade) {
        return filtrarPorCriterio(tarefas, tarefa ->
                "Todas".equals(prioridade) || prioridade.equals(tarefa.getPrioridade()));
    }

    public int contarTarefasPorStatus(List<Tarefa> tarefas, String status) {
        return contarPorCriterio(tarefas, tarefa -> status.equals(tarefa.getStatus()));
    }

    public int contarTarefasAtivas(List<Tarefa> tarefas) {
        return contarPorCriterio(tarefas, tarefa -> !"Concluida".equals(tarefa.getStatus()));
    }

    public int contarTarefasAtrasadas(List<Tarefa> tarefas, long agora) {
        return contarPorCriterio(tarefas, tarefa ->
                !"Concluida".equals(tarefa.getStatus()) && tarefa.getPrazo() < agora);
    }

    public int contarTarefasConcluidasNoPrazo(List<Tarefa> tarefas, long agora) {
        return contarPorCriterio(tarefas, tarefa ->
                "Concluida".equals(tarefa.getStatus()) && tarefa.getPrazo() >= agora);
    }

    public int contarTarefasDoMembroPorStatus(List<Tarefa> tarefas, int idMembro, String status) {
        return contarPorCriterio(tarefas, tarefa ->
                tarefa.getIdMembro() == idMembro && status.equals(tarefa.getStatus()));
    }

    public int contarTarefasEmAndamentoDoMembro(List<Tarefa> tarefas, int idMembro) {
        return contarTarefasDoMembroPorStatus(tarefas, idMembro, "Em_Andamento");
    }

    public List<Tarefa> filtrarTarefasGestao(List<Tarefa> tarefas, List<Projeto> projetos, List<Membro> membros,
                                             String pesquisa, String prioridade, String status,
                                             int idProjeto, int idMembro, long agora) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        ListaDinamica filtradas = ListaDinamicaUtils.criarLista(origem.tamanho());
        String termo = normalizar(pesquisa);

        for (int i = 0; i < origem.tamanho(); i++) {
            Tarefa tarefa = (Tarefa) origem.selecionar(i);
            if (passaBusca(tarefa, projetos, membros, termo)
                    && passaPrioridade(tarefa, prioridade)
                    && passaStatus(tarefa, status, agora)
                    && (idProjeto == 0 || tarefa.getIdProjeto() == idProjeto)
                    && (idMembro == 0 || tarefa.getIdMembro() == idMembro)) {
                filtradas.anexar(tarefa);
            }
        }

        return ListaDinamicaUtils.paraList(filtradas);
    }

    public List<Tarefa> ordenarTarefas(List<Tarefa> tarefas) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        PriorityQueue<Tarefa> fila = new PriorityQueue<>(
                origem.tamanho() > 0 ? origem.tamanho() : 1,
                Comparator
                        .comparingInt((Tarefa t) -> getPrioridadeValor(t.getPrioridade()))
                        .thenComparingLong(Tarefa::getPrazo)
        );

        for (int i = 0; i < origem.tamanho(); i++) {
            fila.add((Tarefa) origem.selecionar(i));
        }

        ListaDinamica ordenadas = ListaDinamicaUtils.criarLista(origem.tamanho());
        while (!fila.isEmpty()) {
            ordenadas.anexar(fila.poll());
        }

        return ListaDinamicaUtils.paraList(ordenadas);
    }

    public List<Tarefa> ordenarTarefasParaGestao(List<Tarefa> tarefas, List<Projeto> projetos,
                                                 List<Membro> membros, String ordenacao) {
        if ("Prioridade".equals(ordenacao)) {
            return ordenarTarefas(tarefas);
        }

        List<Tarefa> retorno = ListaDinamicaUtils.paraList(ListaDinamicaUtils.deList(tarefas));
        if ("Projeto".equals(ordenacao)) {
            Collections.sort(retorno, Comparator
                    .comparing((Tarefa tarefa) -> nomeProjeto(projetos, tarefa.getIdProjeto()))
                    .thenComparingLong(Tarefa::getPrazo));
        } else if ("Responsável".equals(ordenacao)) {
            Collections.sort(retorno, Comparator
                    .comparing((Tarefa tarefa) -> nomeMembro(membros, tarefa.getIdMembro()))
                    .thenComparingLong(Tarefa::getPrazo));
        } else {
            Collections.sort(retorno, Comparator.comparingLong(Tarefa::getPrazo));
        }
        return retorno;
    }

    private List<Tarefa> filtrarPorCriterio(List<Tarefa> tarefas, CriterioTarefa criterio) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        ListaDinamica filtradas = ListaDinamicaUtils.criarLista(origem.tamanho());

        for (int i = 0; i < origem.tamanho(); i++) {
            Tarefa tarefa = (Tarefa) origem.selecionar(i);
            if (criterio.aceita(tarefa)) {
                filtradas.anexar(tarefa);
            }
        }

        return ListaDinamicaUtils.paraList(filtradas);
    }

    private int contarPorCriterio(List<Tarefa> tarefas, CriterioTarefa criterio) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        int total = 0;

        for (int i = 0; i < origem.tamanho(); i++) {
            if (criterio.aceita((Tarefa) origem.selecionar(i))) {
                total++;
            }
        }

        return total;
    }

    private boolean passaBusca(Tarefa tarefa, List<Projeto> projetos, List<Membro> membros, String termo) {
        return termo.isEmpty()
                || String.valueOf(tarefa.getIdTarefa()).contains(termo)
                || normalizar(tarefa.getTitulo()).contains(termo)
                || normalizar(tarefa.getDescricao()).contains(termo)
                || normalizar(nomeProjeto(projetos, tarefa.getIdProjeto())).contains(termo)
                || normalizar(nomeMembro(membros, tarefa.getIdMembro())).contains(termo);
    }

    private boolean passaPrioridade(Tarefa tarefa, String prioridade) {
        return "Todas".equals(prioridade) || prioridade.equals(tarefa.getPrioridade());
    }

    private boolean passaStatus(Tarefa tarefa, String status, long agora) {
        if ("Não iniciada".equals(status)) return "Nao_Iniciada".equals(tarefa.getStatus());
        if ("Em andamento".equals(status)) return "Em_Andamento".equals(tarefa.getStatus());
        if ("Concluída".equals(status)) return "Concluida".equals(tarefa.getStatus());
        if ("Atrasadas".equals(status)) return !"Concluida".equals(tarefa.getStatus()) && tarefa.getPrazo() < agora;
        return true;
    }

    private String nomeProjeto(List<Projeto> projetos, int idProjeto) {
        ListaDinamica origem = ListaDinamicaUtils.deList(projetos);
        for (int i = 0; i < origem.tamanho(); i++) {
            Projeto projeto = (Projeto) origem.selecionar(i);
            if (projeto.getIdProjeto() == idProjeto) {
                return textoSeguro(projeto.getNome());
            }
        }
        return "Projeto " + idProjeto;
    }

    private String nomeMembro(List<Membro> membros, int idMembro) {
        ListaDinamica origem = ListaDinamicaUtils.deList(membros);
        for (int i = 0; i < origem.tamanho(); i++) {
            Membro membro = (Membro) origem.selecionar(i);
            if (membro.getIdMembro() == idMembro) {
                return textoSeguro(membro.getNome());
            }
        }
        return "";
    }

    private int getPrioridadeValor(String prioridade) {
        if ("Alta".equals(prioridade)) return 1;
        if ("Media".equals(prioridade)) return 2;
        if ("Baixa".equals(prioridade)) return 3;
        return 4;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(textoSeguro(texto).trim(), Normalizer.Form.NFD);
        return semAcento.replaceAll("\\p{Mn}+", "").toLowerCase();
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private interface CriterioTarefa {
        boolean aceita(Tarefa tarefa);
    }
}
