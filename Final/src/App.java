import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Tabla curso1 = new Tabla("Curso 1", "D://Users//DANTE//Documents//ALGO 1//TrabajoFinalAlgoritmos//Final//src//curso1.csv");
        Tabla curso2 = new Tabla("Curso 2", "D://Users//DANTE//Documents//ALGO 1//TrabajoFinalAlgoritmos//Final//src//curso2.csv");

        Tabla alumnos = new Tabla("Alumnos",curso1,curso2);
        //alumnos.mostrarNAs();
        
        Tabla alumnos2 = alumnos.eliminarFilasConNAs("Promedio");
        //alumnos2.mostrarTabla();

        Tabla alumnos_asistencia = alumnos2.filtrarPorRango("Asistencias", 75, 100);
       // alumnos_asistencia.mostrarTabla();
        
        Tabla alumnos_promedios = alumnos_asistencia.Ordenamiento(List.of("Promedio","Nombre"),List.of(false,true));
        //alumnos_promedios.mostrarTabla();

        Tabla alumnos_borrar = alumnos_promedios.eliminarColumna("Genero");
        //alumnos_borrar.mostrarTabla();

        String fechaTexto = (String) alumnos_borrar.getValor(7, "Fecha_Nacimiento");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fecha = LocalDate.parse(fechaTexto, formato);
        LocalDate fechaActual = LocalDate.now();
        System.out.println(fecha);
        Double edad = (double) fechaActual.getYear() - fecha.getYear();
        System.out.println(edad);
        
        Tabla alumnos_reasginar = new Tabla(alumnos_borrar.reasignarValor("Edad", 8, edad));
        alumnos_reasginar.mostrarTabla();

        Tabla alumnos_reasignar2 = new Tabla(alumnos_borrar.reasignarValor("Fecha_Nacimiento", 8, "pepe"));
        alumnos_reasignar2.mostrarTabla();


        
    }
}
