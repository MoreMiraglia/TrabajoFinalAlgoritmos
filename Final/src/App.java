import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Tabla nuevaTabla = new Tabla("MiTabla","C://Users//dante//OneDrive//Documentos//ALGO1//TrabajoFinalAlgoritmos//Final//src//prueba1.csv");
        System.out.println("Tabla generada desde Archivo csv:");
        nuevaTabla.mostrarTabla();

        Tabla copiaTabla = new Tabla(nuevaTabla);
        System.out.println("Tabla generada desde Copia Profunda:");
        copiaTabla.mostrarTabla();

        Object[][] datosMatriz = {
            {"Nombre", "Edad", "Ciudad","Activo"}, // Primera fila con nombres de columnas
            {"Alice", 30, "Nueva York",true},
            {"Bob", 25, null,false},
            {null, 35, "Chicago",false}
        };

        // Crear tabla desde Object[][]
        Tabla tablaDesdeMatriz = new Tabla("TablaMatriz", datosMatriz);
        
        System.out.println("Tabla generada desde Object[][]:");
        tablaDesdeMatriz.mostrarTabla();

        List<String> nombresColumnas = List.of("Nombre", "Edad", "Activo");

        // Datos de los empleados en una secuencia lineal
        List<Object> datosLineales = List.of(
            "Ana", 30, true,
            "Luis", 25, false,
            "Maria", 35, true
        );

        // Crear una instancia de la clase Tabla usando el constructor con secuencia lineal
        Tabla tabla = new Tabla("Empleados", nombresColumnas, datosLineales);

        // Mostrar la tabla
        System.out.println("Tabla generada desde secuencia lineal:");
        tabla.mostrarTabla();

        tablaDesdeMatriz.getFilas();
        tablaDesdeMatriz.getColumnas();

        // Implementacion de Limpieza --------------------------------------------------------------
        // Detectar y mostrar las celdas con NA usando la interfaz Limpieza
        System.out.println("\nDetectando valores NA...");
        tablaDesdeMatriz.mostrarNAs();

        // Leer las celdas con NA y mostrar cuántas hay
        List<Celda<Object>> celdasNA = tablaDesdeMatriz.leerNAs();
        System.out.println("\nNúmero de celdas con NA: " + celdasNA.size());
        // ------------------------------------------------------------------------------------------------

        tabla.reasignarValor("Nombre", 0,"Morena");
        tabla.mostrarTabla();
    }
}
