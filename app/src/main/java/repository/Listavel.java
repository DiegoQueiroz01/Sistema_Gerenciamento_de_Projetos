package repository;

public interface Listavel {
    void inserir(Object objeto, int posicao);
    void anexar(Object objeto);
    Object selecionar(int posicao);
    Object[] selecionarTodos();
    void atualizar(Object objeto, int posicao);
    Object apagar(int posicao);
    void limpar();
    int tamanho();
    boolean estaVazia();
    boolean estaCheia();
    String imprimir();
}
