import java.util.List;

public interface Filtro {
    //devuelve una lista de celdas que coincidan con el valor especificado en la columna dada
    List<Celda<?>> filtrarPorColumna(String nombreColumna, Object valor); 
    //devuelve una lista de índices de fila donde el valor especificado se encuentra en la columna dada
    List<Integer> buscarValor(String nombreColumna, Object valor); 
}