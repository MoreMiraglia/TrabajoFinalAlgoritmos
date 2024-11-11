/**
 * Representa una celda en una tabla, que almacena un valor, el nombre de la columna a la que pertenece
 * y su índice dentro de dicha columna.
 *
 * @param <T> El tipo de dato que almacena la celda.
 */
class Celda<T> {
    private T valor;
    private String nombreColumna;
    private int indice;

    /**
     * Constructor de la clase Celda.
     *
     * @param valor El valor de la celda.
     * @param nombreColumna El nombre de la columna a la que pertenece la celda.
     * @param indice El índice de la celda en la columna.
     */
    public Celda(T valor, String nombreColumna, int indice) {
        this.valor = valor;
        this.nombreColumna = nombreColumna;
        this.indice = indice;
    }

    /**
     * Obtiene el nombre de la columna a la que pertenece esta celda.
     *
     * @return El nombre de la columna.
     */
    public String getNombreColumna() {
        return nombreColumna;
    }

    /**
     * Obtiene el valor almacenado en la celda.
     *
     * @return El valor de la celda.
     */
    public T getValor() {
        return valor;
    }

    /**
     * Establece un nuevo valor en la celda.
     *
     * @param nuevoValor El nuevo valor a asignar en la celda.
     */
    public void setValor(T nuevoValor) {
        this.valor = nuevoValor;
    }

    /**
     * Obtiene el índice de la celda dentro de su columna.
     *
     * @return El índice de la celda.
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Establece un nuevo índice para la celda.
     *
     * @param indiceNuevo El nuevo índice de la celda.
     */
    public void setIndice(int indiceNuevo) {
        this.indice = indiceNuevo;
    }

    /**
     * Devuelve una representación en cadena del valor de la celda.
     * Si el valor es null, devuelve "null".
     *
     * @return El valor de la celda como String.
     */
    @Override
    public String toString() {
        return valor == null ? "null" : valor.toString();
    }
}
