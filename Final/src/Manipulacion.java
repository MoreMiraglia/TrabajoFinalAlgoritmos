import java.util.ArrayList;
import java.util.List;

public interface Manipulacion {
    public abstract void reasignarValor(String nombre,int indice, Object nuevoValor);

    public abstract void agregarColumna(Columna nuevaColumna);

    public abstract void agregarColumna (String nombre, Object[] nuevaColumnaArray);

    public abstract void agregarFila (List<Object> valores);

    public abstract void eliminarColumna(String nombreColumna);

    public abstract void eliminarFila(int indiceFila);

}
