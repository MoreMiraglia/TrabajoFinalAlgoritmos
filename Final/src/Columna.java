import java.util.List;
import java.util.ArrayList;

class Columna<T> {
    private String nombre;
    private List<Celda<T>> celdas;


    // Constructores
    public Columna(String nombre) {
        this.nombre = nombre;
        this.celdas = new ArrayList<>();
    }

    public Columna(String nombre, List<Celda<T>> celdas) {
        this.nombre = nombre;
        this.celdas = celdas;
    }
    // Constructor de copia profunda
    public Columna(Columna<T> otraColumna) {
        this.nombre = otraColumna.nombre;
        this.celdas = new ArrayList<>();
        for (Celda<T> celda : otraColumna.celdas) {
            this.celdas.add(new Celda<>(celda.getValor())); // Copia de cada celda
        }
    }
    // Getters
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
