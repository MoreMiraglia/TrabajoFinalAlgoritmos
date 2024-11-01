import java.util.List;

public interface Limpieza {
    void mostrarNAs();  // Método para mostrar las celdas con valores NA
    List<Celda<Object>> leerNAs();  // Método para devolver las celdas con NA
    public abstract void reemplazarNAs();
    public abstract void reemplazarNAs(String nombreColumna);
    void eliminarFilasConNAs();  // Nuevo método
    void eliminarFilasConNAs(String nombreColumna);
}