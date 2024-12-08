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
    private void seleccionarCriatura() {
    if (entrenador1.getEquipo().size() > 0) {
        String[] nombresCriaturas = new String[entrenador1.getEquipo().size()];
        for (int i = 0; i < entrenador1.getEquipo().size(); i++) {
            nombresCriaturas[i] = entrenador1.getEquipo().get(i).getNombre();
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona tu criatura:",
                "Seleccionar Criatura",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresCriaturas,
                nombresCriaturas[0]
        );

        if (seleccion != null) {
            for (Criatura criatura : entrenador1.getEquipo()) {
                if (criatura.getNombre().equals(seleccion)) {
                    criaturaSeleccionada = criatura;
                    enemigo = seleccionarEnemigoDelRival(criaturaSeleccionada);
                    actualizarTexto();
                    return;
                }
            }
        }
    }
}

private Criatura seleccionarEnemigoDelRival(Criatura criaturaSeleccionada) {
    int puntajeCriaturaSeleccionada = calcularPuntajeCriatura(criaturaSeleccionada);

    int rango = 25;
    List<Criatura> candidatos = entrenadorRival.getEquipo().stream()
            .filter(c -> Math.abs(calcularPuntajeCriatura(c) - puntajeCriaturaSeleccionada) <= rango)
            .collect(Collectors.toList());

    if (!candidatos.isEmpty()) {
        Random random = new Random();
        return candidatos.get(random.nextInt(candidatos.size()));
    }

    return entrenadorRival.getEquipo().stream()
            .min((c1, c2) -> Integer.compare(
                    Math.abs(calcularPuntajeCriatura(c1) - puntajeCriaturaSeleccionada),
                    Math.abs(calcularPuntajeCriatura(c2) - puntajeCriaturaSeleccionada)))
            .orElse(null);
}

private int calcularPuntajeCriatura(Criatura criatura) {
    return criatura.getSalud() + criatura.getAtaque() + criatura.getDefensa();
}

private void atacar() {
    if (criaturaSeleccionada != null && enemigo != null) {
        criaturaSeleccionada.atacar(enemigo);
        textArea.append("\n" + criaturaSeleccionada.getNombre() + " ataca a " + enemigo.getNombre() + "!\n");

        if (enemigo.getSalud() <= 0) {
            textArea.append(enemigo.getNombre() + " ha sido derrotado! Has ganado.\n");
            criaturasVencidas.add(enemigo);
            enemigo = null;
            return;
        }

        enemigo.atacar(criaturaSeleccionada);
        textArea.append("\n" + enemigo.getNombre() + " ataca a " + criaturaSeleccionada.getNombre() + "!\n");

        if (criaturaSeleccionada.getSalud() <= 0) {
            textArea.append(criaturaSeleccionada.getNombre() + " ha sido derrotado!\n");
            entrenador1.getEquipo().remove(criaturaSeleccionada);

            if (entrenador1.getEquipo().isEmpty()) {
                textArea.append("¡Has perdido! No quedan más Pokémon en tu equipo.\n");
            } else {
                textArea.append("Elige otro Pokémon para continuar.\n");
                seleccionarCriatura();
            }

            criaturaSeleccionada = null;
        }

        actualizarTexto();
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}
