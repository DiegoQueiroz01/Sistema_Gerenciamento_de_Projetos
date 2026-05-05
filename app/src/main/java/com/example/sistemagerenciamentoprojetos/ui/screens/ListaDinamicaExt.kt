package com.example.sistemagerenciamentoprojetos.ui.screens

import repository.dinamicas.lista.ListaDinamica

fun <T> List<T>.paraListaDinamica(): ListaDinamica {
    val lista = ListaDinamica(size.coerceAtLeast(1))

    for (item in this) {
        lista.anexar(item)
    }

    return lista
}

@Suppress("UNCHECKED_CAST")
fun <T> List<T>.filtrarComListaDinamica(criterio: (T) -> Boolean): List<T> {
    val origem = paraListaDinamica()
    val filtrada = ListaDinamica(size.coerceAtLeast(1))

    for (i in 0 until origem.tamanho()) {
        val item = origem.selecionar(i) as T
        if (criterio(item)) {
            filtrada.anexar(item)
        }
    }

    return filtrada.paraListaKotlin()
}

@Suppress("UNCHECKED_CAST")
fun <T> List<T>.contarComListaDinamica(criterio: (T) -> Boolean): Int {
    val origem = paraListaDinamica()
    var total = 0

    for (i in 0 until origem.tamanho()) {
        val item = origem.selecionar(i) as T
        if (criterio(item)) {
            total++
        }
    }

    return total
}

@Suppress("UNCHECKED_CAST")
private fun <T> ListaDinamica.paraListaKotlin(): List<T> {
    val lista = mutableListOf<T>()

    for (i in 0 until tamanho()) {
        lista.add(selecionar(i) as T)
    }

    return lista
}
