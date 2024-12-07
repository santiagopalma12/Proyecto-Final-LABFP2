public class Entrenador {
    private String nombre;
    private List<Criatura> equipo;
    private List<Criatura> coleccion;

    public Entrenador(String nombre) {
        this.nombre = nombre;
        this.equipo = new ArrayList<>();
        this.coleccion = new ArrayList<>();
    }

    public void capturarCriatura(Criatura criatura) {
        if (equipo.size() < 3) {
            equipo.add(criatura);
        } else {
            System.out.println("No puedes tener más de 3 criaturas en tu equipo.");
        }
    }

    public void agregarAColeccion(Criatura criatura) {
        coleccion.add(criatura);
    }

    public String getNombre() {
        return nombre;
    }

    public List<Criatura> getEquipo() {
        return equipo;
    }

    public List<Criatura> getColeccion() {
        return coleccion;
    }
}
