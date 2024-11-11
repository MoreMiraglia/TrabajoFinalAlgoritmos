public interface Filtro {
    Tabla filtrarPorColumna(String nombreColumna, Object valor); 
    Tabla filtrarPorRango(String nombreColumna, Comparable<?> valorMin, Comparable<?> valorMax); 
}