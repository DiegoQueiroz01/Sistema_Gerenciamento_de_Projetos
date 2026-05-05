package repository;

/**
 * Contrato abstrato de lista usado na disciplina de Estrutura de Dados.
 *
 * <p>Define as operacoes CRUD basicas de uma lista: inserir/anexar,
 * selecionar, atualizar e apagar elementos. A implementacao concreta
 * utilizada no aplicativo e a lista dinamica duplamente encadeada.</p>
 */
public interface Listavel {
    /** Insere um objeto em uma posicao especifica da lista. */
    void inserir(Object objeto, int posicao);

    /** Adiciona um objeto ao final da lista. */
    void anexar(Object objeto);

    /** Retorna o objeto armazenado em determinada posicao. */
    Object selecionar(int posicao);

    /** Retorna todos os elementos da lista em um array. */
    Object[] selecionarTodos();

    /** Substitui o objeto armazenado em determinada posicao. */
    void atualizar(Object objeto, int posicao);

    /** Remove e retorna o objeto armazenado em determinada posicao. */
    Object apagar(int posicao);

    /** Remove todos os elementos da lista. */
    void limpar();

    /** Retorna a quantidade atual de elementos. */
    int tamanho();

    /** Indica se a lista esta vazia. */
    boolean estaVazia();

    /** Indica se a lista atingiu sua capacidade maxima. */
    boolean estaCheia();

    /** Retorna uma representacao textual dos elementos. */
    String imprimir();
}
