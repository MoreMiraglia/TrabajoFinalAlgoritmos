public class App {
    public static void main(String[] args) throws Exception {
        Tabla nuevaTabla = new Tabla("MiTabla","D://Users//DANTE//Documents//ALGO 1//TrabajoFinalAlgoritmos//Final//src//prueba1.csv");
        System.out.println("Tabla generada desde Archivo csv:");
        nuevaTabla.mostrarTabla();
        Tabla copiaTabla = new Tabla(nuevaTabla);
        System.out.println("Tabla generada desde Copia Profunda:");
        copiaTabla.mostrarTabla();

        Object[][] datosMatriz = {
            {"Nombre", "Edad", "Ciudad","Activo"}, // Primera fila con nombres de columnas
            {"Alice", 30, "Nueva York",true},
            {"Bob", 25, null,false},
            {"Charlie", 35, "Chicago",false}
        };

        // Crear tabla desde Object[][]
        Tabla tablaDesdeMatriz = new Tabla("TablaMatriz", datosMatriz);
        
        System.out.println("Tabla generada desde Object[][]:");
        tablaDesdeMatriz.mostrarTabla();
    }
}
