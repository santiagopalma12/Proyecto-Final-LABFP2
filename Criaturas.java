public class Criatura {
    private String nombre;
    private int salud;
    private int ataque;
    private int defensa;
    private String tipo;
    private String habilidad;
    private String evolucion;

    public Criatura(String nombre, int salud, int ataque, int defensa, String tipo, String habilidad, String evolucion) {
        this.nombre = nombre;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.tipo = tipo;
        this.habilidad = habilidad;
        this.evolucion = evolucion;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSalud() {
        return salud;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public String getTipo() {
        return tipo;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public String getEvolucion() {
        return evolucion;
    }

    @Override
    public String toString() {
        return nombre + " (Tipo: " + tipo + ", Salud: " + salud + ", Ataque: " + ataque + ", Defensa: " + defensa + ", Habilidad: " + habilidad + ", Evoluciona a: " + evolucion + ")";
    }
}
