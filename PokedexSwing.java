import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

@SuppressWarnings("unused")
public class PokedexSwing {

    private JFrame frame;
    private JComboBox<String> comboBox;
    private JLabel lblNombre;
    private JLabel lblSalud;
    private JLabel lblAtaque;
    private JLabel lblDefensa;
    private JLabel lblTipo;
    private JLabel lblHabilidad;
    private JLabel lblEvolucion;
    private JLabel lblDescripcion;
    private JLabel lblImagen;
    private Pokedex pokedex;

    public PokedexSwing(Pokedex pokedex) {
        this.pokedex = pokedex;
        initialize();
    }

    private void initialize() {
        // Configuración de la ventana principal
        frame = new JFrame("Pokédex");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 350);
        frame.setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2));
        JPanel panelInfoIzquierda = new JPanel(new GridLayout(4, 1));
        JPanel panelImagenDerecha = new JPanel();

        // Labels para la información básica
        lblNombre = new JLabel("Nombre: ");
        lblSalud = new JLabel("Salud: ");
        lblAtaque = new JLabel("Ataque: ");
        lblDefensa = new JLabel("Defensa: ");

        // Añadir labels al panel izquierdo
        panelInfoIzquierda.add(lblNombre);
        panelInfoIzquierda.add(lblSalud);
        panelInfoIzquierda.add(lblAtaque);
        panelInfoIzquierda.add(lblDefensa);

        // Imagen del Pokémon
        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        panelImagenDerecha.add(lblImagen);

        // Agregar paneles al panel superior
        panelSuperior.add(panelInfoIzquierda);
        panelSuperior.add(panelImagenDerecha);

        // Panel inferior
        JPanel panelInferior = new JPanel(new GridLayout(4, 1));

        lblTipo = new JLabel("Tipo: ");
        lblHabilidad = new JLabel("Habilidad: ");
        lblEvolucion = new JLabel("Evolución: ");
        lblDescripcion = new JLabel("Descripción: ");

        panelInferior.add(lblTipo);
        panelInferior.add(lblHabilidad);
        panelInferior.add(lblEvolucion);
        panelInferior.add(lblDescripcion);

        // ComboBox para seleccionar Pokémon
        comboBox = new JComboBox<>();
        for (Criatura criatura : pokedex.getCriaturas()) {
            comboBox.addItem(criatura.getNombre());
        }

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreSeleccionado = (String) comboBox.getSelectedItem();
                actualizarInformacion(nombreSeleccionado);
            }
        });

        // Agregar componentes al frame
        frame.add(comboBox, BorderLayout.NORTH);
        frame.add(panelSuperior, BorderLayout.CENTER);
        frame.add(panelInferior, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void actualizarInformacion(String nombre) {
        Criatura criatura = obtenerCriaturaPorNombre(nombre);
        if (criatura != null) {
            lblNombre.setText("Nombre: " + criatura.getNombre());
            lblSalud.setText("Salud: " + criatura.getSalud());
            lblAtaque.setText("Ataque: " + criatura.getAtaque());
            lblDefensa.setText("Defensa: " + criatura.getDefensa());
            lblTipo.setText("Tipo: " + criatura.getTipo());
            lblHabilidad.setText("Habilidad: " + criatura.getHabilidad());
            lblEvolucion.setText("Evolución: " + criatura.getEvolucion());
            lblDescripcion.setText("Descripción: " + criatura.getDescripcion());

            // Actualizar imagen
            ImageIcon icon = new ImageIcon(criatura.getRutaImagen());
            Image imagen = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(imagen));
        }
    }

    private Criatura obtenerCriaturaPorNombre(String nombre) {
        for (Criatura criatura : pokedex.getCriaturas()) {
            if (criatura.getNombre().equalsIgnoreCase(nombre)) {
                return criatura;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Pokedex pokedex = new Pokedex();
        // Cargar datos en la Pokédex aquí
        new PokedexSwing(pokedex);
    }
}
