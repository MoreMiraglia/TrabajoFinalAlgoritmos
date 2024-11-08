import java.util.ArrayList;
import java.util.List;

public class App {
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


}