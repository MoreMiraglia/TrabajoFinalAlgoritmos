class Celda<T> {
    private T valor;
    private String nombreColumna;
    private int indice;

    public Celda(T valor,String nombreColumna,int indice) {
        this.valor = valor;
        this.nombreColumna = nombreColumna;
        this.indice= indice;
    }

    public T getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor == null ? "null" : valor.toString();
    }
}