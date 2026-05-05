package repository.dinamicas;

/**
 * No de uma lista duplamente encadeada.
 *
 * <p>Cada no guarda o dado e tambem referencias para o no anterior
 * e para o proximo no, permitindo percorrer a estrutura nos dois sentidos.</p>
 */
public class NoDuplo {
    private NoDuplo anterior;
    private Object dado;
    private NoDuplo proximo;

    public NoDuplo(Object dado) {
        this.dado = dado;
    }

    public NoDuplo getAnterior() {
        return anterior;
    }

    public Object getDado() {
        return dado;
    }

    public NoDuplo getProximo() {
        return proximo;
    }

    public void setAnterior(NoDuplo anterior) {
        this.anterior = anterior;
    }

    public void setDado(Object dado) {
        this.dado = dado;
    }

    public void setProximo(NoDuplo proximo) {
        this.proximo = proximo;
    }
}
