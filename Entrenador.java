import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entrenador implements Serializable {
    private String nombre;
    private List<Criatura> equipo;
    private List<Criatura> coleccion;
    private Pokedex pokedex; // Instancia de la Pokedex
    private int pocionesCuracion = 5; // Número de pociones de curación disponibles

    // Estado de la batalla
    private boolean enBatalla; // Si está en batalla o no
    private String estadoBatalla; // Puede ser "en curso", "terminada", etc.
    private Criatura criaturaActiva; // Criatura activa en la batalla
    private boolean esTurnoDelEntrenador; // Indica si es el turno del entrenador

    // Constructor
    public Entrenador(String nombre) {
        this.nombre = nombre;
        this.equipo = new ArrayList<>();
        this.coleccion = new ArrayList<>();
        this.pokedex = new Pokedex(); // Inicializar la Pokédex
        this.enBatalla = false;
        this.estadoBatalla = "no iniciada"; // La batalla no ha comenzado
        this.esTurnoDelEntrenador = true; // El entrenador empieza su turno por defecto
    }

    // Métodos relacionados con pociones
    public boolean usarPocionCuracion(Criatura criatura, int cantidad) {
        if (pocionesCuracion >= cantidad) {
            int totalCuracion = cantidad * 20;
            criatura.curar(totalCuracion);
            pocionesCuracion -= cantidad;
            System.out.println(nombre + " usó " + cantidad + " pociones de curación. Pociones restantes: " + pocionesCuracion);
            return true;
        } else {
            System.out.println("No tienes suficientes pociones de curación.");
            return false;
        }
    }

    // Métodos para manejar criaturas
    public void capturarCriatura(Criatura criatura) {
        if (equipo.size() < 3) {
            equipo.add(criatura);
        } else {
            System.out.println("No puedes tener más de 3 criaturas en tu equipo.");
        }
    }

    public void agregarAColeccion(Criatura criatura) {
        coleccion.add(criatura);
        pokedex.agregarCriatura(criatura); // Agregar la criatura a la Pokédex
    }

    // Métodos relacionados con la batalla
    public void iniciarBatalla() {
        if (!equipo.isEmpty()) {
            this.criaturaActiva = equipo.get(0); // Asume que el primer Pokémon en el equipo es el activo
            this.enBatalla = true;
            this.estadoBatalla = "en curso";
            System.out.println("La batalla comenzó con " + criaturaActiva.getNombre());
        }
    }

    public void terminarBatalla() {
        this.estadoBatalla = "terminada";
        System.out.println("La batalla ha terminado.");
    }

    // Setters y Getters
    public void setEquipo(List<Criatura> equipo) {
        this.equipo = equipo;
    }

    public List<Criatura> getEquipo() {
        return equipo;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Criatura> getColeccion() {
        return coleccion;
    }

    public int getPocionesCuracion() {
        return pocionesCuracion;
    }

    public Pokedex getPokedex() {
        return pokedex;
    }

    // Métodos para manejar la batalla
    public boolean isEnBatalla() {
        return enBatalla;
    }

    public void setEnBatalla(boolean enBatalla) {
        this.enBatalla = enBatalla;
    }

    public String getEstadoBatalla() {
        return estadoBatalla;
    }

    public void setEstadoBatalla(String estadoBatalla) {
        this.estadoBatalla = estadoBatalla;
    }

    public Criatura getCriaturaActiva() {
        return criaturaActiva;
    }

    public void setCriaturaActiva(Criatura criaturaActiva) {
        this.criaturaActiva = criaturaActiva;
    }
    
    public void setPocionesCuracion(int pocionesCuracion) {
        this.pocionesCuracion = pocionesCuracion;
    }
    
    public void setPokedex(Pokedex pokedex) {
        this.pokedex = pokedex;
    }
    
    public boolean isEsTurnoDelEntrenador() {
        return esTurnoDelEntrenador;
    }

    public void setEsTurnoDelEntrenador(boolean esTurnoDelEntrenador) {
        this.esTurnoDelEntrenador = esTurnoDelEntrenador;
    }
}