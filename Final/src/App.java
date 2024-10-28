public class App {
    public static void main(String[] args) throws Exception {
        Tabla nuevaTabla = new Tabla("MiTabla","/home/laura/Escritorio/Algo1/TrabajoFinalAlgoritmos/Final/src/prueba1.csv");
        //nuevaTabla.mostrarTabla();
        Tabla copiaTabla = new Tabla(nuevaTabla);
        copiaTabla.mostrarTabla();
    }
}
