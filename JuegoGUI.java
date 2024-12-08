import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class JuegoGUI extends JFrame {
    private Entrenador entrenador1;
    private Criatura criaturaSeleccionada;
    private Criatura enemigo;
    private JTextArea textArea;
    private JButton btnAtacar, btnSeleccionarCriatura, btnSiguienteTurno, btnGuardarProgreso;
    private Entrenador entrenadorRival;
    private ArrayList<Criatura> criaturasVencidas = new ArrayList<>();

    public JuegoGUI() {
        setTitle("Juego de Criaturas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        textArea = new JTextArea();  
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    
        JPanel panelBotones = new JPanel();
        btnSeleccionarCriatura = new JButton("Seleccionar Criatura");
        btnAtacar = new JButton("Atacar");
        btnSiguienteTurno = new JButton("Siguiente Turno");
        btnGuardarProgreso = new JButton("Guardar Progreso");
    
        panelBotones.add(btnSeleccionarCriatura);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnSiguienteTurno);
        panelBotones.add(btnGuardarProgreso);
        add(panelBotones, BorderLayout.SOUTH);
        entrenador1 = new Entrenador("Ash");
        entrenadorRival = new Entrenador("Rival");
    
        // Cargar criaturas y asignar el equipo inicial
        cargarCriaturasDesdeArchivo("criaturas.txt");
        asignarEquipoInicial();
        crearEntrenadorRival();
    
        // Acciones de los botones
        btnSeleccionarCriatura.addActionListener(e -> seleccionarCriatura());
        btnAtacar.addActionListener(e -> atacar());
        btnSiguienteTurno.addActionListener(e -> siguienteTurno());
        btnGuardarProgreso.addActionListener(e -> guardarProgreso());
    }
    private void cargarCriaturasDesdeArchivo(String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                String nombre = datos[0];
                int salud = Integer.parseInt(datos[1]);
                int ataque = Integer.parseInt(datos[2]);
                int defensa = Integer.parseInt(datos[3]);
                String tipo = datos[4];
                String habilidad = datos[5];
                String evolucion = datos[6];
    
                Criatura criatura = new Criatura(nombre, salud, ataque, defensa, tipo, habilidad, evolucion);
                entrenador1.agregarAColeccion(criatura);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo de criaturas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarEquipoInicial() {
        List<Criatura> iniciales = new ArrayList<>();
        iniciales.add(new Criatura("Charmander", 120, 50, 10, "Fuego", "Lanzallamas", "Charmeleon"));
        iniciales.add(new Criatura("Squirtle", 110, 30, 10, "Agua", "Hydro Pump", "War Tortle"));
        iniciales.add(new Criatura("Bulbasaur", 130, 35, 10, "Planta", "Látigo Cepa", "Ivysaur"));
    
        entrenador1.getEquipo().clear();
        for (Criatura inicial : iniciales) {
            entrenador1.capturarCriatura(inicial);
        }
    }

    public void crearEntrenadorRival() {
        entrenadorRival = new Entrenador("Rival");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}
