import java.util.List;

public interface Limpieza {
    void mostrarNAs();  
    List<Celda<Object>> leerNAs();  
    public abstract Tabla reemplazarNAs();
    public abstract Tabla reemplazarNAs(String nombreColumna);
    public abstract Tabla eliminarFilasConNAs();  
    public abstract Tabla eliminarFilasConNAs(String nombreColumna);
}