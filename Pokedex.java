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
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout());
    
        // Panel con imagen de fondo
        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Cargar la imagen de fondo
                ImageIcon fondo = new ImageIcon("Imagenes/fondo3.png");  // Asegúrate de tener la ruta correcta
                g.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), this);  // Dibujar la imagen
            }
        };
        panelFondo.setLayout(new BorderLayout());
    
        // Panel superior con espaciado
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2));
        panelSuperior.setBackground(new Color(0, 0, 0, 0));  // Fondo transparente
        JPanel panelInfoIzquierda = new JPanel(new GridLayout(4, 1));
        panelInfoIzquierda.setBackground(new Color(0, 0, 0, 0));  // Fondo transparente
        JPanel panelImagenDerecha = new JPanel();
        panelImagenDerecha.setBackground(new Color(0, 0, 0, 0));  // Fondo transparente
    
        // Labels para la información básica con espaciado
        lblNombre = new JLabel("Nombre: ");
        lblNombre.setFont(new Font("DialogInput", Font.BOLD, 30));  // Cambiar fuente a DialogInput
        lblNombre.setForeground(Color.WHITE);  // Letras blancas
        lblNombre.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblSalud = new JLabel("Salud: ");
        lblSalud.setFont(new Font("DialogInput", Font.BOLD,20));  // Cambiar fuente a DialogInput
        lblSalud.setForeground(Color.WHITE);  // Letras blancas
        lblSalud.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblAtaque = new JLabel("Ataque: ");
        lblAtaque.setFont(new Font("DialogInput", Font.BOLD, 20));  // Cambiar fuente a DialogInput
        lblAtaque.setForeground(Color.WHITE);  // Letras blancas
        lblAtaque.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblDefensa = new JLabel("Defensa: ");
        lblDefensa.setFont(new Font("DialogInput", Font.BOLD, 20));  // Cambiar fuente a DialogInput
        lblDefensa.setForeground(Color.WHITE);  // Letras blancas
        lblDefensa.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
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
    
        // Panel inferior con espaciado
        JPanel panelInferior = new JPanel(new GridLayout(4, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 50)); // Transparencia
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelInferior.setOpaque(false);
    
        lblTipo = new JLabel("Tipo: ");
        lblTipo.setFont(new Font("DialogInput", Font.BOLD, 14));  // Cambiar fuente a DialogInput
        lblTipo.setForeground(Color.WHITE);  // Letras blancas
        lblTipo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblHabilidad = new JLabel("Habilidad: ");
        lblHabilidad.setFont(new Font("DialogInput", Font.BOLD, 14));  // Cambiar fuente a DialogInput
        lblHabilidad.setForeground(Color.WHITE);  // Letras blancas
        lblHabilidad.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblEvolucion = new JLabel("Evolución: ");
        lblEvolucion.setFont(new Font("DialogInput", Font.BOLD, 14));  // Cambiar fuente a DialogInput
        lblEvolucion.setForeground(Color.WHITE);  // Letras blancas
        lblEvolucion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        lblDescripcion = new JLabel("Descripción: ");
        lblDescripcion.setFont(new Font("DialogInput", Font.BOLD, 14));  // Cambiar fuente a DialogInput
        lblDescripcion.setForeground(Color.WHITE);  // Letras blancas
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        panelInferior.add(lblTipo);
        panelInferior.add(lblHabilidad);
        panelInferior.add(lblEvolucion);
        panelInferior.add(lblDescripcion);
    
        // ComboBox para seleccionar Pokémon con espaciado
        comboBox = new JComboBox<>();
        for (Criatura criatura : pokedex.getCriaturas()) {
            comboBox.addItem(criatura.getNombre());
        }
        comboBox.setFont(new Font("DialogInput", Font.BOLD, 14));  // Cambiar fuente a DialogInput
        comboBox.setForeground(Color.BLACK);  // Letras blancas
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Añadir margen
    
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreSeleccionado = (String) comboBox.getSelectedItem();
                actualizarInformacion(nombreSeleccionado);
            }
        });
    
        // Agregar componentes al panelFondo
        panelFondo.add(comboBox, BorderLayout.NORTH);
        panelFondo.add(panelSuperior, BorderLayout.CENTER);
        panelFondo.add(panelInferior, BorderLayout.SOUTH);
    
        // Agregar el panelFondo al frame
        frame.add(panelFondo);
    
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
            Image imagen = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
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
