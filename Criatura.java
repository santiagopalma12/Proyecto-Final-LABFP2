import java.io.Serializable;

public class Criatura implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int salud;
    private int ataque;
    private int defensa;
    private String tipo;
    private String habilidad;
    private String evolucion;
    private String rutaImagen;
    private int combatesGanados;
    private String descripcion;
    
    // Nuevo atributo para almacenar el puntaje de la criatura
    private int puntaje;

    // Constructor original
    public Criatura(String nombre, int salud, int ataque, int defensa, String tipo, String habilidad, String evolucion, String rutaImagen, String descripcion) {
        this.nombre = nombre;
        this.salud = salud;
        this.ataque = ataque;
        this.defensa = defensa;
        this.tipo = tipo;
        this.habilidad = habilidad;
        this.evolucion = evolucion;
        this.rutaImagen = rutaImagen;
        this.combatesGanados = 0;
        this.descripcion = descripcion;
    }

    // Métodos getter y setter para los atributos
    public void curar(int cantidad) {
        salud = Math.min(salud + cantidad, 100); // Salud máxima de 100
    }

    public int getCombatesGanados() {
        return combatesGanados;
    }

    public void incrementarCombatesGanados() {
        combatesGanados++;
    }

    public String getRutaImagen() {
        return rutaImagen;
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

    public int getPuntaje() {
        return puntaje;
    }
    public void calcularPuntaje() {
        this.puntaje = salud + ataque + defensa;
    }
    // Getters y setters para el nuevo campo
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    

    // Método setter para el puntaje
    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
    @Override
    public String toString() {
        return nombre + " (Tipo: " + tipo + ", Salud: " + salud + ", Ataque: " + ataque + ", Defensa: " + defensa + ", Habilidad: " + habilidad + ", Evoluciona a: " + evolucion + ")";
    }
    public void atacar(Criatura objetivo) {
        // El daño es calculado según el ataque de esta criatura menos la defensa del objetivo
        int daño = this.ataque - objetivo.getDefensa();
        if (daño > 0) {
            objetivo.recibirDaño(daño);
            System.out.println(this.nombre + " ataca a " + objetivo.getNombre() + " causando " + daño + " de daño.");
        } else {
            System.out.println(this.nombre + " ataca a " + objetivo.getNombre() + " pero no causa daño.");
        }
    }

    // Método para recibir daño
    public void recibirDaño(int daño) {
        this.salud -= daño;
        if (this.salud < 0) {
            this.salud = 0; // Evitar que la salud sea negativa
        }
        System.out.println(this.nombre + " ahora tiene " + this.salud + " de salud.");
    }


}


