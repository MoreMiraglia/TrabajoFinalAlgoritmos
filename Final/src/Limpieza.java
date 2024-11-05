import java.util.List;

public interface Limpieza {
    void mostrarNAs();  // Método para mostrar las celdas con valores NA
    List<Celda<Object>> leerNAs();  // Método para devolver las celdas con NA
    public abstract Tabla reemplazarNAs();
    public abstract Tabla reemplazarNAs(String nombreColumna);
    public abstract Tabla eliminarFilasConNAs();  // Nuevo método
    public abstract Tabla eliminarFilasConNAs(String nombreColumna);
}