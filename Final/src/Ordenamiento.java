import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Proporciona funcionalidad para ordenar las filas de una tabla en base a los valores
 * de una o más columnas y los criterios de orden (ascendente o descendente) especificados.
 */
class Ordenamiento {
    private final Tabla tabla;

    /**
     * Constructor de la clase Ordenamiento.
     *
     * @param tabla La tabla cuyos datos se van a ordenar.
     */
    public Ordenamiento(Tabla tabla) {
        this.tabla = tabla;
    }

    /**
     * Obtiene una lista de índices que representan el orden de las filas de la tabla
     * en base a los criterios de ordenación definidos para cada columna.
     *
     * @param nombresColumnas      Los nombres de las columnas a ordenar.
     * @param criteriosAscendentes Una lista de valores booleanos que indica si cada columna
     *                             se ordena de forma ascendente (true) o descendente (false).
     * @return Una lista de índices que representa el nuevo orden de las filas de la tabla.
     * @throws IllegalArgumentException Si la cantidad de nombres de columnas y criterios no coincide
     *                                  o si alguna columna especificada no se encuentra en la tabla.
     */
    public List<Integer> obtenerOrdenIndices(List<String> nombresColumnas, List<Boolean> criteriosAscendentes) {
        if (nombresColumnas.size() != criteriosAscendentes.size()) {
            throw new IllegalArgumentException("La cantidad de columnas y criterios debe coincidir.");
        }

        // Crear una lista de comparadores en base a los criterios proporcionados
        List<Comparator<Integer>> comparadores = new ArrayList<>();
        for (int i = 0; i < nombresColumnas.size(); i++) {
            String nombreColumna = nombresColumnas.get(i);
            boolean ascendente = criteriosAscendentes.get(i);

            Comparator<Integer> comparador = (filaIdx1, filaIdx2) -> {
                Celda<Object> celda1 = obtenerCelda(nombreColumna, filaIdx1);
                Celda<Object> celda2 = obtenerCelda(nombreColumna, filaIdx2);

                if (celda1 == null || celda2 == null) {
                    throw new IllegalArgumentException("Columna no encontrada: " + nombreColumna);
                }

                Comparable valor1 = (Comparable) celda1.getValor();
                Comparable valor2 = (Comparable) celda2.getValor();

                // Manejo de nulls: primero los nulls van al final
                if (valor1 == null && valor2 == null) return 0;
                if (valor1 == null) return ascendente ? 1 : -1;  // null al final en orden ascendente
                if (valor2 == null) return ascendente ? -1 : 1;  // null al final en orden descendente

                // Comparación normal si ambos valores no son null
                return ascendente ? valor1.compareTo(valor2) : valor2.compareTo(valor1);
            };

            comparadores.add(comparador);
        }

        // Obtener los índices de las filas y ordenarlos usando los comparadores
        List<Integer> indicesFilas = new ArrayList<>();
        for (int i = 0; i < tabla.getFilas(); i++) {
            indicesFilas.add(i);
        }

        indicesFilas.sort((filaIdx1, filaIdx2) -> {
            for (Comparator<Integer> comparador : comparadores) {
                int resultado = comparador.compare(filaIdx1, filaIdx2);
                if (resultado != 0) return resultado;
            }
            return 0;
        });

        return indicesFilas; // Retorna el nuevo orden de índices de las filas
    }

    /**
     * Obtiene la celda ubicada en una columna específica y en el índice de fila indicado.
     *
     * @param nombreColumna El nombre de la columna de la celda.
     * @param filaIdx       El índice de la fila de la celda.
     * @return La celda en la columna y fila especificadas, o null si no se encuentra la columna.
     */
    private Celda<Object> obtenerCelda(String nombreColumna, int filaIdx) {
        for (Columna<?> columna : tabla.getColumnas()) {
            if (columna.getNombre().equals(nombreColumna)) {
                return (Celda<Object>) columna.getCeldas().get(filaIdx);
            }
        }
        return null;
    }
}
