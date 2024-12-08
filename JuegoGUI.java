import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.*;

public class JuegoGUI extends JFrame {
    private Entrenador entrenador1;
    private Criatura criaturaSeleccionada;
    private Criatura enemigo;
    private JTextArea textArea;
    private JButton btnAtacar, btnSeleccionarCriatura, btnSiguienteTurno, btnGuardarProgreso, btnCapturarPokemon;

    private Entrenador entrenadorRival;
    private ArrayList<Criatura> criaturasVencidas = new ArrayList<>();

    public JuegoGUI() {
        // Configuración inicial
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
        btnCapturarPokemon = new JButton("Capturar Pokémon");
        btnCapturarPokemon.setVisible(false);
    
        panelBotones.add(btnSeleccionarCriatura);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnSiguienteTurno);
        panelBotones.add(btnGuardarProgreso);
        panelBotones.add(btnCapturarPokemon);
        add(panelBotones, BorderLayout.SOUTH);
        JButton btnVerColeccion = new JButton("Ver Colección");
        panelBotones.add(btnVerColeccion);
        btnVerColeccion.addActionListener(e -> mostrarColeccion());

    
        // Inicializar entrenadores
        entrenador1 = new Entrenador("Ash");
        entrenadorRival = new Entrenador("Rival");
    
        // Cargar criaturas y configurar el equipo inicial
        cargarCriaturasDesdeArchivo("criaturas.txt");
        seleccionarEquipoInicial(); // Invocar el menú de selección
        crearEntrenadorRival();
    
        // Configurar acciones de botones
        btnSeleccionarCriatura.addActionListener(e -> seleccionarCriatura());
        btnAtacar.addActionListener(e -> atacar());
        btnSiguienteTurno.addActionListener(e -> siguienteTurno());
        btnGuardarProgreso.addActionListener(e -> guardarProgreso());
        btnCapturarPokemon.addActionListener(e -> capturarPokemon());
    
        actualizarTexto();
    }
    
    
    private int parseIntSeguro(String valor) {
        try {
            return Integer.parseInt(valor);  // Intenta convertir el valor a un número
        } catch (NumberFormatException e) {
            return 0;  // Si falla, asigna un valor por defecto, como 0
        }
    }
    private void cargarCriaturasDesdeArchivo(String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                
                String nombre = datos[0];
                int salud = parseIntSeguro(datos[1]);
                int ataque = parseIntSeguro(datos[2]);
                int defensa = parseIntSeguro(datos[3]);
                String tipo = datos[4];
                String habilidad = datos[5];
                String evolucion = datos[6];
    
                Criatura criatura = new Criatura(nombre, salud, ataque, defensa, tipo, habilidad, evolucion);
                entrenador1.agregarAColeccion(criatura); // Solo agregar a la colección
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo de criaturas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
    private Criatura seleccionarEnemigoDelRival(Criatura criaturaSeleccionada) {
        int puntajeCriaturaSeleccionada = calcularPuntajeCriatura(criaturaSeleccionada);
    
        // Ajustar el rango para una mejor compatibilidad
        int rango = 25;
        List<Criatura> candidatos = entrenadorRival.getEquipo().stream()
                .filter(c -> Math.abs(calcularPuntajeCriatura(c) - puntajeCriaturaSeleccionada) <= rango)
                .collect(Collectors.toList());
    
        if (!candidatos.isEmpty()) {
            Random random = new Random();
            return candidatos.get(random.nextInt(candidatos.size()));
        }
    
        // Si no hay candidatos adecuados, seleccionar el enemigo con puntaje más cercano
        return entrenadorRival.getEquipo().stream()
                .min((c1, c2) -> Integer.compare(
                        Math.abs(calcularPuntajeCriatura(c1) - puntajeCriaturaSeleccionada),
                        Math.abs(calcularPuntajeCriatura(c2) - puntajeCriaturaSeleccionada)))
                .orElse(null);
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
                        textArea.append("\nHas seleccionado a " + criatura.getNombre() + ".\n");
    
                        // Si no hay un enemigo seleccionado, selecciona el primero del equipo rival
                        if (enemigo == null && !entrenadorRival.getEquipo().isEmpty()) {
                            enemigo = entrenadorRival.getEquipo().get(0);
                            textArea.append("El enemigo seleccionado es: " + enemigo.getNombre() + ".\n");
                        }
                        actualizarTexto();
                        return;
                    }
                }
            }
        }
    }
    
    
