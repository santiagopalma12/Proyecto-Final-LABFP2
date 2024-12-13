import javax.swing.JOptionPane;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Pokedex implements Serializable {
    private static final long serialVersionUID = 1L; // Agregar un identificador de versión serial (recomendado)
    
    private List<Criatura> criaturas;

    // Constructor
    public Pokedex() {
        this.criaturas = new ArrayList<>();
    }

    // Método para agregar criaturas a la Pokedex
    public void agregarCriatura(Criatura criatura) {
        if (!criaturas.contains(criatura)) {
            criaturas.add(criatura);
        }
    }

    // Método para mostrar todas las criaturas en la Pokedex
    public void mostrarPokedex() {
        StringBuilder sb = new StringBuilder("Descripción de tus Pokémon:\n");
        
        // Recorrer las criaturas y mostrar su información
        for (Criatura criatura : criaturas) {
            sb.append("- ").append(criatura.getNombre())
              .append(" | Salud: ").append(criatura.getSalud())
              .append(" | Ataque: ").append(criatura.getAtaque())
              .append(" | Defensa: ").append(criatura.getDefensa())
              .append(" | Descripción: ").append(criatura.getDescripcion())
              .append("\n");
        }

        // Mostrar la Pokedex en una ventana emergente
        JOptionPane.showMessageDialog(null, sb.toString(), "Pokedex", JOptionPane.INFORMATION_MESSAGE);
    }
    // Método para obtener la cantidad de criaturas
    public int getCantidadDeCriaturas() {
        return criaturas.size();  // Devuelve el tamaño de la lista de criaturas
    }
    // Método para obtener todas las criaturas
    public List<Criatura> getCriaturas() {
        return criaturas;
    }
}
