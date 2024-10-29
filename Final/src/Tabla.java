import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Tabla {
    private String nombreTabla;
    private List<Columna<?>> columnas;
    private int cantColumnas;
    private int cantFilas;


    public Tabla(String nombreTabla, String rutaArchivo) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        ArchivoCSV archivoCSV = new ArchivoCSV(rutaArchivo);
        cargarDatosTabla(archivoCSV);
    }

    public Tabla(String nombreTabla, List<String> nombresColumnas) {
        this.nombreTabla = nombreTabla;
        this.columnas = new ArrayList<>();
        for (String nombreColumna : nombresColumnas) {
            columnas.add(new Columna<>(nombreColumna));
        }
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
                celdas.add(new Celda<>(datos[j][i]));
            }
            columnas.add(new Columna<>(datos[0][i].toString(), celdas));
        }
    }

    // Constructor de la copia de la tabla original. (Constructor de copia profunda)
    public Tabla(Tabla otraTabla) {
        this.nombreTabla = otraTabla.nombreTabla;
        this.columnas = new ArrayList<>();
        for (Columna<?> columna : otraTabla.columnas) {
            this.columnas.add(new Columna<>(columna));
        }
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
            columna.addCelda(new Celda<>(datosLineales.get(i)));
        }
    }

    public void cargarDatosTabla(ArchivoCSV archivoCSV) {
        Map<String, List<Object>> datos = archivoCSV.getMap();
        for (Map.Entry<String, List<Object>> entry : datos.entrySet()) {
            List<Celda<Object>> celdas = new ArrayList<>();
            for (Object valor : entry.getValue()) {
                celdas.add(new Celda<>(valor));
            }
            Columna<Object> nuevaColumna = new Columna<>(entry.getKey(), celdas);
            columnas.add(nuevaColumna);
        }
        this.cantColumnas = columnas.size();
        this.cantFilas = columnas.get(0).getCeldas().size(); 


    }
    
    public void addColumna(Columna<?> columna) {
        columnas.add(columna);
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
        for (int i = 0; i < columnas.size(); i++) {
            System.out.printf("%-" + anchos.get(i) + "s | ", columnas.get(i).getNombre());
        }
        System.out.println();
        
        for (int ancho : anchos) {
            System.out.print("-".repeat(ancho) + "-+-");
        }
        System.out.println();
    }

    public void mostrarDatos() {
        List<Integer> anchos = calcularAnchoColumnas();
        int maxFilas = columnas.get(0).getCeldas().size();
        for (int i = 0; i < maxFilas; i++) {
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
}