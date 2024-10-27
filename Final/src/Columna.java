import java.util.List;

class Columna<T> {
    private String nombre;
    private List<Object> celdas;

    public Columna (String nombre){
        this.nombre = nombre;
    }
    public Columna(String nombre, List<Object> listaCelda) {
        this.nombre = nombre;
        this.celdas = listaCelda;

    }
    public void addCelda(T valor) {
        celdas.add(new Celda<>(valor));
    }
    // Métodos adicionales para acceder a las celdas, etc.
    public String getNombre() {
        return nombre;
    }

    public List<Object> getCeldas() {
        return celdas;
    }

}