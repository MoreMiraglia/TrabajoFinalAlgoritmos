import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivoCSV {
    private Map<String, List<Object>> columnas;

    // Constructor que recibe la ruta del archivo y carga los datos
    public ArchivoCSV(String archivoCSV) {
        columnas = new HashMap<>();
        cargarDatos(archivoCSV);
    }
    public Map<String, List<Object>> getMap (){
        return columnas;
    } 
    // Método para cargar datos desde el archivo CSV
    private void cargarDatos(String archivoCSV) {
        String linea;
        String separador = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivoCSV), "UTF-8"))) {
            // Leer la primera línea para obtener los nombres de las columnas
            if ((linea = br.readLine()) != null) {
                String[] nombresColumnas = linea.split(separador);

                // Inicializar una lista vacía para cada columna
                for (String nombreColumna : nombresColumnas) {
                    columnas.put(nombreColumna, new ArrayList<>());
                }
            }

            // Leer cada línea de datos y almacenarla en la lista correspondiente
            while ((linea = br.readLine()) != null) {
                String[] datosFila = linea.split(separador);
 
                int i = 0;
                for (String nombreColumna : columnas.keySet()) {
                    if (i < datosFila.length) {
                        columnas.get(nombreColumna).add(convertirDato(datosFila[i]));
                    } else {
                        columnas.get(nombreColumna).add(null); // Manejo de valores faltantes
                    }
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para convertir cada dato al tipo adecuado
    private Object convertirDato(String dato) {
        if (dato.isEmpty()) {
            return null; // Valor nulo
        }

        // Intentar convertir a boolean
        if (dato.equalsIgnoreCase("true") || dato.equals("1")) {
            return true;
        } else if (dato.equalsIgnoreCase("false") || dato.equals("0")) {
            return false;
        }
        // Intentar convertir a boolean tmb en mayus
        if (dato.equalsIgnoreCase("TRUE") || dato.equals("1")) {
            return true;
        } else if (dato.equalsIgnoreCase("FALSE") || dato.equals("0")) {
            return false;
        }

        // Intentar convertir a Double o Integer
        try {
            if (dato.contains(".")) {
                return Double.parseDouble(dato); // Intenta como Double
            }
            return Integer.parseInt(dato); // Intenta como Integer
        } catch (NumberFormatException e) {
            return dato; // Si no es numérico ni booleano, devolver como String
        }
    }

    // Método para obtener los datos de una columna específica
    public List<Object> getColumna(String nombreColumna) {
        return columnas.getOrDefault(nombreColumna, new ArrayList<>());
    }

// Método para imprimir los datos de cada columna con formato (para pruebas)
public void imprimirColumnasConFormato() {
    // Determinar el ancho máximo de cada columna
    Map<String, Integer> anchoColumnas = new HashMap<>();
    for (Map.Entry<String, List<Object>> entry : columnas.entrySet()) {
        String nombreColumna = entry.getKey();
        int maxAncho = nombreColumna.length();
        for (Object dato : entry.getValue()) {
            maxAncho = Math.max(maxAncho, dato != null ? dato.toString().length() : 4); // "null" tiene 4 caracteres
        }
        anchoColumnas.put(nombreColumna, maxAncho);
    }

    // Imprimir encabezados de columnas con formato
    for (String nombreColumna : columnas.keySet()) {
        System.out.printf("%-" + anchoColumnas.get(nombreColumna) + "s | ", nombreColumna);
    }
    System.out.println();

    // Línea separadora
    for (String nombreColumna : columnas.keySet()) {
        System.out.print("-".repeat(anchoColumnas.get(nombreColumna)) + "-+-");
    }
    System.out.println();

    // Imprimir datos de cada fila con formato
    int numFilas = columnas.values().stream().findFirst().orElse(new ArrayList<>()).size();
    for (int i = 0; i < numFilas; i++) {
        for (String nombreColumna : columnas.keySet()) {
            List<Object> datosColumna = columnas.get(nombreColumna);
            String dato = i < datosColumna.size() && datosColumna.get(i) != null
                    ? datosColumna.get(i).toString()
                    : "null";
            System.out.printf("%-" + anchoColumnas.get(nombreColumna) + "s | ", dato);
        }
        System.out.println();
    }
}

}