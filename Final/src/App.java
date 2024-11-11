import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Tabla tablaGrande = new Tabla("Tabla Grande", "D://Users//DANTE//Documents//ALGO 1//TrabajoFinalAlgoritmos//Final//src//nuevo.csv");
        
        tablaGrande.agregarFila(List.of("Pepe",20,"Buenos Aires",false));
        tablaGrande.agregarFila(List.of("Manuel",20,"Santa fe",true));
        tablaGrande.agregarFila(List.of("Facu",20,"Cordoba",true));

        tablaGrande.head();

        
    }
}
