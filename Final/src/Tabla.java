import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.print.attribute.standard.MediaSize.NA;
import javax.swing.*;
import java.awt.*;

class Tabla implements Manipulacion, Limpieza, Filtro{
    private String nombreTabla;
    private List<Columna<?>> columnas;
    private int cantColumnas;
    private int cantFilas;

    /**
     * Constructor de la clase Tabla que inicializa una tabla a partir de un archivo CSV.
     * Se asume que la primera fila del archivo contiene los nombres de las columnas.
     * 
     * @param nombreTabla El nombre asignado a la tabla.
     * @param rutaArchivo Ruta del archivo CSV desde el cual se cargarán los datos.
     *                     Este archivo se leerá para poblar las columnas y filas de la tabla.
     */
    public Tabla(String nombreTabla, String rutaArchivo) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        ArchivoCSV archivoCSV = new ArchivoCSV(rutaArchivo);
        cargarDatosTabla(archivoCSV);
        this.cantColumnas = columnas.size();
        setCantFilas();
        
    }
    /**
     * Constructor de la clase Tabla que inicializa una tabla con columnas vacías,
     * utilizando una lista de nombres de columnas.
     *
     * @param nombreTabla El nombre asignado a la tabla.
     * @param nombresColumnas Lista de nombres de las columnas que se crearán en la tabla.
     *                         Cada elemento de la lista se convertirá en una columna vacía.
     */
    public Tabla(String nombreTabla, List<String> nombresColumnas) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        for (String nombreColumna : nombresColumnas) {
            columnas.add(new Columna<>(nombreColumna));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }
    /**
     * Constructor que crea una tabla a partir de una matriz bidimensional de objetos. 
     * Se asume que la primera fila contiene los nombres de las columnas.
     *
     * @param nombreTabla El nombre de la tabla.
     * @param datos Una matriz bidimensional de objetos, donde la primera fila contiene los nombres de las columnas 
     *              y las filas posteriores contienen los datos correspondientes a cada columna.
     * @throws IllegalArgumentException Si la matriz de datos está vacía, no se realiza ninguna acción.
     */
    public Tabla(String nombreTabla, Object[][] datos) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();

        // Suponemos que la primera fila contiene los nombres de las columnas
        if (datos.length == 0) return;  // Si no hay datos, se sale del constructor

        // Crear columnas usando los nombres en la primera fila
        for (int i = 0; i < datos[0].length; i++) {
            List<Celda<Object>> celdas = new ArrayList<>();
            for (int j = 1; j < datos.length; j++) { // Empezamos en la segunda fila para obtener datos
                celdas.add(new Celda<>(datos[j][i],datos[0][i].toString(),celdas.size()));
            }
            columnas.add(new Columna<>(datos[0][i].toString(), celdas));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }
    /**
     * Constructor de copia profunda que crea una nueva tabla a partir de otra tabla existente.
     * Se copian los datos y las columnas de la tabla original a la nueva tabla.
     *
     * @param otraTabla La tabla original que se va a copiar.
     */
    public Tabla(Tabla otraTabla) {
        this.nombreTabla = otraTabla.nombreTabla;
        this.columnas = new ArrayList<>();
        for (Columna<?> columna : otraTabla.columnas) {
            this.columnas.add(new Columna<>(columna));
        }
        this.cantColumnas = columnas.size();
        setCantFilas();
    }
    /**
     * Constructor que crea una tabla a partir de una lista de nombres de columnas y una lista de datos lineales.
     * Los datos se distribuyen en las columnas según el orden en la lista de datos.
     *
     * @param nombreTabla El nombre de la tabla.
     * @param nombresColumnas Lista de nombres de las columnas.
     * @param datosLineales Lista de datos que se distribuirán entre las columnas.
     * @throws IllegalArgumentException Si el número de elementos en datosLineales no es un múltiplo del número de columnas.
     */
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
    /**
     * Constructor que crea una tabla concatenando dos tablas existentes, asegurándose de que ambas tablas tengan las mismas columnas.
     * Si las tablas no son compatibles, se lanza una excepción.
     *
     * @param nombre El nombre asignado a la tabla concatenada.
     * @param tabla1 La primera tabla a concatenar.
     * @param tabla2 La segunda tabla a concatenar.
     * @throws IllegalArgumentException Si las tablas no tienen el mismo número de columnas o columnas incompatibles.
     */
    public Tabla(String nombre,Tabla tabla1, Tabla tabla2) {
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
        //Actualizar cantidad de colunas
        this.cantColumnas = columnas.size();
        // Copiar filas de la primera tabla
        copiarFilas(tabla1);
        // Copiar filas de la segunda tabla
        copiarFilas(tabla2);
        //Actulizar cantidad de filas
        setCantFilas();
    }
    /**
     * Carga los datos de la tabla a partir de un archivo CSV.
     * Los datos del archivo CSV se almacenan en un mapa, donde las claves son los nombres de las columnas
     * y los valores son las listas de objetos correspondientes a los valores de cada columna.
     * Para cada columna en el archivo CSV, se crea una nueva columna en la tabla.
     *
     * @param archivoCSV El objeto que contiene los datos del archivo CSV a cargar en la tabla.
     */
    public void cargarDatosTabla(ArchivoCSV archivoCSV) {
        Map<String, List<Object>> datos = archivoCSV.getMap();

        for (Map.Entry<String, List<Object>> entry : datos.entrySet()) {
            List<Celda<Object>> celdas = new ArrayList<>();
            for (Object valor : entry.getValue()) {
                celdas.add(new Celda<>(valor,entry.getKey(),celdas.size()));
            }
            Columna<Object> nuevaColumna = new Columna<>(entry.getKey(), celdas);
            columnas.add(nuevaColumna);
        }

    }
    /**
     * Establece la cantidad de filas en la tabla, basándose en la cantidad de celdas de la primera columna.
     * Este método asume que todas las columnas tienen la misma cantidad de filas.
     */
    public void setCantFilas (){
        this.cantFilas = columnas.get(0).getCeldas().size(); 
    }
    /**
     * Agrega una nueva columna a la tabla, verificando que la longitud de la nueva columna coincida
     * con el número de filas actuales en la tabla.
     *
     * @param columna La columna que se desea agregar a la tabla.
     * @throws IllegalArgumentException Si la longitud de la columna no coincide con el número de filas de la tabla.
     */
    @Override
    public void agregarColumna(Columna columna) {
        if (columna.getCeldas().size() != cantFilas) {
            throw new IllegalArgumentException("La longitud de la nueva columna debe coincidir con el número de filas de la tabla.");
        }
        columnas.add(columna);
        cantColumnas = columnas.size(); 
    }
    /**
     * Agrega una nueva columna a la tabla a partir de un arreglo de objetos. 
     * Verificando que la longitud del arreglo coincida con el número de filas de la tabla.
     * Se crea una columna a partir del nombre proporcionado y los datos del arreglo.
     *
     * @param nombre El nombre de la nueva columna.
     * @param nuevaColumnaArray El arreglo de datos que contiene los valores de la nueva columna.
     * @throws IllegalArgumentException Si la longitud del arreglo no coincide con el número de filas de la tabla.
     */
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
    /**
     * Agrega una nueva fila de valores a la tabla.
     *
     * @param valores Lista de valores que conforman la fila.
     * @throws IllegalArgumentException Si la cantidad de valores no coincide con el número de columnas.
     */
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
    /**
     * Elimina una columna de la tabla, dado su nombre.
     * Si la columna no existe, se muestra un mensaje indicando que no se encontró la columna.
     * 
     * @param nombreColumna El nombre de la columna que se desea eliminar.
     * @return Una nueva instancia de la tabla con la columna eliminada.
     *         Si la columna no se encuentra, la tabla original se devuelve sin cambios.
     */
    public Tabla eliminarColumna(String nombreColumna) {
        Tabla nuevaTabla = new Tabla(this);
        Columna<?> columnaAEliminar = null;
    
        // Buscar la columna con el nombre especificado
        for (Columna<?> columna : nuevaTabla.columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columnaAEliminar = columna;
                break;
            }
        }
    
        // Si se encuentra la columna, eliminarla de la lista de columnas
        if (columnaAEliminar != null) {
            nuevaTabla.columnas.remove(columnaAEliminar);
            cantColumnas = nuevaTabla.columnas.size(); // Actualizar la cantidad de columnas
        } else {
            System.out.println("La columna '" + nombreColumna + "' no existe en la tabla.");
        }
        return nuevaTabla;
    }
    /**
     * Elimina una fila específica de la tabla según su índice.
     *
     * @param indice Índice de la fila a eliminar.
     * @throws IndexOutOfBoundsException Si el índice está fuera del rango de filas.
     */
    public Tabla eliminarFila(int indiceFila) {
        Tabla nuevaTabla = new Tabla (this);
        // Verificar si el índice de la fila es válido
        if (indiceFila < 0 || indiceFila >= nuevaTabla.cantFilas) {
            throw new IndexOutOfBoundsException("Índice de fila fuera de rango.");
        }
    
        // Eliminar la celda correspondiente en cada columna
        for (Columna<?> columna : nuevaTabla.columnas) {
            columna.eliminarFila(indiceFila);
        }
        nuevaTabla.actualizarIndices(indiceFila);
        nuevaTabla.cantFilas--; // Actualizar la cantidad de filas
        return nuevaTabla;
    }

    /**
     * Actualiza los índices de cada columna después de la eliminación de una fila.
     * <p>
     * Este método se llama cuando una fila es eliminada de la tabla. Para cada columna,
     * se ajustan los índices de las celdas para reflejar la eliminación del índice especificado.
     * </p>
     *
     * @param indiceEliminado El índice de la fila que fue eliminada de la tabla.
     */
    private void actualizarIndices(int indiceEliminado){
        for (Columna columna: columnas){
            columna.actualizarIndices(indiceEliminado);
        }
    }
    /**
     * Muestra en consola el tipo de dato de cada columna en la tabla.
     * Recorre todas las columnas de la tabla y llama al método {@link Columna#getTipoDeDato()} 
     * para cada una de ellas, mostrando el tipo de dato de cada columna en la consola.
     */
    public void getTipoDato(){
        for (Columna<?> columna : columnas) {
            columna.getTipoDeDato();           
        }
    }

    /**
     * Obtiene el tipo de dato de una columna específica y lo muestra en consola.
     *
     * @param nombreColumna El nombre de la columna de la cual queremos obtener el tipo de dato.
     *                      Si la columna existe, muestra el tipo de dato en consola;
     *                      si no, muestra un mensaje de error.
     */
    public void getTipoDato (String nombreColumna) {
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columna.getTipoDeDato();
                break;
            }
        }
        System.out.println("La columna '" + nombreColumna + "' no existe en la tabla.");
    }
    
    /**
     * Reasigna un valor en una fila específica de una columna y devuelve una nueva tabla con el cambio.
     *
     * @param nombre Nombre de la columna en la que queremos reasignar el valor.
     * @param indice Índice de la fila en la que se realizará el cambio.
     * @param nuevoValor Nuevo valor que se asignará en la celda.
     * @return Una nueva instancia de Tabla con el valor modificado en la celda indicada.
     * @throws IllegalArgumentException Si el índice está fuera de rango o si el nuevo valor no es del tipo adecuado.
     */
    @Override
    public Tabla reasignarValor(String nombre,int indice, Object nuevoValor){
        Tabla nuevaTabla = new Tabla(this);
        for (Columna columna : nuevaTabla.columnas){
            if (columna.getNombre().equals(nombre)){
                columna.modificarValor(indice, nuevoValor);
            }
        }
        return nuevaTabla;
    }
    /**
     * Muestra todas las celdas que contienen valores nulos (NA).
     * Si no existen valores NA en la tabla, muestra un mensaje indicando que no hay valores NA.
     * Muestra los nombres de las columnas, índices de filas y valores de las celdas con valores NA en una ventana.
     */
    @Override
    public void mostrarNAs() {
        List<Celda<Object>> hayNAs = leerNAs();
        if (hayNAs.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay valores NA en la tabla " + nombreTabla + ".");
            return;
        }

        String[] nombresColumnas = {"Columna", "Fila", "Valor"};
        String[][] datos = new String[hayNAs.size()][3];

        for (int i = 0; i < hayNAs.size(); i++) {
            Celda<Object> celda = hayNAs.get(i);
            datos[i][0] = celda.getNombreColumna();
            datos[i][1] = String.valueOf(celda.getIndice());
            datos[i][2] = "null";
        }

        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Celdas con NA en " + nombreTabla, tabla);
    }
    /**
     * Elimina todas las filas que contienen valores NA en una nueva tabla.
     * Si no existen valores NA en la tabla, muestra un mensaje y devuelve null.
     * 
     * @return Una nueva instancia de Tabla sin las filas con valores NA. Si no hay valores NA, devuelve null.
     */
    @Override
    public Tabla eliminarFilasConNAs() {
        Tabla nuevaTabla = new Tabla(this);
        List<Celda<Object>> celdasNAs = leerNAs();
        if (celdasNAs.isEmpty()) {
            System.out.println("No hay valores NA en la tabla.");
            return null;
        }

        // Obtener índices únicos en orden descendente para eliminar correctamente
        List<Integer> indices = celdasNAs.stream()
            .map(Celda::getIndice)
            .distinct()
            .sorted((a, b) -> b - a)  // Ordenar en orden descendente
            .toList();

        // Eliminar filas en orden descendente
        for (Integer indice : indices) {
            nuevaTabla = nuevaTabla.eliminarFila(indice);
        }

        System.out.println("Se eliminaron " + indices.size() + " filas con valores NA.");
        return nuevaTabla;
    }
    /**
     * Elimina las filas que contienen valores NA en una columna específica.
     * Si no existen valores NA en la columna, muestra un mensaje y devuelve null.
     * 
     * @param nombreColumna El nombre de la columna en la que se buscarán valores NA.
     * @return Una nueva instancia de Tabla sin las filas con valores NA en la columna indicada. 
     *         Si no hay valores NA en esa columna, devuelve null.
     */
    @Override
    public Tabla eliminarFilasConNAs(String nombreColumna) {
        Tabla nuevaTabla = new Tabla(this);
        int cantEliminadas = 0;
    
        // Filtrar solo las celdas NA de la columna indicada
        List<Celda<Object>> celdasNAs = leerNAs()
            .stream()
            .filter(celda -> celda.getNombreColumna().equals(nombreColumna))
            .toList();
    
        if (celdasNAs.isEmpty()) {
            System.out.println("No hay valores NA en la columna " + nombreColumna);
            return null;
        }
    
        // Obtener índices de las filas que tienen NA en la columna específica, en orden descendente
        List<Integer> indices = celdasNAs.stream()
            .map(Celda::getIndice)
            .distinct()
            .sorted((a, b) -> b - a)  // Orden descendente
            .toList();
    
        // Eliminar filas en el nuevo objeto de tabla en orden descendente
        for (Integer indice : indices) {
            nuevaTabla = nuevaTabla.eliminarFila(indice);
            cantEliminadas++;
        }
    
        System.out.println("Se eliminaron " + cantEliminadas + " filas con valores NA de la columna " + nombreColumna);
        return nuevaTabla;
    }
    /**
     * Verifica si las columnas de dos tablas son compatibles.
     * Las columnas se consideran compatibles si tienen el mismo nombre y tipo de dato.
     *
     * @param tabla1 La primera tabla a comparar.
     * @param tabla2 La segunda tabla a comparar.
     * @return true si las columnas son compatibles, false si no lo son.
     */
    private boolean sonColumnasCompatibles(Tabla tabla1, Tabla tabla2) {
        if (tabla1.cantColumnas != tabla2.cantColumnas) {
            return false;
        }

        for (int i = 0; i < tabla1.cantColumnas; i++) {
            Columna<?> columna1 = tabla1.columnas.get(i);
            Columna<?> columna2 = tabla2.columnas.get(i);

            // Verificar nombre y tipo de las columnas
            if (!columna1.getNombre().equals(columna2.getNombre()) || !columna1.getTipoDeDato().equals(columna2.getTipoDeDato())) {
                return false;
            }
        }

        return true;
    }
    /**
     * Copia las filas de una tabla y las agrega a la tabla actual.
     *
     * @param tabla La tabla desde la cual se copian las filas.
     */
    private void copiarFilas(Tabla tabla){
        for (int i = 0; i < tabla.cantFilas; i++) {
            List<Object> fila = new ArrayList<>();
            for (Columna<?> columna : tabla.columnas) {
                fila.add(columna.getCeldas().get(i).getValor());
            }
            this.agregarFila(fila);
        }
    }
    
    /**
     * Filtra las filas de la tabla de acuerdo a un valor específico en una columna y devuelve una nueva tabla con las filas filtradas.
     * Si la columna no existe o el valor es incompatible, se lanza una excepción.
     *
     * @param nombreColumna El nombre de la columna en la que se realizará el filtro.
     * @param valor El valor que se usará para filtrar las filas.
     * @return Una nueva instancia de Tabla con las filas que cumplen la condición de filtro.
     * @throws IllegalArgumentException Si la columna no existe o el valor es de un tipo incompatible.
     */
    @Override
    public Tabla filtrarPorColumna(String nombreColumna, Object valor) throws IllegalArgumentException {
        // Verificar que la columna exista
        Columna<?> columnaFiltro = null;
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columnaFiltro = columna;
                break;
            }
        }
        if (columnaFiltro == null) {
            throw new IllegalArgumentException("La columna '" + nombreColumna + "' no existe en la tabla.");
        }
    
        // Verificar que el tipo del valor sea compatible con la columna
        if (!columnaFiltro.getCeldas().isEmpty()) {
            Object primerValor = columnaFiltro.getCeldas().get(0).getValor();
            if (primerValor != null && !primerValor.getClass().isInstance(valor)) {
                throw new IllegalArgumentException("El valor proporcionado es de tipo incompatible con la columna '" + nombreColumna + "'.");
            }
        }
    
        // Crear una nueva tabla para almacenar los resultados filtrados
        Tabla tablaFiltrada = new Tabla("Tabla Filtrada", obtenerNombresColumnas());
    
        // Iterar sobre cada fila
        for (int i = 0; i < cantFilas; i++) {
            Object valorFila = null;
    
            // Obtener el valor de la celda en la fila actual de la columna especificada
            valorFila = columnaFiltro.getCeldas().get(i).getValor();
    
            // Si el valor de la celda no es nulo y coincide con el valor dado, agregar la fila a la nueva tabla
            if (valorFila != null && valorFila.equals(valor)) {
                List<Object> valoresFila = new ArrayList<>();
                for (Columna<?> columna : columnas) {
                    valoresFila.add(columna.getCeldas().get(i).getValor());
                }
                tablaFiltrada.agregarFila(valoresFila);
            }
        }
    
        return tablaFiltrada;
    }
    
    /**
     * Filtra las filas de la tabla basándose en un rango específico de valores para una columna dada.
     * Devuelve una nueva tabla que contiene solo las filas cuyo valor en la columna indicada está dentro
     del rango especificado (valor mínimo y máximo).
     *
     * @param nombreColumna El nombre de la columna en la que se va a realizar el filtrado.
     * @param valorMin El valor mínimo que debe tener la celda de la columna para que la fila sea incluida.
     * @param valorMax El valor máximo que puede tener la celda de la columna para que la fila sea incluida.
     * @return Una nueva instancia de la clase Tabla, que contiene las filas que cumplen con el criterio de filtrado.
     */
    @Override
    public Tabla filtrarPorRango(String nombreColumna, Comparable<?> valorMin, Comparable<?> valorMax) throws IllegalArgumentException, ClassCastException {
        // Verificar que la columna existe
        Columna<?> columnaFiltrar = null;
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                columnaFiltrar = columna;
                break;
            }
        }
    
        if (columnaFiltrar == null) {
            throw new IllegalArgumentException("La columna '" + nombreColumna + "' no existe en la tabla.");
        }
    
        // Verificar que valorMin y valorMax son del mismo tipo que la columna especificada
        if (!columnaFiltrar.getCeldas().isEmpty()) {
            Object valorEjemplo = columnaFiltrar.getCeldas().get(0).getValor();
            if (!(valorEjemplo instanceof Comparable<?>)) {
                throw new ClassCastException("Los valores de la columna '" + nombreColumna + "' no implementan Comparable.");
            }
    
            if (valorMin != null && valorMax != null) {
                if (!valorEjemplo.getClass().isInstance(valorMin) || !valorEjemplo.getClass().isInstance(valorMax)) {
                    throw new ClassCastException("Los tipos de valorMin y valorMax no coinciden con el tipo de la columna.");
                }
            }
        }
    
        // Crear una nueva tabla para almacenar los resultados filtrados
        Tabla tablaFiltrada = new Tabla("Tabla Filtrada por Rango", obtenerNombresColumnas());
    
        // Iterar sobre cada fila
        for (int i = 0; i < cantFilas; i++) {
            Comparable<Object> valorFila = null;
    
            // Obtener el valor de la celda en la fila actual
            Object valorCelda = columnaFiltrar.getCeldas().get(i).getValor();
            if (valorCelda instanceof Comparable) {
                valorFila = (Comparable<Object>) valorCelda;
            }
    
            // Si el valor de la celda no es nulo y está dentro del rango, agregar la fila a la nueva tabla
            if (valorFila != null && valorFila.compareTo(valorMin) >= 0 && valorFila.compareTo(valorMax) <= 0) {
                List<Object> valoresFila = new ArrayList<>();
                for (Columna<?> columna : columnas) {
                    valoresFila.add(columna.getCeldas().get(i).getValor());
                }
                tablaFiltrada.agregarFila(valoresFila);
            }
        }
    
        return tablaFiltrada;
    }
    
    
        /**
     * Obtiene los nombres de todas las columnas en la tabla.
     *
     * @return Una lista de cadenas que contiene los nombres de todas las columnas.
     */        
    private List<String> obtenerNombresColumnas() {
        List<String> nombres = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            nombres.add(columna.getNombre());
        }
        return nombres;
    }
    /**
     * Lee todas las celdas que contienen valores nulos (NA) en la tabla.
     *
     * @return Una lista de celdas con valores nulos (NA).
     */
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
    /**
     * Calcula el ancho máximo de cada columna en la tabla, basado en el tamaño del contenido más largo.
     *
     * @return Una lista de enteros que representa el ancho máximo de cada columna.
     */
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
    /**
     * Devuelve una fila de la tabla en el índice especificado.
     *
     * @param indice El índice de la fila que se desea obtener.
     * @return Una lista de celdas que conforman la fila en el índice especificado.
     */
    public List<Celda<Object>> devolverFila(int indice){
        List<Celda<Object>> fila = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            for (Celda<?> celda : columna.getCeldas()) {
                if (celda.getIndice() == indice) {
                    fila.add((Celda<Object>) celda);
                }
            }
        }
        return fila;
    }
    /**
     * Muestra el nombre y el contenido de las columnas de la tabla con una alineación adecuada.
     */
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
    /**
     * Muestra las primeras 5 filas de la tabla, con los nombres de columnas y los datos correspondientes.
     * Si la tabla posee menos de 5 filas, se mostraran el total de las fillas que contiene.
     */
    public void head() {
        head(5); // Llama a la versión de head con parámetro 5
    }
    /**
     * Muestra las primeras 'x' filas de la tabla, con los nombres de columnas y los datos correspondientes.
     * Si 'x' es mayor al número de filas disponibles, se ajusta al número máximo de filas.
     *
     * @param x El número de filas que se mostrarán desde el principio de la tabla.
     */
    public void head(int x) {
        x = Math.min(x, cantFilas); // Limitar x al número de filas disponibles
        String[] nombresColumnas = columnas.stream().map(Columna::getNombre).toArray(String[]::new);
        String[][] datos = new String[x][cantColumnas];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < cantColumnas; j++) {
                Object valor = columnas.get(j).getCeldas().get(i).getValor();
                datos[i][j] = valor == null ? "NA" : valor.toString();
            }
        }

        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Head (Primeras " + x + " filas) de " + nombreTabla, tabla);
    }
    /**
     * Muestra las últimas 5 filas de la tabla, con los nombres de columnas y los datos correspondientes..
     * * Si 5 es mayor al número de filas disponibles, se ajusta al número máximo de filas.
     */
    public void tail() {
        tail(5); // Llama a la versión de tail con parámetro 5
    }
    /**
     * Muestra las últimas 'x' filas de la tabla, con los nombres de columnas y los datos correspondientes.
     * Si 'x' es mayor al número de filas disponibles, se ajusta al número máximo de filas.
     *
     * @param x El número de filas que se mostrarán desde el final de la tabla.
     */
    public void tail(int x) {
        x = Math.min(x, cantFilas); // Limitar x al número de filas disponibles
        int startRow = cantFilas - x;
        String[] nombresColumnas = columnas.stream().map(Columna::getNombre).toArray(String[]::new);
        String[][] datos = new String[x][cantColumnas];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < cantColumnas; j++) {
                Object valor = columnas.get(j).getCeldas().get(startRow + i).getValor();
                datos[i][j] = valor == null ? "NA" : valor.toString();
            }
        }

        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Tail (Últimas " + x + " filas) de " + nombreTabla, tabla);
    }
    /**
     * Muestra los datos de las filas dentro del rango especificado, con los nombres de columnas y los datos correspondientes.
     *
     * @param startRow El índice de la primera fila que se mostrará.
     * @param endRow El índice de la última fila que se mostrará.
     */
    public void mostrarDatos(int startRow, int endRow) {
        List<Integer> anchos = calcularAnchoColumnas();
        
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < columnas.size(); j++) {
                System.out.printf("%-" + anchos.get(j) + "s | ", columnas.get(j).getCeldas().get(i).getValor());
            }
            System.out.println();
        }
    }
    /**
     * Muestra una fila específica de la tabla, con los nombres de columnas y los datos correspondientes.
     *
     * @param indice El índice de la fila que se mostrará.
     */
    public void mostrarFila(int indice) {
        String[] nombresColumnas = columnas.stream().map(Columna::getNombre).toArray(String[]::new);
        String[][] datos = new String[1][cantColumnas];

        for (int j = 0; j < cantColumnas; j++) {
            Object valor = columnas.get(j).getCeldas().get(indice).getValor();
            datos[0][j] = valor == null ? "NA" : valor.toString();
        }

        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Fila " + indice + " de " + nombreTabla, tabla);
    }
    /**
     * Muestra toda la tabla, con los nombres de columnas y los datos correspondientes.
     */
    public void mostrarTabla() {
        String[] nombresColumnas = columnas.stream().map(Columna::getNombre).toArray(String[]::new);
        String[][] datos = new String[cantFilas][columnas.size()];

        for (int i = 0; i < cantFilas; i++) {
            for (int j = 0; j < columnas.size(); j++) {
                Object valor = columnas.get(j).getCeldas().get(i).getValor();
                datos[i][j] = valor == null ? "NA" : valor.toString();
            }
        }

        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Tabla Completa: " + nombreTabla, tabla);
    }
    /**
     * Devuelve el número de filas en la tabla.
     *
     * @return El número de filas en la tabla.
     */
    public int getFilas(){
        return cantFilas;
    }
    /**
     * Devuelve la lista de columnas en la tabla.
     *
     * @return Una lista con las columnas de la tabla.
     */
    public List<Columna<?>> getColumnas() {
        return columnas;
    }
    /**
     * Devuelve el número de columnas en la tabla.
     *
     * @return El número de columnas en la tabla.
     */
    public int getCantColumnas() {
        return cantColumnas;
    }
    /**
     * Obtiene el valor de una celda específica dentro de una columna identificada por su nombre.
     *
     * @param indice el índice de la celda dentro de la columna, debe estar en el rango válido.
     * @param nombreColumna el nombre de la columna de la que se quiere obtener el valor.
     *                      No debe ser {@code null} ni una cadena vacía.
     * @return el valor de la celda en la posición indicada dentro de la columna especificada,
     *         o {@code null} si no se encuentra la celda en el índice especificado.
     * @throws IllegalArgumentException si {@code nombreColumna} es {@code null} o vacío, 
     *                                  o si no existe una columna con el nombre dado.
     * @throws IndexOutOfBoundsException si el índice está fuera del rango de celdas de la columna.
     */
    public Object getValor(int indice, String nombreColumna) {
        // Validar que el nombre de la columna no sea null o vacío
        if (nombreColumna == null || nombreColumna.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la columna no puede ser null o vacío.");
        }
        
        // Buscar la columna por nombre
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                // Verificar que el índice esté dentro del rango de celdas
                if (indice < 0 || indice >= columna.getCeldas().size()) {
                    throw new IndexOutOfBoundsException("El índice está fuera del rango de celdas.");
                }
                
                // Buscar la celda con el índice específico
                for (Celda<?> celda : columna.getCeldas()) {
                    if (celda.getIndice() - 1 == indice) {
                        return celda.getValor();
                    }
                }
                // Si no encuentra la celda con el índice específico
                return null;
            }
        }
        
        // Si no encuentra la columna con el nombre especificado
        throw new IllegalArgumentException("La columna con nombre '" + nombreColumna + "' no existe.");
    }    
    
    /**
     * Muestra los nombres de todas las columnas de la tabla en la consola.
     */
    public void nombreColumnas(){
        for (Columna columna : columnas){
            System.out.println(columna.getNombre());
        }
    }
    /**
     * Crea una nueva instancia de la tabla con los valores "NA" reemplazados en todas las columnas.
     * Este método llama al método reemplazarNAs de cada columna de la tabla actual, generando una nueva tabla con estos cambios.
     *
     * @return una nueva instancia de Tabla con los valores "NA" reemplazados en todas las columnas.
     */
    @Override
    public Tabla reemplazarNAs() {
        Tabla tablaNueva = new Tabla(this);
        for (Columna<?> columna : tablaNueva.columnas) {
            columna.reemplazarNAs();
        }
        return tablaNueva;
    }
    /**
     * Crea una nueva instancia de la tabla con los valores "NA" reemplazados en una columna específica.
     * Este método busca una columna por su nombre y, si coincide, llama a reemplazarNAs en esa columna específica,
     * generando una nueva tabla con ese cambio.
     *
     * @param nombreColumna el nombre de la columna en la que se reemplazarán los valores "NA".
     * @return una nueva instancia de Tabla con los valores "NA" reemplazados en la columna especificada.
     */
    @Override
    public Tabla reemplazarNAs(String nombreColumna) {
        Tabla tablaNueva = new Tabla(this);
        for (Columna<?> columna : tablaNueva.columnas) {

            if (nombreColumna.equals(columna.getNombre())) {
                columna.reemplazarNAs();
            }
        }
        return tablaNueva;
    }

    /**
     * Genera una muestra aleatoria de la tabla basada en un porcentaje dado.
     *
     * @param porcentaje Porcentaje de filas a incluir en la muestra (entre 0 y 100).
     * @return Una nueva instancia de Tabla con las filas seleccionadas aleatoriamente.
     * @throws IllegalArgumentException Si el porcentaje no está entre 0 y 100.
     */
    public Tabla muestreoAleatorio(double porcentaje) {
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }

        int totalFilas = cantFilas;
        int filasAMostrar = (int) Math.ceil(totalFilas * (porcentaje / 100.0));//redondeo el num de filas a mostrar

        // Creo una lista con índices de todas las filas
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalFilas; i++) {
            indices.add(i);
        }

        // Mezclo la lista y se seleccionan los primeros filasAMostrar índices
        Collections.shuffle(indices, new Random());
        List<Integer> indicesMuestra = indices.subList(0, filasAMostrar);



        // Crear una nueva tabla con las filas seleccionadas
        List<String> nombresColumnas = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            nombresColumnas.add(columna.getNombre());
        }
        Tabla tablaMuestra = new Tabla(this.nombreTabla + "_muestra", nombresColumnas);
        
        
        for (int indice : indicesMuestra) {
            List<Celda<Object>> fila = devolverFila(indice);
            List <Object> lista = new ArrayList<>();
            for (Celda celda : fila){
                lista.add(celda.getValor());
            }
            tablaMuestra.agregarFila(lista);
        }

        return tablaMuestra;
    }
    /**
     * Exporta la tabla actual a un archivo CSV en la ubicación especificada.
     * Este método crea una nueva instancia de ArchivoCSV, que se encarga de escribir
     * el contenido de la tabla en un archivo en formato CSV.
     *
     * @param rutaDestino la ruta de destino donde se guardará el archivo CSV.
     */
    public void extrarTablaEnCSV(String rutaDestino){
        new ArchivoCSV(this,rutaDestino);
    }
    /**
     * Muestra la tabla en una ventana emergente con un título y una representación de {@code JTable}.
     * Este método crea una nueva ventana que contiene una {@code JTable} dentro de un {@code JScrollPane},
     * permitiendo visualizar los datos de la tabla.
     *
     * @param titulo el título de la ventana.
     * @param tabla el {@code JTable} que contiene los datos de la tabla.
     */
    public void mostrarEnVentana(String titulo, JTable tabla) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(tabla);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
        frame.setVisible(true);
    }
    /**
     * Ordena la tabla actual en función de una lista de columnas y criterios de ordenación, y devuelve una nueva instancia ordenada de {@code Tabla}.
     * Este método utiliza una lista de nombres de columna y una lista de booleanos para determinar el orden ascendente o descendente
     * en cada columna. Luego reorganiza las celdas en cada columna de acuerdo con el orden especificado.
     *
     * @param nombreColumna una lista con los nombres de las columnas por las que se ordenará.
     * @param criteriosAscendentes una lista de booleanos que indica si cada columna se ordena de manera ascendente (true) o descendente (false).
     * @return una nueva instancia de {@code Tabla} con los datos ordenados.
     */
    public Tabla Ordenamiento(List<String> nombreColumna, List<Boolean> criteriosAscendentes) { 
        // Crear una copia de la tabla actual
        Tabla tablanueva = new Tabla(this);
        Ordenamiento ordenar = new Ordenamiento(tablanueva);

        // Obtener el nuevo orden de índices de las filas
        List<Integer> ordenIndices = ordenar.obtenerOrdenIndices(nombreColumna, criteriosAscendentes);

        // Reorganizar las celdas de cada columna según el orden de índices
        for (Columna<?> columna : tablanueva.getColumnas()) {
            List<Celda<?>> celdasOriginales = new ArrayList<>(columna.getCeldas());
            List<Celda<?>> celdasOrdenadas = new ArrayList<>();

            for (int indice : ordenIndices) {
                celdasOrdenadas.add(celdasOriginales.get(indice));
            }

            // Actualizar las celdas de la columna con el nuevo orden
            List<?> celdasColumna = columna.getCeldas();
            for (int i = 0; i < celdasOrdenadas.size(); i++) {
                ((List<Celda<?>>) celdasColumna).set(i, celdasOrdenadas.get(i));
            }
        }

        return tablanueva;
    }

}