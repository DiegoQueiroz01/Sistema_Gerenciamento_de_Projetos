package com.example.sistemagerenciamentoprojetos.domain.servicos;

import com.example.sistemagerenciamentoprojetos.domain.estruturas.ListaDinamicaUtils;
import com.example.sistemagerenciamentoprojetos.domain.projetos.ProjetoResumo;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import repository.dinamicas.lista.ListaDinamica;

/**
 * Manipula listas de projetos usando lista dinamica e fila de prioridade.
 *
 * <p>A lista dinamica armazena os projetos em memoria, enquanto a
 * {@link PriorityQueue} aplica a regra de prioridade definida para o trabalho:
 * projetos atrasados, no limite e mais proximos do prazo aparecem primeiro.</p>
 */
public class GerenciadorProjetos {

    public ListaDinamica listarProjetosPorPrioridade(List<ProjetoResumo> projetos) {
        ListaDinamica origem = ListaDinamicaUtils.deList(projetos);
        PriorityQueue<ProjetoResumo> fila = new PriorityQueue<>(
                origem.tamanho() > 0 ? origem.tamanho() : 1,
                Comparator
                        .comparingInt((ProjetoResumo p) -> getRiscoProjeto(p))
                        .thenComparingLong(p -> p.dataFim)
                        .thenComparing(p -> textoSeguro(p.nome))
        );

        for (int i = 0; i < origem.tamanho(); i++) {
            fila.add((ProjetoResumo) origem.selecionar(i));
        }

        ListaDinamica ordenados = ListaDinamicaUtils.criarLista(origem.tamanho());
        while (!fila.isEmpty()) {
            ordenados.anexar(fila.poll());
        }

        return ordenados;
    }

    public List<ProjetoResumo> ordenarProjetosPorPrioridade(List<ProjetoResumo> projetos) {
        return ListaDinamicaUtils.paraList(listarProjetosPorPrioridade(projetos));
    }

    public List<ProjetoResumo> filtrarProjetos(List<ProjetoResumo> projetos, String pesquisa, String situacao) {
        ListaDinamica ordenados = listarProjetosPorPrioridade(projetos);
        ListaDinamica filtrados = ListaDinamicaUtils.criarLista(ordenados.tamanho());
        String termo = normalizar(pesquisa);

        for (int i = 0; i < ordenados.tamanho(); i++) {
            ProjetoResumo projeto = (ProjetoResumo) ordenados.selecionar(i);
            if (passaBusca(projeto, termo) && passaSituacao(projeto, situacao)) {
                filtrados.anexar(projeto);
            }
        }

        return ListaDinamicaUtils.paraList(filtrados);
    }

    private boolean passaBusca(ProjetoResumo projeto, String termo) {
        return termo.isEmpty()
                || String.valueOf(projeto.idProjeto).contains(termo)
                || normalizar(projeto.nome).contains(termo)
                || normalizar(projeto.descricao).contains(termo);
    }

    private boolean passaSituacao(ProjetoResumo projeto, String situacao) {
        boolean concluido = projeto.totalTarefas > 0 && projeto.tarefasConcluidas == projeto.totalTarefas;
        boolean limiteCheio = projeto.limiteTarefas > 0 && projeto.tarefasAtivas >= projeto.limiteTarefas;

        if ("Atrasados".equals(situacao)) return projeto.tarefasAtrasadas > 0;
        if ("No limite".equals(situacao)) return limiteCheio;
        if ("Em andamento".equals(situacao)) return projeto.tarefasEmAndamento > 0;
        if ("Concluídos".equals(situacao)) return concluido;
        return true;
    }

    private int getRiscoProjeto(ProjetoResumo projeto) {
        if (projeto.tarefasAtrasadas > 0) return 1;
        if (projeto.limiteTarefas > 0 && projeto.tarefasAtivas >= projeto.limiteTarefas) return 2;
        if (projeto.limiteTarefas > 0 && projeto.tarefasAtivas >= projeto.limiteTarefas * 0.8f) return 3;
        return 4;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(textoSeguro(texto).trim(), Normalizer.Form.NFD);
        return semAcento.replaceAll("\\p{Mn}+", "").toLowerCase();
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }
}