// Método para calcular el puntaje de una criatura
private int calcularPuntajeCriatura(Criatura criatura) {
    // Calcula el puntaje basado en salud, ataque y defensa, ajusta la fórmula según sea necesario
    return criatura.getSalud() + criatura.getAtaque() + criatura.getDefensa();
}
private void evolucionarCriatura(Criatura criatura) {
    String evolucion = criatura.getEvolucion();
    if (evolucion != null && !evolucion.isEmpty()) {
        // Buscar la criatura evolucionada en el archivo
        try (BufferedReader reader = new BufferedReader(new FileReader("criaturas.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos[0].equalsIgnoreCase(evolucion)) {
                    // Crear la nueva criatura
                    Criatura evolucionada = new Criatura(
                        datos[0],
                        Integer.parseInt(datos[1]),
                        Integer.parseInt(datos[2]),
                        Integer.parseInt(datos[3]),
                        datos[4],
                        datos[5],
                        datos[6]
                    );
                    // Reemplazar la criatura en el equipo
                    entrenador1.getEquipo().remove(criatura);
                    entrenador1.getEquipo().add(evolucionada);
                    textArea.append("\n¡" + criatura.getNombre() + " ha evolucionado a " + evolucionada.getNombre() + "!\n");
                    return;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la evolución", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        textArea.append(criatura.getNombre() + " no tiene evolución definida.\n");
    }
}
private void atacar() {
    if (criaturaSeleccionada != null && enemigo != null) {
        criaturaSeleccionada.atacar(enemigo);
        textArea.append("\n" + criaturaSeleccionada.getNombre() + " ataca a " + enemigo.getNombre() + "!\n");

        if (enemigo.getSalud() <= 0) {
            textArea.append(enemigo.getNombre() + " ha sido derrotado!\n");
            criaturasVencidas.add(enemigo);
            entrenadorRival.getEquipo().remove(enemigo);

            if (entrenadorRival.getEquipo().isEmpty()) {
                textArea.append("¡Has ganado el combate! Todos los Pokémon enemigos han sido derrotados.\n");
                enemigo = null;

                // Mostrar el botón para capturar Pokémon
                btnCapturarPokemon.setVisible(true);
                return;
            }

            enemigo = entrenadorRival.getEquipo().get(0);
            textArea.append("El próximo Pokémon enemigo es: " + enemigo.getNombre() + ".\n");
        }

        if (enemigo != null) {
            enemigo.atacar(criaturaSeleccionada);
            textArea.append("\n" + enemigo.getNombre() + " ataca a " + criaturaSeleccionada.getNombre() + "!\n");

            if (criaturaSeleccionada.getSalud() <= 0) {
                textArea.append(criaturaSeleccionada.getNombre() + " ha sido derrotado!.\n");
                entrenador1.getEquipo().remove(criaturaSeleccionada);

                if (entrenador1.getEquipo().isEmpty()) {
                    textArea.append("¡Has perdido! No quedan más Pokémon en tu equipo.\n");
                    criaturaSeleccionada = null;
                    return;
                }

                textArea.append("Elige otro Pokémon para continuar.\n");
                seleccionarCriatura();
            }
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
        // El enemigo ataca después de que el jugador ha atacado
        if (enemigo != null && criaturaSeleccionada != null) {
            enemigo.atacar(criaturaSeleccionada);
            textArea.append("\n" + enemigo.getNombre() + " ataca a " + criaturaSeleccionada.getNombre() + "!\n");
    
            // Si la salud de la criatura seleccionada llega a 0, se muestra un mensaje de derrota
            if (criaturaSeleccionada.getSalud() <= 0) {
                textArea.append(criaturaSeleccionada.getNombre() + " ha sido derrotado! Has perdido.\n");
            }
    
            // Actualizar el estado de la batalla
            actualizarTexto();
        }
    }

    public void seleccionarYGenerarEquipos() {
        // 1. Mostrar la colección del jugador para que seleccione su equipo.
        System.out.println("Elige tu equipo (hasta 3 Pokémon):");
        mostrarColeccion(entrenador1.getColeccion());
    
        // Leer las elecciones del jugador
        List<Criatura> equipoJugador = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int opcion = obtenerOpcionValida();
            equipoJugador.add(entrenador1.getColeccion().get(opcion - 1));
        }
        entrenador1.setEquipo(equipoJugador);
    
        // 2. Generar un equipo para el enemigo basado en la puntuación del equipo del jugador.
        List<Criatura> equipoEnemigo = generarEquipoEmparejado(entrenadorRival.getColeccion(), equipoJugador);
        entrenadorRival.setEquipo(equipoEnemigo);
    
        // Mostrar equipos
        System.out.println("Tu equipo:");
        mostrarEquipo(entrenador1.getEquipo());
        System.out.println("\nEquipo enemigo:");
        mostrarEquipo(entrenadorRival.getEquipo());
    }
    private void mostrarColeccion(List<Criatura> coleccion) {
        for (int i = 0; i < coleccion.size(); i++) {
            Criatura c = coleccion.get(i);
            System.out.printf("%d. %s (Puntuación: %d)\n", i + 1, c.getNombre(), c.getPuntaje());
        }
    }
    public void crearEntrenadorRival() {
        entrenadorRival = new Entrenador("Rival");
    
        // Generar un equipo balanceado basado en las criaturas disponibles
        List<Criatura> equipoEnemigo = generarEquipoEmparejado(entrenador1.getColeccion(), entrenador1.getEquipo());
        entrenadorRival.setEquipo(equipoEnemigo);
    }

    private List<Criatura> generarEquipoEmparejado(List<Criatura> coleccionRival, List<Criatura> equipoJugador) {
        int promedioJugador = equipoJugador.stream()
                                            .mapToInt(Criatura::getPuntaje)
                                            .sum() / equipoJugador.size();
    
        // Rango fijo de 10 puntos
        int rango = 10;
    
        // Filtrar criaturas dentro de la diferencia de 10 puntos
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
    private void capturarPokemon() {
        if (criaturasVencidas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay Pokémon vencidos para capturar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        String[] nombresCriaturas = new String[criaturasVencidas.size()];
        for (int i = 0; i < criaturasVencidas.size(); i++) {
            nombresCriaturas[i] = criaturasVencidas.get(i).getNombre();
        }
    
        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona un Pokémon para capturar:",
                "Capturar Pokémon",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresCriaturas,
                nombresCriaturas[0]
        );
    
        if (seleccion != null) {
            for (Criatura criatura : criaturasVencidas) {
                if (criatura.getNombre().equals(seleccion)) {
                    entrenador1.agregarAColeccion(criatura);
                    criaturasVencidas.remove(criatura);
                    textArea.append("\n" + criatura.getNombre() + " ha sido capturado y añadido a tu colección.\n");
                    break;
                }
            }
        }
    
        if (criaturasVencidas.isEmpty()) {
            btnCapturarPokemon.setVisible(false);
        }
    
        actualizarTexto();
    }
    private void mostrarColeccion() {
        List<Criatura> coleccion = entrenador1.getColeccion();
    
        if (coleccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tu colección está vacía.", "Colección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        StringBuilder sb = new StringBuilder("Tu colección de Pokémon:\n");
        for (Criatura criatura : coleccion) {
            sb.append("- ").append(criatura.getNombre())
              .append(" | Salud: ").append(criatura.getSalud())
              .append(" | Ataque: ").append(criatura.getAtaque())
              .append(" | Defensa: ").append(criatura.getDefensa())
              .append("\n");
        }
    
        JOptionPane.showMessageDialog(this, sb.toString(), "Colección", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void seleccionarEquipoInicial() {
        List<Criatura> coleccion = entrenador1.getColeccion();
    
        if (coleccion.size() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Tu colección debe tener al menos 3 Pokémon para formar un equipo.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        List<Criatura> equipoSeleccionado = new ArrayList<>();
        while (equipoSeleccionado.size() < 3) {
            // Convertir la colección en un arreglo de nombres
            String[] nombresCriaturas = coleccion.stream()
                                                  .map(Criatura::getNombre)
                                                  .toArray(String[]::new);
    
            String seleccion = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona un Pokémon para tu equipo:",
                    "Seleccionar Pokémon",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    nombresCriaturas,
                    nombresCriaturas[0]
            );
    
            if (seleccion != null) {
                // Buscar la criatura seleccionada
                Criatura criaturaSeleccionada = coleccion.stream()
                                                         .filter(c -> c.getNombre().equals(seleccion))
                                                         .findFirst()
                                                         .orElse(null);
    
                if (criaturaSeleccionada != null) {
                    equipoSeleccionado.add(criaturaSeleccionada);
                    coleccion.remove(criaturaSeleccionada);
                    JOptionPane.showMessageDialog(this, 
                        criaturaSeleccionada.getNombre() + " añadido al equipo.", 
                        "Equipo", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    
        // Asignar el equipo al entrenador
        entrenador1.setEquipo(equipoSeleccionado);
    
        JOptionPane.showMessageDialog(this, 
            "Tu equipo inicial ha sido seleccionado con éxito:\n" + 
            equipoSeleccionado.stream()
                              .map(Criatura::getNombre)
                              .reduce((a, b) -> a + ", " + b)
                              .orElse(""),
            "Equipo Inicial", 
            JOptionPane.INFORMATION_MESSAGE);
    }
       
    private void asignarEquipoInicial() {
        // Crear los Pokémon iniciales
        List<Criatura> iniciales = new ArrayList<>();
        iniciales.add(new Criatura("Charmander", 120, 50, 10, "Fuego", "Lanzallamas", "Charmeleon"));
        iniciales.add(new Criatura("Squirtle", 110, 30, 10, "Agua", "Hydro Pump", "War Tortle"));
        iniciales.add(new Criatura("Bulbasaur", 130, 35, 10, "Planta", "Látigo Cepa", "Ivysaur"));
    
        // Limpiar el equipo y la colección del jugador
        entrenador1.getEquipo().clear();
        entrenador1.getColeccion().clear();
    
        // Agregar los Pokémon iniciales al equipo y a la colección
        for (Criatura inicial : iniciales) {
            entrenador1.capturarCriatura(inicial);
            entrenador1.agregarAColeccion(inicial);
        }
    
        textArea.append("Se ha configurado el equipo inicial:\n");
        for (Criatura criatura : iniciales) {
            textArea.append("- " + criatura.getNombre() + "\n");
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
    
    
    private void guardarProgreso() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("progreso.dat"))) {
            out.writeObject(entrenador1);  // Guardar el entrenador (y sus criaturas)
            JOptionPane.showMessageDialog(this, "Progreso guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el progreso", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProgreso() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("progreso.dat"))) {
            entrenador1 = (Entrenador) in.readObject();  // Cargar el objeto entrenador
            JOptionPane.showMessageDialog(this, "Progreso cargado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el progreso", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}
