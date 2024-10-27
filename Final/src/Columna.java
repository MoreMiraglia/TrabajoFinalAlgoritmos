import java.util.List;
import java.util.ArrayList;

class Columna<T> {
    private String nombre;
    private List<Celda<T>> celdas;

    public Columna(String nombre) {
        this.nombre = nombre;
        this.celdas = new ArrayList<>();
    }

    public Columna(String nombre, List<Celda<T>> celdas) {
        this.nombre = nombre;
        this.celdas = celdas;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Celda<T>> getCeldas() {
        return celdas;
    }

    public void addCelda(Celda<T> celda) {
        celdas.add(celda);
    }
}
