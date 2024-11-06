import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Tabla implements Manipulacion, Limpieza, Filtro{
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
                celdas.add(new Celda<>(datos[j][i],datos[0][i].toString(),celdas.size()));
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

    //Constructor que genera una tabla concatenada, dada dos tablas iguales
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

    private void actualizarIndices(int indiceEliminado){
        for (Columna columna: columnas){
            columna.actualizarIndices(indiceEliminado);
        }
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
    public Tabla reasignarValor(String nombre,int indice, Object nuevoValor){
        Tabla nuevaTabla = new Tabla(this);
        for (Columna columna : nuevaTabla.columnas){
            if (columna.getNombre().equals(nombre)){
                columna.modificarValor(indice, nuevoValor);
            }
        }
        return nuevaTabla;
    }

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

    public Tabla eliminarFilasConNAs() {
        Tabla nuevaTabla = new Tabla(this);
        List <Celda<Object>> celdasNAs = leerNAs();
        if (celdasNAs.isEmpty()) {
            System.out.println("No hay valores NA en la tabla.");
            return null;
        }
        for (Celda celda:celdasNAs){
            nuevaTabla.eliminarFila(celda.getIndice());
        }
        System.out.println("Se eliminaron " + celdasNAs.size() + " filas con valores NA.");
        return nuevaTabla;
    }

    @Override
    public Tabla eliminarFilasConNAs(String nombreColumna) {
        Tabla nuevaTabla = new Tabla(this);
        int cantEliminadas = 0;
        List <Celda<Object>> celdasNAs = leerNAs();
        if (celdasNAs.isEmpty()) {
            System.out.println("No hay valores NA en la columna" + nombreColumna);
            return null;
        }
        for (Celda celda:celdasNAs){
            if (celda.getNombreColumna().equals(nombreColumna)){
                nuevaTabla.eliminarFila(celda.getIndice());
                cantEliminadas++;
            }
        }
        if (cantEliminadas == 0) {
            System.out.println("No hay valores NA en la columna" + nombreColumna);
            return null;
        }
        System.out.println("Se eliminaron " + cantEliminadas + " filas con valores NA de la columna" + nombreColumna);
        return nuevaTabla;
    }

    // Método para verificar compatibilidad de columnas entre dos tablas
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
    private void copiarFilas(Tabla tabla){
        for (int i = 0; i < tabla.cantFilas; i++) {
            List<Object> fila = new ArrayList<>();
            for (Columna<?> columna : tabla.columnas) {
                fila.add(columna.getCeldas().get(i).getValor());
            }
            this.agregarFila(fila);
        }
    }

    // Método para filtrar por columna
    @Override
    public Tabla filtrarPorColumna(String nombreColumna, Object valor) {
    // Crear una nueva tabla para almacenar los resultados filtrados
    Tabla tablaFiltrada = new Tabla("Tabla Filtrada", obtenerNombresColumnas());

    // Iterar sobre cada fila
    for (int i = 0; i < cantFilas; i++) {
        Object valorFila = null;

        // Encontrar la columna especificada
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                // Obtener el valor de la celda en la fila actual
                valorFila = columna.getCeldas().get(i).getValor();
                break;
            }
        }

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
     // Método para obtener los nombres de las columnas
    private List<String> obtenerNombresColumnas() {
        List<String> nombres = new ArrayList<>();
        for (Columna<?> columna : columnas) {
            nombres.add(columna.getNombre());
        }
        return nombres;
    }
    //Metodo para buscar por valor 

    @Override
    public Tabla buscarValor(String nombreColumna, Object valor) {
    // Crear una nueva tabla para almacenar los resultados encontrados
    Tabla tablaResultado = new Tabla("Resultados de Búsqueda", obtenerNombresColumnas());

    // Iterar sobre cada fila
    for (int i = 0; i < cantFilas; i++) {
        Object valorFila = null;

        // Encontrar la columna especificada
        for (Columna<?> columna : columnas) {
            if (columna.getNombre().equals(nombreColumna)) {
                // Obtener el valor de la celda en la fila actual
                valorFila = columna.getCeldas().get(i).getValor();
                break;
            }
        }

        // Si el valor de la celda no es nulo y coincide con el valor dado, agregar la fila a la nueva tabla
        if (valorFila != null && valorFila.equals(valor)) {
            List<Object> valoresFila = new ArrayList<>();
            for (Columna<?> columna : columnas) {
                valoresFila.add(columna.getCeldas().get(i).getValor());
            }
            tablaResultado.agregarFila(valoresFila);
        }
    }

    return tablaResultado;
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
    private void mostrarColumnas() {
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
        head(5); // Llama a la versión de head con parámetro 5
    }
    
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

    public void tail() {
        tail(5); // Llama a la versión de tail con parámetro 5
    }
    
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


    public void mostrarDatos() {
        // Obtener los nombres de las columnas en el orden original
        List<String> nombresColumnas = new ArrayList<>(columnas.keySet());
    
        // Calcular la cantidad de filas (asumiendo que todas las columnas tienen la misma cantidad de filas)
        int numFilas = columnas.get(nombresColumnas.get(0)).size();
    
        // Mostrar cada fila de datos
        for (int i = 0; i < numFilas; i++) {
            for (String nombreColumna : nombresColumnas) {
                String valor = columnas.get(nombreColumna).get(i);
                System.out.print((valor != null ? valor : "null") + "\t");
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

    public void mostrarTabla() {
        String[] nombresColumnas = columnas.stream().map(Columna::getNombre).toArray(String[]::new);
        String[][] datos = new String[cantFilas][cantColumnas];
    
        for (int i = 0; i < cantFilas; i++) {
            for (int j = 0; j < cantColumnas; j++) {
                Object valor = columnas.get(j).getCeldas().get(i).getValor();
                datos[i][j] = valor == null ? "NA" : valor.toString();
            }
        }
    
        JTable tabla = new JTable(datos, nombresColumnas);
        mostrarEnVentana("Tabla Completa: " + nombreTabla, tabla);
    }

    public int getFilas(){
        return cantFilas;
    }
    public List<Columna<?>> getColumnas() {
        return columnas;
    }
    public int getCantColumnas(){
        return cantColumnas;
    }
    public void nombreColumnas(){
        for (Columna columna : columnas){
            System.out.println(columna.getNombre());
        }
    }
    @Override
    public Tabla reemplazarNAs() {
        Tabla tablaNueva = new Tabla(this);
        for (Columna<?> columna : tablaNueva.columnas) {
            columna.reemplazarNAs();
        }
        return tablaNueva;
    }

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

    //metodo para crear la tabla de MUESTREO
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
    public void extrarTablaEnCSV(String rutaDestino){
        new ArchivoCSV(this,rutaDestino);
    }
    
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
}