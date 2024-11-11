import java.util.ArrayList;
import java.util.List;

/*public class App {
    public static void main(String[] args) throws Exception {
        Tabla tablaGrande = new Tabla("Tabla Grande","C://Users//dante//OneDrive//Documentos//ALGO1//TrabajoFinalAlgoritmos//Final//src//nuevo.csv");
        tablaGrande.mostrarTabla();
        tablaGrande.getTipoDato();

        // Especifica las columnas y el orden para la ordenación
        List<String> columnasOrden = List.of("Edad", "Ciudad");  // Columnas por las que quieres ordenar
        List<Boolean> ordenAscendente = List.of(false, false);     // true para ascendente en "Edad", false para descendente en "Ciudad"

        // Ordenar la tabla
        Tabla tablaOrdenada = new Tabla(tablaGrande.Ordenamiento(columnasOrden, ordenAscendente));

    }


}*/

public class App {
    public static void main(String[] args) {
        // Crear la matriz de datos
        Object[][] datosMatriz = {
            {"Nombre", "Edad", "Ciudad", "Activo"}, // Primera fila con los nombres de las columnas
            {"Alice", 30, "Nueva York", true},
            {"Bob", 25, null, false},
            {"Charlie", 35, "Chicago", false},
            {"David", 40, "Lima", true}
        };

        // Crear la tabla con los datos
        Tabla tabla = new Tabla("Tabla de Datos", datosMatriz);

        // Mostrar la tabla original
        System.out.println("Tabla Original:");
        tabla.mostrarTabla();

        // Implementación de filtrado por columna
        System.out.println("\nFiltrado por columna 'Ciudad' = 'Nueva York':");
        Tabla tablaFiltrada = tabla.filtrarPorColumna("Ciudad", "Nueva York");
        tablaFiltrada.mostrarTabla();

        // Implementación de búsqueda por valor
        System.out.println("\nBúsqueda por valor en la columna 'Edad' = 25:");
        Tabla tablaBusqueda = tabla.buscarValor("Edad", 25);
        tablaBusqueda.mostrarTabla();

        // Implementación de filtrado por rango
        System.out.println("\nFiltrado por rango en la columna 'Edad' entre 30 y 40:");
        Tabla tablaFiltradaRango = tabla.filtrarPorRango("Edad", 30, 40);
        tablaFiltradaRango.mostrarTabla();
    }
}
