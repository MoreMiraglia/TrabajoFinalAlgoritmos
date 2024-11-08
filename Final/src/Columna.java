import java.util.List;
import java.util.ArrayList;

class Columna<T> {
    private String nombre;
    private List<Celda<T>> celdas;

    /**
     * Constructor que crea una columna con el nombre especificado y sin celdas.
     *
     * @param nombre el nombre de la columna.
     */
    public Columna(String nombre) {
        this.nombre = nombre;
        this.celdas = new ArrayList<>();
    }
    /**
     * Constructor que crea una columna con el nombre especificado y una lista de celdas.
     *
     * @param nombre el nombre de la columna.
     * @param celdas la lista de celdas que contiene la columna.
     */
    public Columna(String nombre, List<Celda<T>> celdas) {
        this.nombre = nombre;
        this.celdas = celdas;
    }
    /**
     * Constructor de copia profunda que crea una nueva columna copiando el nombre y 
     * duplicando cada celda de otra columna.
     *
     * @param otraColumna la columna a copiar.
     */
    public Columna(Columna<T> otraColumna) {
        this.nombre = otraColumna.nombre;
        this.celdas = new ArrayList<>();
        for (Celda<T> celda : otraColumna.celdas) {
            this.celdas.add(new Celda<>(celda.getValor(),this.nombre,celdas.size()-1)); // Copia de cada celda
        }
    }
    /**
     * Obtiene el nombre de la columna.
     *
     * @return el nombre de la columna.
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Obtiene la lista de celdas que contiene la columna.
     *
     * @return la lista de celdas de la columna.
     */
    public List<Celda<T>> getCeldas() {
        return celdas;
    }
    /**
     * Obtiene el tipo de dato de la columna basado en el valor de la primera celda.
     * Si la columna está vacía o contiene solo valores nulos, devuelve {@code null}.
     *
     * @return la clase del tipo de dato de la columna o {@code null} si la columna está vacía o solo tiene valores nulos.
     */
    public Class <?> getTipoDeDato() {
        if (!celdas.isEmpty() && celdas.get(0).getValor() != null) {
            System.out.println(nombre + " es: " + celdas.get(0).getValor().getClass().getSimpleName());
            return celdas.get(0).getValor().getClass();
        } else {
            System.out.println("La columna " + nombre + " está vacía o contiene solo valores nulos.");
            return null;
        }
    }

    /**
     * Agrega una celda a la columna, verificando que el tipo de dato coincida con el de las celdas existentes.
     * Si el tipo de dato de la nueva celda no coincide con el tipo de las celdas actuales de la columna,
     * lanza una excepción.
     *
     * @param celda la celda a agregar a la columna.
     * @throws IllegalArgumentException si el tipo de dato de la celda no coincide con el de la columna.
     */
    public void addCelda(Celda<T> celda) {
        // Si la columna ya tiene celdas, verificar el tipo de dato
        if (!celdas.isEmpty()) {
            T tipoReferencia = celdas.get(0).getValor();
            
            // Verificar si el tipo coincide antes de agregar la nueva celda
            if (celda.getValor() != null && tipoReferencia != null && !tipoReferencia.getClass().isInstance(celda.getValor())) {
                throw new IllegalArgumentException("El tipo de dato de la celda no coincide con el tipo de dato de la columna '" + nombre + "'.");
            }
        }
        celdas.add(celda);
    }
    /**
     * Elimina una celda de la columna en la posición especificada.
     *
     * @param indiceFila el índice de la fila a eliminar.
     */
    public void eliminarFila (int indiceFila){
        celdas.remove(indiceFila);
    }
    /**
     * Actualiza los índices de las celdas después de eliminar una celda en una posición específica.
     * Todas las celdas con un índice mayor al índice eliminado se decrementan en uno.
     *
     * @param indiceEliminado el índice de la celda eliminada.
     */
    public void actualizarIndices(int indiceEliminado){
        for (Celda celda:celdas){
            if (celda.getIndice() > indiceEliminado ){
                celda.setIndice(celda.getIndice()-1);
            }
        }
    }
    /**
     * Modifica el valor de una celda en la posición especificada, verificando que el tipo de dato coincida con el de la columna.
     * Si el valor es {@code null}, lo establece sin verificación de tipo.
     *
     * @param indice el índice de la celda a modificar.
     * @param valor el nuevo valor a asignar a la celda.
     * @throws IllegalArgumentException si el tipo de dato del valor no coincide con el tipo de la columna.
     */
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
    /**
     * Reemplaza los valores {@code null} en la columna con un valor representativo basado en el tipo de dato de la columna.
     * - Si el tipo es {@code Integer} o {@code Float}, reemplaza los valores nulos con el promedio de los valores no nulos.
     * - Si el tipo es {@code Boolean}, reemplaza los valores nulos con el valor más frecuente (true o false).
     */
    public void reemplazarNAs() {
            Class<?> tipoDato = getTipoDeDato();
            
            if (tipoDato == Integer.class) {
                // Calcular promedio de valores no nulos
                double promedio = getCeldas().stream()
                                         .filter(celda -> celda.getValor() != null)
                                         .mapToInt(celda -> (Integer) celda.getValor())
                                         .average()
                                         .orElse(0);
                T valor = (T) Double.valueOf(promedio);
                
                // Reemplazar valores null por el promedio
                for (Celda<T> celda : getCeldas()) {
                    if (celda.getValor() == null) {
                        celda.setValor(valor);
                    }
                }
                
            } else if (tipoDato == Float.class) {
                // Calcular promedio de valores no nulos
                double promedio = getCeldas().stream()
                                         .filter(celda -> celda.getValor() != null)
                                         .mapToDouble(celda -> (Float) celda.getValor())
                                         .average()
                                         .orElse(0);
                T valor = (T) Double.valueOf(promedio);

                // Reemplazar valores null por el promedio
                for (Celda<T> celda : getCeldas()) {
                    if (celda.getValor() == null) {
                        celda.setValor(valor);
                    }
                }
                
            } else if (tipoDato == Boolean.class) {
                // Contar frecuencia de true y false
                long countTrue = getCeldas().stream()
                                        .filter(celda -> celda.getValor() != null)
                                        .filter(celda -> (Boolean) celda.getValor())
                                        .count();



                long countFalse = getCeldas().stream()
                                         .filter(celda -> celda.getValor() != null)
                                         .filter(celda -> !(Boolean) celda.getValor())
                                         .count();

                // Determinar el valor más frecuente
                boolean valorMasFrecuente = countTrue >= countFalse;

                T valor = (T) Boolean.valueOf(valorMasFrecuente);

                // Reemplazar valores null por el valor más frecuente
                for (Celda<T> celda : getCeldas()) {
                    if (celda.getValor() == null) {
                        celda.setValor(valor);
                }
            }
        }
    }
}