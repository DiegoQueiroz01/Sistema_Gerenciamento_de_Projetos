package com.example.sistemagerenciamentoprojetos.domain.servicos;

import com.example.sistemagerenciamentoprojetos.domain.estruturas.ListaDinamicaUtils;
import com.example.sistemagerenciamentoprojetos.domain.membros.Membro;
import com.example.sistemagerenciamentoprojetos.domain.tarefas.Tarefa;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import repository.dinamicas.lista.ListaDinamica;

/**
 * Centraliza as regras de listagem, filtragem e contagem de membros.
 *
 * <p>Os dados chegam do Room como {@link List}, mas sao processados por uma
 * {@link ListaDinamica} para deixar explicita a estrutura dinamica usada na
 * camada de regras de negocio.</p>
 */
public class GerenciadorMembros {

    private static final int LIMITE_TAREFAS_MEMBRO = 3;

    public List<Membro> filtrarMembros(List<Membro> membros, List<Tarefa> tarefas,
                                       String pesquisa, String cargo, String carga) {
        ListaDinamica origem = ListaDinamicaUtils.deList(membros);
        ListaDinamica filtrados = ListaDinamicaUtils.criarLista(origem.tamanho());
        String termo = normalizar(pesquisa);

        for (int i = 0; i < origem.tamanho(); i++) {
            Membro membro = (Membro) origem.selecionar(i);
            int tarefasAtivas = contarTarefasEmAndamentoDoMembro(tarefas, membro.getIdMembro());

            if (passaBusca(membro, termo)
                    && passaCargo(membro, cargo)
                    && passaCarga(tarefasAtivas, carga)) {
                filtrados.anexar(membro);
            }
        }

        List<Membro> retorno = ListaDinamicaUtils.paraList(filtrados);
        Collections.sort(retorno, (m1, m2) -> normalizar(m1.getNome()).compareTo(normalizar(m2.getNome())));
        return retorno;
    }

    public List<String> listarCargos(List<Membro> membros) {
        ListaDinamica origem = ListaDinamicaUtils.deList(membros);
        ListaDinamica cargos = ListaDinamicaUtils.criarLista(origem.tamanho() + 1);
        cargos.anexar("Todos");

        for (int i = 0; i < origem.tamanho(); i++) {
            Membro membro = (Membro) origem.selecionar(i);
            String cargo = textoSeguro(membro.getCargo()).trim();

            if (!cargo.isEmpty() && !contemCargo(cargos, cargo)) {
                cargos.anexar(cargo);
            }
        }

        List<String> retorno = ListaDinamicaUtils.paraList(cargos);
        if (retorno.size() > 1) {
            List<String> ordenados = new ArrayList<>(retorno.subList(1, retorno.size()));
            Collections.sort(ordenados, (c1, c2) -> normalizar(c1).compareTo(normalizar(c2)));
            retorno = new ArrayList<>();
            retorno.add("Todos");
            retorno.addAll(ordenados);
        }
        return retorno;
    }

    public int contarTarefasEmAndamentoDoMembro(List<Tarefa> tarefas, int idMembro) {
        ListaDinamica origem = ListaDinamicaUtils.deList(tarefas);
        int total = 0;

        for (int i = 0; i < origem.tamanho(); i++) {
            Tarefa tarefa = (Tarefa) origem.selecionar(i);
            if (tarefa.getIdMembro() == idMembro && "Em_Andamento".equals(tarefa.getStatus())) {
                total++;
            }
        }

        return total;
    }

    private boolean passaBusca(Membro membro, String termo) {
        return termo.isEmpty()
                || String.valueOf(membro.getIdMembro()).contains(termo)
                || normalizar(membro.getNome()).contains(termo)
                || normalizar(membro.getCargo()).contains(termo)
                || normalizar(membro.getEmail()).contains(termo);
    }

    private boolean passaCargo(Membro membro, String cargo) {
        return "Todos".equals(cargo) || textoSeguro(membro.getCargo()).equals(cargo);
    }

    private boolean passaCarga(int tarefasAtivas, String carga) {
        if ("Disponíveis".equals(carga)) return tarefasAtivas < LIMITE_TAREFAS_MEMBRO;
        if ("Com tarefas".equals(carga)) return tarefasAtivas > 0;
        if ("No limite".equals(carga)) return tarefasAtivas >= LIMITE_TAREFAS_MEMBRO;
        return true;
    }

    private boolean contemCargo(ListaDinamica cargos, String cargo) {
        for (int i = 0; i < cargos.tamanho(); i++) {
            if (cargo.equals(cargos.selecionar(i))) {
                return true;
            }
        }
        return false;
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(textoSeguro(texto).trim(), Normalizer.Form.NFD);
        return semAcento.replaceAll("\\p{Mn}+", "").toLowerCase();
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }
}
