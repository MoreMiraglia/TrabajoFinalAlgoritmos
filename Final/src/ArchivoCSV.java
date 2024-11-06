import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArchivoCSV {
    private Map<String, List<Object>> columnas;

    // Constructor que recibe la ruta del archivo y carga los datos
    public ArchivoCSV(String archivoCSV) {
        columnas = new LinkedHashMap<>();
        cargarDatos(archivoCSV);
    }

    // Constructor alternativo para guardar una Tabla en un archivo CSV
    public ArchivoCSV(Tabla tabla, String rutaDestino) {
        guardarTablaEnCSV(tabla, rutaDestino);
    }

    public Map<String, List<Object>> getMap() {
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
        if (dato.isEmpty() || dato.equalsIgnoreCase("NA")) { // Validación para "NA" y vacío
            return null; // Valor nulo para valores vacíos o "NA"
        }

        // Intentar convertir a boolean
        if (dato.equalsIgnoreCase("true") || dato.equals("1")) {
            return true;
        } else if (dato.equalsIgnoreCase("false") || dato.equals("0")) {
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

    // Método para guardar una Tabla en un archivo CSV
    public void guardarTablaEnCSV(Tabla tabla, String archivoDestino) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoDestino))) {
            // Escribir la cabecera con los nombres de las columnas
            for (int i = 0; i < tabla.getColumnas().size(); i++) {
                writer.write(tabla.getColumnas().get(i).getNombre());
                if (i < tabla.getColumnas().size() - 1) {
                    writer.write(","); // Agregar coma entre nombres de columnas
                }
            }
            writer.newLine(); // Nueva línea después de la cabecera

            // Escribir cada fila de datos
            for (int fila = 0; fila < tabla.getFilas(); fila++) {
                for (int col = 0; col < tabla.getColumnas().size(); col++) {
                    Columna<?> columna = tabla.getColumnas().get(col);
                    Object valor = columna.getCeldas().get(fila).getValor();
                    writer.write(valor != null ? valor.toString() : ""); // Manejo de valores nulos

                    if (col < tabla.getColumnas().size() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
