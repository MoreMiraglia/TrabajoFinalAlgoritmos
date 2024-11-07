import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Ordenamiento {
    private final Tabla tabla;

    public Ordenamiento(Tabla tabla) {
        this.tabla = tabla;
    }

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

    private Celda<Object> obtenerCelda(String nombreColumna, int filaIdx) {
        for (Columna<?> columna : tabla.getColumnas()) {
            if (columna.getNombre().equals(nombreColumna)) {
                return (Celda<Object>) columna.getCeldas().get(filaIdx);
            }
        }
        return null;
    }
}
