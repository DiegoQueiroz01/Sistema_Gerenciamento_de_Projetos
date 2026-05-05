package com.example.sistemagerenciamentoprojetos.domain.estruturas;

import java.util.ArrayList;
import java.util.List;

import repository.dinamicas.lista.ListaDinamica;

/**
 * Funcoes de apoio para converter colecoes do Android/Room para ListaDinamica.
 *
 * <p>O Room e o Jetpack Compose trabalham naturalmente com {@link List}.
 * Para evidenciar a aplicacao de Estrutura de Dados, as regras de negocio
 * convertem essas colecoes para {@link ListaDinamica} antes de filtrar,
 * contar, selecionar ou ordenar os dados em memoria.</p>
 */
public final class ListaDinamicaUtils {

    private ListaDinamicaUtils() {
    }

    /**
     * Cria uma lista dinamica com capacidade minima igual a 1.
     */
    public static ListaDinamica criarLista(int capacidade) {
        return new ListaDinamica(Math.max(capacidade, 1));
    }

    /**
     * Copia os itens de uma List comum para uma ListaDinamica.
     */
    public static ListaDinamica deList(List<?> itens) {
        ListaDinamica lista = criarLista(itens == null ? 1 : itens.size());

        if (itens != null) {
            for (Object item : itens) {
                lista.anexar(item);
            }
        }

        return lista;
    }

    /**
     * Copia os itens da ListaDinamica para uma List comum usada pela UI.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> paraList(ListaDinamica lista) {
        List<T> retorno = new ArrayList<>();

        for (int i = 0; i < lista.tamanho(); i++) {
            retorno.add((T) lista.selecionar(i));
        }

        return retorno;
    }
}
