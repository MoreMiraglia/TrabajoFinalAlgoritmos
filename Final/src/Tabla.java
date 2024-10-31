import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//prueba
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class Tabla implements Manipulacion,Limpieza {
    private String nombreTabla;
    private List<Columna<?>> columnas;
    private int cantColumnas;
    private int cantFilas;


    public Tabla(String nombreTabla, String rutaArchivo) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        ArchivoCSV archivoCSV = new ArchivoCSV(rutaArchivo);
        cargarDatosTabla(archivoCSV);
        this.cantColumnas = columnas.size();
        setCantFilas();
        
    }

    public Tabla(String nombreTabla, List<String> nombresColumnas) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        for (String nombreColumna : nombresColumnas) {
            columnas.add(new Columna<>(nombreColumna));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }

    // Constructor desde Object[][], tomando la primera fila como nombres de columnas
    public Tabla(String nombreTabla, Object[][] datos) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();

        // Suponemos que la primera fila contiene los nombres de las columnas
        if (datos.length == 0) return;  // Si no hay datos, se sale del constructor

        // Crear columnas usando los nombres en la primera fila
        for (int i = 0; i < datos[0].length; i++) {
            List<Celda<Object>> celdas = new ArrayList<>();
            for (int j = 1; j < datos.length; j++) { // Empezamos en la segunda fila para obtener datos
                celdas.add(new Celda<>(datos[j][i],datos[0][i].toString(),celdas.size()-1));
            }
            columnas.add(new Columna<>(datos[0][i].toString(), celdas));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }

    // Constructor de la copia de la tabla original. (Constructor de copia profunda)
    public Tabla(Tabla otraTabla) {
        this.nombreTabla = otraTabla.nombreTabla;
        this.columnas = new ArrayList<>();
        for (Columna<?> columna : otraTabla.columnas) {
            this.columnas.add(new Columna<>(columna));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }
    
    // Constructor que recibe una secuencia lineal de datos y nombres de columnas
    public Tabla(String nombreTabla, List<String> nombresColumnas, List<Object> datosLineales) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();

        // Crear columnas a partir de los nombres
        for (String nombreColumna : nombresColumnas) {
            columnas.add(new Columna<>(nombreColumna));
            
        }

        // Verificar que la cantidad de datos sea un múltiplo del número de columnas
        int numColumnas = nombresColumnas.size();
        if (datosLineales.size() % numColumnas != 0) {
            throw new IllegalArgumentException("La cantidad de datos no coincide con las columnas proporcionadas.");
        }

        // Distribuir los datos en las columnas
        for (int i = 0; i < datosLineales.size(); i++) {
            int colIndex = i % numColumnas;
            Columna<Object> columna = (Columna<Object>) columnas.get(colIndex);
            columna.addCelda(new Celda<>(datosLineales.get(i),columna.getNombre(),i));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }

    //Constructor que genera una tabla concatenada, dada dos tablas iguales (NO FUNCIONA BIEN TODAVIA)
    public Tabla(String nombre, Tabla tabla1, Tabla tabla2) {
        // Verificar que las tablas tengan las mismas columnas (cantidad y nombres)
        if (!sonColumnasCompatibles(tabla1, tabla2)) {
            throw new IllegalArgumentException("Las tablas no tienen las mismas columnas o tipos de datos.");
        }
        
        // Inicializar el nombre de la tabla concatenada
        this.nombreTabla = nombre;
        this.columnas = new ArrayList<>();
        
        // Copiar las columnas de la primera tabla (sin datos)
        for (Columna<?> columna : tabla1.columnas) {
            this.columnas.add(new Columna<>(columna.getNombre()));
        }

        this.cantColumnas = columnas.size();
    
        // Copiar filas de la primera tabla
        for (int i = 0; i < tabla1.cantFilas; i++) {
            List<Object> fila = new ArrayList<>();
            for (Columna<?> columna : tabla1.columnas) {
                fila.add(columna.getCeldas().get(i).getValor());
            }
            this.agregarFila(fila); // Aquí agregar fila llama a agregarFila() para cada fila de la primera tabla
        }
        
        // Copiar filas de la segunda tabla
        for (int i = 0; i < tabla2.cantFilas; i++) {
            List<Object> fila = new ArrayList<>();
            for (Columna<?> columna : tabla2.columnas) {
                fila.add(columna.getCeldas().get(i).getValor());
            }
            this.agregarFila(fila); // Aquí agregar fila llama a agregarFila() para cada fila de la segunda tabla
        }
        
        // Ajustar el conteo de columnas y filas en la tabla resultante

        setCantFilas();
    }
    
    

    public void cargarDatosTabla(ArchivoCSV archivoCSV) {
        Map<String, List<Object>> datos = archivoCSV.getMap();
        for (Map.Entry<String, List<Object>> entry : datos.entrySet()) {
            List<Celda<Object>> celdas = new ArrayList<>();
            for (Object valor : entry.getValue()) {
                celdas.add(new Celda<>(valor,entry.getKey(),celdas.size()-1));
            }
            Columna<Object> nuevaColumna = new Columna<>(entry.getKey(), celdas);
            columnas.add(nuevaColumna);
        }

    }
    public void setCantFilas (){
        this.cantFilas = columnas.get(0).getCeldas().size(); 
    }

    @Override
    public void agregarColumna(Columna columna) {
        if (columna.getCeldas().size() != cantFilas) {
            throw new IllegalArgumentException("La longitud de la nueva columna debe coincidir con el número de filas de la tabla.");
        }
        columnas.add(columna);
        cantColumnas = columnas.size(); 
    }

    @Override
    public void agregarColumna(String nombre, Object[] nuevaColumnaArray) {
        // Verificar que la longitud del arreglo coincida con el número de filas de la tabla
        if (nuevaColumnaArray.length != cantFilas) {
            throw new IllegalArgumentException("La longitud de la nueva columna debe coincidir con el número de filas de la tabla.");
        }

        // Crear una lista de celdas a partir del arreglo
        List<Celda<Object>> celdas = new ArrayList<>();
        for (int i = 0; i < nuevaColumnaArray.length; i++) {
            Object valorCelda = nuevaColumnaArray[i];
            celdas.add(new Celda<>(valorCelda, nombre, i));  // Crear cada celda con el valor, nombre de columna e índice
        }

        // Crear la nueva columna y añadirla a la tabla
        Columna<Object> columna = new Columna<>(nombre, celdas);
        columnas.add(columna);

        // Actualizar el conteo de columnas
        cantColumnas = columnas.size();
    }

    @Override
    public void agregarFila(List<Object> valores) {
        // Verificar que la cantidad de valores coincida con el número de columnas
        if (valores.size() != cantColumnas) {
            throw new IllegalArgumentException("La cantidad de valores no coincide con el número de columnas.");
        }
    
        // Agregar los valores a las columnas correspondientes
        for (int i = 0; i < valores.size(); i++) {
            Columna<Object> columna = (Columna<Object>) columnas.get(i);
            columna.addCelda(new Celda<>(valores.get(i), columna.getNombre(), columna.getCeldas().size()));
        }
    
        // Actualizar el número de filas
        setCantFilas();
    }

    public void eliminarColumna(String nombreColumna) {
        Columna<?> columnaAEliminar = null;
    
        // Buscar la columna con el nombre especificado
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columnaAEliminar = columna;
                break;
            }
        }
    
        // Si se encuentra la columna, eliminarla de la lista de columnas
        if (columnaAEliminar != null) {
            columnas.remove(columnaAEliminar);
            cantColumnas = columnas.size(); // Actualizar la cantidad de columnas
        } else {
            System.out.println("La columna '" + nombreColumna + "' no existe en la tabla.");
        }
    }

    public void eliminarFila(int indiceFila) {
        // Verificar si el índice de la fila es válido
        if (indiceFila < 0 || indiceFila >= cantFilas) {
            throw new IndexOutOfBoundsException("Índice de fila fuera de rango.");
        }
    
        // Eliminar la celda correspondiente en cada columna
        for (Columna<?> columna : columnas) {
            columna.eliminarFila(indiceFila);
        }
        cantFilas--; // Actualizar la cantidad de filas
    }

    public void getTipoDato(){
        for (Columna<?> columna : columnas) {
            columna.getTipoDeDato();           
        }
    }

    public void getTipoDato (String nombreColumna) {
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columna.getTipoDeDato();
                break;
            }
        }
        System.out.println("La columna '" + nombreColumna + "' no existe en la tabla.");
    }
    

    @Override
    public void reasignarValor(String nombre,int indice, Object nuevoValor){
        for (Columna columna : columnas){
            if (columna.getNombre().equals(nombre)){
                columna.modificarValor(indice, nuevoValor);
            }
        }
    }

    @Override
    public void mostrarNAs() {
        System.out.println("Celdas con NA en la tabla: " + nombreTabla);
        boolean hayNAs = false;

        for (Columna<?> columna : columnas) {
            for (Celda<?> celda : columna.getCeldas()) {
                if (celda.getValor() == null) {
                    System.out.println("Columna: " + columna.getNombre() + " Fila n°" + celda.getIndice() +  " - Valor: null");
                    hayNAs = true;
                }
            }
        }

        if (!hayNAs) {
            System.out.println("No hay valores NA en la tabla.");
        }
    }

    // Método para verificar compatibilidad de columnas entre dos tablas
    private boolean sonColumnasCompatibles(Tabla tabla1, Tabla tabla2) {
        if (tabla1.getColumnas() != tabla2.getColumnas()) {
            return false;
        }

        for (int i = 0; i < tabla1.getColumnas(); i++) {
            Columna<?> columna1 = tabla1.columnas.get(i);
            Columna<?> columna2 = tabla2.columnas.get(i);

            // Verificar nombre y tipo de las columnas
            if (!columna1.getNombre().equals(columna2.getNombre()) || !columna1.getTipoDeDato().equals(columna2.getTipoDeDato())) {
                return false;
            }
        }

        return true;
    }
    // Método para copiar las columnas de una tabla
    private void copiarColumnas(Tabla tabla) {
        for (Columna<?> columna : tabla.columnas) {
            Columna<Object> columnaCopiada = new Columna<>(columna.getNombre());

            for (Celda<?> celda : columna.getCeldas()) {
                columnaCopiada.addCelda(new Celda<>(celda.getValor(), columnaCopiada.getNombre(), columnaCopiada.getCeldas().size()));
            }

            this.columnas.add(columnaCopiada);
        }
    }

    @Override
    public List<Celda<Object>> leerNAs() {
        List<Celda<Object>> celdasNA = new ArrayList<>();

        for (Columna<?> columna : columnas) {
            for (Celda<?> celda : columna.getCeldas()) {
                if (celda.getValor() == null) {
                    celdasNA.add((Celda<Object>) celda);
                }
            }
        }

        return celdasNA;
    }

    // Métodos de visualización
    private List<Integer> calcularAnchoColumnas() {
        List<Integer> anchos = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            int maxAncho = columna.getNombre().length();
            for (Celda<?> celda : columna.getCeldas()) {
                maxAncho = Math.max(maxAncho, celda.toString().length());
            }
            anchos.add(maxAncho);
        }
        return anchos;
    }

    public void mostrarColumnas() {
        List<Integer> anchos = calcularAnchoColumnas();
        for (int i = 0; i < cantColumnas; i++) {
            System.out.printf("%-" + anchos.get(i) + "s | ", columnas.get(i).getNombre());
        }
        System.out.println();
        
        for (int ancho : anchos) {
            System.out.print("-".repeat(ancho) + "-+-");
        }
        System.out.println();
    }

    public void head() {
        int x = 5;
        if (x > cantFilas) {
            x = cantFilas; // Limitar x al número de filas disponibles
        }
        
        mostrarColumnas(); // Imprime los nombres de las columnas
        mostrarDatos(0, x); // Imprime las primeras x filas
    }
    
    public void head(int x) {
        if (x > cantFilas) {
            x = cantFilas; // Limitar x al número de filas disponibles
        }
        
        mostrarColumnas(); // Imprime los nombres de las columnas
        mostrarDatos(0, x); // Imprime las primeras x filas
    }

    public void tail() {
        int x = 5;
        if (x > cantFilas) {
            x = cantFilas; // Limitar x al número de filas disponibles
        }
        
        mostrarColumnas(); // Imprime los nombres de las columnas
        mostrarDatos(cantFilas - x, cantFilas); // Imprime las últimas x filas
    }

    public void tail(int x) {
        if (x > cantFilas) {
            x = cantFilas; // Limitar x al número de filas disponibles
        }
        
        mostrarColumnas(); // Imprime los nombres de las columnas
        mostrarDatos(cantFilas - x, cantFilas); // Imprime las últimas x filas
    }


    public void mostrarDatos() {
        List<Integer> anchos = calcularAnchoColumnas();
        for (int i = 0; i < cantFilas; i++) {
            for (int j = 0; j < cantColumnas; j++) {
                System.out.printf("%-" + anchos.get(j) + "s | ", columnas.get(j).getCeldas().get(i).getValor());
            }
            System.out.println();
        }
    }

    public void mostrarDatos(int startRow, int endRow) {
        List<Integer> anchos = calcularAnchoColumnas();
        
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < columnas.size(); j++) {
                System.out.printf("%-" + anchos.get(j) + "s | ", columnas.get(j).getCeldas().get(i).getValor());
            }
            System.out.println();
        }
    }

    public void mostrarTabla() {
        mostrarColumnas();
        mostrarDatos();
 
   }

    public int getFilas(){
        System.out.println(cantFilas);
        return cantFilas;
    }

    public int getColumnas(){
        System.out.println(cantColumnas);
        return cantColumnas;
    }
    public void nombreColumnas(){
        for (Columna columna : columnas){
            System.out.println(columna.getNombre());
        }
    }
}