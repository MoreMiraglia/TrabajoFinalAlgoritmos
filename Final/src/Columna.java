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
    
    public void getTipoDeDato() {
        if (!celdas.isEmpty() && celdas.get(0).getValor() != null) {
            System.out.println(nombre + " es: " + celdas.get(0).getValor().getClass().getSimpleName());
        } else {
            System.out.println("La columna " + nombre + " está vacía o contiene solo valores nulos.");
        }
    }
    
    public void addCelda(Celda<T> celda) {
        // Si la columna ya tiene celdas, verificar el tipo de dato
        if (!celdas.isEmpty()) {
            T tipoReferencia = celdas.get(0).getValor();
            
            // Verificar si el tipo coincide antes de agregar la nueva celda
            if (tipoReferencia != null && !tipoReferencia.getClass().isInstance(celda.getValor())) {
                throw new IllegalArgumentException("El tipo de dato de la celda no coincide con el tipo de dato de la columna '" + nombre + "'.");
            }
        }
        celdas.add(celda);
    }

    public void eliminarFila (int indiceFila){
        celdas.remove(indiceFila);
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
                throw new IllegalArgumentException("El tipo de dato de la celda no coincide con el tipo de dato de la columna '" + nombre + "'.");
            }
        }

        // Realizar el casting a T y modificar el valor
        T valorConvertido = (T) valor;  // Casting explícito a T
        celdas.get(indice).setValor(valorConvertido);
    }

    
}
