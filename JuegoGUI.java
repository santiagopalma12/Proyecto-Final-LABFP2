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
private void elegirCriaturaVencida() {
    if (criaturasVencidas.size() > 0) {
        String[] nombresCriaturas = new String[criaturasVencidas.size()];
        for (int i = 0; i < criaturasVencidas.size(); i++) {
            nombresCriaturas[i] = criaturasVencidas.get(i).getNombre();
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona una criatura vencida para añadir a tu equipo:",
                "Elegir Criatura Vencida",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresCriaturas,
                nombresCriaturas[0]
        );

        if (seleccion != null) {
            for (Criatura criatura : criaturasVencidas) {
                if (criatura.getNombre().equals(seleccion)) {
                    entrenador1.capturarCriatura(criatura);
                    criaturasVencidas.remove(criatura);
                    textArea.append("\n" + criatura.getNombre() + " se ha unido a tu equipo.\n");
                    break;
                }
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "No hay criaturas vencidas para elegir.", "Atención", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void siguienteTurno() {
    if (enemigo != null && criaturaSeleccionada != null) {
        enemigo.atacar(criaturaSeleccionada);
        textArea.append("\n" + enemigo.getNombre() + " ataca a " + criaturaSeleccionada.getNombre() + "!\n");

        if (criaturaSeleccionada.getSalud() <= 0) {
            textArea.append(criaturaSeleccionada.getNombre() + " ha sido derrotado! Has perdido.\n");
        }

        actualizarTexto();
    }
}

private void actualizarTexto() {
    StringBuilder texto = new StringBuilder();
    texto.append("=== Equipo de ").append(entrenador1.getNombre()).append(" ===\n");

    for (Criatura c : entrenador1.getEquipo()) {
        texto.append("- ").append(c.getNombre())
              .append(" | Salud: ").append(c.getSalud())
              .append(" | Ataque: ").append(c.getAtaque())
              .append(" | Defensa: ").append(c.getDefensa())
              .append("\n");
    }

    texto.append("\n=== Equipo de ").append(entrenadorRival.getNombre()).append(" ===\n");

    for (Criatura c : entrenadorRival.getEquipo()) {
        texto.append("- ").append(c.getNombre())
              .append(" | Salud: ").append(c.getSalud())
              .append(" | Ataque: ").append(c.getAtaque())
              .append(" | Defensa: ").append(c.getDefensa())
              .append("\n");
    }

    texto.append("\n=== Estado Actual ===\n");

    if (criaturaSeleccionada != null) {
        texto.append(">> Criatura seleccionada: ").append(criaturaSeleccionada.getNombre())
              .append(" | Salud: ").append(criaturaSeleccionada.getSalud()).append("\n");
    }

    if (enemigo != null) {
        texto.append(">> Enemigo: ").append(enemigo.getNombre())
              .append(" | Salud: ").append(enemigo.getSalud()).append("\n");
    }

    textArea.setText(texto.toString());
}
public void seleccionarYGenerarEquipos() {
    System.out.println("Elige tu equipo (hasta 3 Pokémon):");
    mostrarColeccion(entrenador1.getColeccion());

    List<Criatura> equipoJugador = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
        int opcion = obtenerOpcionValida();
        equipoJugador.add(entrenador1.getColeccion().get(opcion - 1));
    }
    entrenador1.setEquipo(equipoJugador);

    List<Criatura> equipoEnemigo = generarEquipoEmparejado(entrenadorRival.getColeccion(), equipoJugador);
    entrenadorRival.setEquipo(equipoEnemigo);

    System.out.println("Tu equipo:");
    mostrarEquipo(entrenador1.getEquipo());
    System.out.println("\nEquipo enemigo:");
    mostrarEquipo(entrenadorRival.getEquipo());
}

private List<Criatura> generarEquipoEmparejado(List<Criatura> coleccionRival, List<Criatura> equipoJugador) {
    int promedioJugador = equipoJugador.stream()
                                        .mapToInt(Criatura::getPuntaje)
                                        .sum() / equipoJugador.size();

    int rango = 10;
    List<Criatura> posiblesEnemigos = coleccionRival.stream()
        .filter(c -> Math.abs(c.getPuntaje() - promedioJugador) <= rango)
        .collect(Collectors.toList());

    List<Criatura> equipoEnemigo = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 3; i++) {
        Criatura seleccionada = posiblesEnemigos.remove(random.nextInt(posiblesEnemigos.size()));
        equipoEnemigo.add(seleccionada);
    }

    return equipoEnemigo;
}

private void mostrarColeccion(List<Criatura> coleccion) {
    for (int i = 0; i < coleccion.size(); i++) {
        Criatura c = coleccion.get(i);
        System.out.printf("%d. %s (Puntuación: %d)\n", i + 1, c.getNombre(), c.getPuntaje());
    }
}

private void mostrarEquipo(List<Criatura> equipo) {
    for (Criatura c : equipo) {
        System.out.printf("%s | Salud: %d | Ataque: %d | Defensa: %d | Puntuación: %d\n",
                          c.getNombre(), c.getSalud(), c.getAtaque(), c.getDefensa(), c.getPuntaje());
    }
}

private int obtenerOpcionValida() {
    Scanner scanner = new Scanner(System.in);
    int opcion;
    do {
        System.out.print("Ingresa el número de la criatura que quieres seleccionar: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Por favor, ingresa un número válido: ");
            scanner.next();
        }
        opcion = scanner.nextInt();
    } while (opcion < 1 || opcion > entrenador1.getColeccion().size());
    return opcion;
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}
