import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Tabla {
    private String nombreTabla;
    private List<Columna<?>> columnas;

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
}