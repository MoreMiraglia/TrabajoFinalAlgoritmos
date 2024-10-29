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
            this.celdas.add(new Celda<>(celda.getValor(),this.nombre,celdas.size()-1)); // Copia de cada celda
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

    public void modificarValor(int indice, Object valor){
    // Permitir valor null sin hacer casting
    if (valor == null) {
        celdas.get(indice).setValor(null);
        return;
    }

    // Verificar el tipo del valor con el tipo de la columna antes del casting
        if (!celdas.isEmpty()) {
            T tipoReferencia = celdas.get(0).getValor();
            
            // Verificar si el tipo coincide antes de hacer el casting
            if (tipoReferencia != null && !tipoReferencia.getClass().isInstance(valor)) {
                throw new IllegalArgumentException("El tipo del valor no coincide con el tipo de la columna '" + nombre + "'.");
            }
        }

        // Realizar el casting a T y modificar el valor
        T valorConvertido = (T) valor;  // Casting explícito a T
        celdas.get(indice).setValor(valorConvertido);
    }

    
}
