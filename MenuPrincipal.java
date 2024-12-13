import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MenuPrincipal extends JFrame {
    private JButton btnNuevaPartida;
    private JButton btnCargarPartida;
    private JLabel lblLogoPokemon;

    public MenuPrincipal() {
        setTitle("Menú Principal");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana
        setLayout(new BorderLayout());

        // Cargar el logo de Pokémon (ruta relativa dentro del proyecto)
        try {
            ImageIcon iconoLogo = new ImageIcon(getClass().getResource("/Imagenes/pokemon_logo.png"));
            Image imagenEscalada = iconoLogo.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            lblLogoPokemon = new JLabel(new ImageIcon(imagenEscalada));
        } catch (Exception e) {
            lblLogoPokemon = new JLabel("Logo no disponible");
        }

        lblLogoPokemon.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblLogoPokemon, BorderLayout.CENTER);

        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 1, 10, 10));

        // Botón Nueva Partida
        btnNuevaPartida = new JButton("Nueva Partida");
        btnNuevaPartida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cerrar la ventana del menú principal
                JuegoGUI juego = new JuegoGUI(); // Crear la instancia del juego
                juego.setVisible(true); // Mostrar la ventana del juego
            }
        });
        panelBotones.add(btnNuevaPartida);

        // Botón Cargar Partida
        btnCargarPartida = new JButton("Cargar Partida");
        btnCargarPartida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar archivo de guardado");
                int result = fileChooser.showOpenDialog(MenuPrincipal.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File archivoGuardado = fileChooser.getSelectedFile();
                    cargarPartida(archivoGuardado);
                }
            }
        });
        panelBotones.add(btnCargarPartida);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarPartida(File archivo) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            Entrenador entrenadorCargado = (Entrenador) in.readObject();
            System.out.println("Partida cargada: " + entrenadorCargado.getNombre());
    
            // Restaurar la Pokédex y otros valores
            Pokedex pokedexCargada = (Pokedex) in.readObject();
            boolean enBatalla = in.readBoolean();
            String estadoBatalla = (String) in.readObject();
            Criatura criaturaActiva = (Criatura) in.readObject();
            boolean esTurnoDelEntrenador = in.readBoolean();
            int pocionesCuracion = in.readInt();
    
            // Rellenar el estado del entrenador con los valores cargados
            entrenadorCargado.setPokedex(pokedexCargada);
            entrenadorCargado.setEnBatalla(enBatalla);
            entrenadorCargado.setEstadoBatalla(estadoBatalla);
            entrenadorCargado.setCriaturaActiva(criaturaActiva);
            entrenadorCargado.setEsTurnoDelEntrenador(esTurnoDelEntrenador);
            entrenadorCargado.setPocionesCuracion(pocionesCuracion);
    
            // Verificar si la batalla está en curso y continuar la lógica de batalla
            if (enBatalla) {
                continuarBatalla(entrenadorCargado);
            } else {
                JOptionPane.showMessageDialog(this, "La batalla no está en curso.", "Batalla no iniciada", JOptionPane.INFORMATION_MESSAGE);
            }
    
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la partida: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public void continuarBatalla(Entrenador entrenador) {
        if ("en curso".equals(entrenador.getEstadoBatalla())) {
            System.out.println("Continuando la batalla con " + entrenador.getCriaturaActiva().getNombre());
    
            // Verificar si es el turno del entrenador
            if (entrenador.isEsTurnoDelEntrenador()) {
                System.out.println("Es tu turno, " + entrenador.getNombre() + ". ¿Qué acción deseas tomar?");

            } else {
                System.out.println("Es el turno del rival.");
              
            }
    
            // Si la criatura activa ha sido derrotada, termina la batalla
            if (entrenador.getCriaturaActiva().getSalud() <= 0) {
                System.out.println(entrenador.getCriaturaActiva().getNombre() + " ha sido derrotada.");
            } else {
                // Cambiar de turno al rival
                entrenador.setEsTurnoDelEntrenador(!entrenador.isEsTurnoDelEntrenador());
            }
        } else {
            System.out.println("La batalla no está en curso.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MenuPrincipal().setVisible(true);
            }
        });
    }
}