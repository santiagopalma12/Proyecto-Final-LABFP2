import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.*;
import javafx.stage.Stage;
public class JuegoGUI extends JFrame {
    private Entrenador entrenador1;
    private Criatura criaturaSeleccionada;
    private Criatura enemigo;
    private JTextArea textArea;
    private JButton btnAtacar, btnSeleccionarCriatura, btnGuardarProgreso, btnCapturarPokemon;
    private Entrenador entrenadorRival;
    private ArrayList<Criatura> criaturasVencidas = new ArrayList<>();
    private JLabel imagenCriaturaSeleccionada;
    private JLabel imagenEnemigo;
    private JLabel imagenVersus;
    private static final String RUTA_IMAGENES = "Imagenes/";
    private PokedexGUI pokedexGUI = null;
    @SuppressWarnings("unused")
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
        btnGuardarProgreso = new JButton("Guardar Progreso");
        btnCapturarPokemon = new JButton("Capturar Pokémon");
        btnCapturarPokemon.setVisible(false);
    
        panelBotones.add(btnSeleccionarCriatura);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnGuardarProgreso);
        panelBotones.add(btnCapturarPokemon);
        add(panelBotones, BorderLayout.SOUTH);
        JButton btnVerPokedex = new JButton("Ver Pokédex");
        panelBotones.add(btnVerPokedex);
        btnVerPokedex.addActionListener(e -> mostrarPokedex());

        entrenador1 = new Entrenador("Ash");
        entrenadorRival = new Entrenador("Rival");
    
        cargarCriaturasDesdeArchivo("criaturas.txt");
        seleccionarEquipoInicial(); // Invocar el menú de selección
        crearEntrenadorRival();
        
    
        btnSeleccionarCriatura.addActionListener(e -> seleccionarCriatura());
        btnAtacar.addActionListener(e -> atacar());
        btnGuardarProgreso.addActionListener(e -> guardarProgreso());
        btnCapturarPokemon.addActionListener(e -> capturarPokemon());
    
        actualizarTexto();
         // Crear el panel para las imágenes
         JPanel panelImagenes = new JPanel();
         panelImagenes.setLayout(new GridBagLayout()); // Distribución más flexible
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 10, 10);
 
         // Crear labels para las imágenes
         imagenCriaturaSeleccionada = new JLabel();
         imagenCriaturaSeleccionada.setHorizontalAlignment(JLabel.CENTER);
 
         imagenVersus = new JLabel();
         imagenVersus.setHorizontalAlignment(JLabel.CENTER);
         cargarImagenVersus();
 
         imagenEnemigo = new JLabel();
         imagenEnemigo.setHorizontalAlignment(JLabel.CENTER);
 
         // Añadir los labels al panel
         gbc.gridx = 0;
         panelImagenes.add(imagenCriaturaSeleccionada, gbc);
 
         gbc.gridx = 1;
         panelImagenes.add(imagenVersus, gbc);
 
         gbc.gridx = 2;
         panelImagenes.add(imagenEnemigo, gbc);
 
         add(panelImagenes, BorderLayout.NORTH);
    }
    private void cargarImagenVersus() {
        String rutaVersus = RUTA_IMAGENES + "versus.png";
        File archivoImagen = new File(rutaVersus);
    
        if (archivoImagen.exists()) {
            ImageIcon icon = new ImageIcon(rutaVersus);
            imagenVersus.setIcon(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        } else {
            imagenVersus.setText("VS"); // Texto alternativo si no se encuentra la imagen
            imagenVersus.setHorizontalAlignment(JLabel.CENTER);
        }
    }
    

    private void actualizarImagenes() {
        if (criaturaSeleccionada != null) {
            ImageIcon icon = new ImageIcon(criaturaSeleccionada.getRutaImagen());
            imagenCriaturaSeleccionada.setIcon(new ImageIcon(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        } else {
            imagenCriaturaSeleccionada.setIcon(null);
        }
    
        if (enemigo != null) {
            ImageIcon icon = new ImageIcon(enemigo.getRutaImagen());
            imagenEnemigo.setIcon(new ImageIcon(icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        } else {
            imagenEnemigo.setIcon(null);
        }
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
                String rutaImagen = RUTA_IMAGENES + nombre + " delante.png";
                String descripcion = datos[8]; // Nueva columna de descripción

                Criatura criatura = new Criatura(nombre, salud, ataque, defensa, tipo, habilidad, evolucion, rutaImagen, descripcion);

                entrenador1.agregarAColeccion(criatura); // Solo agregar a la colección
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo de criaturas", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
    
    
private void evolucionarCriatura(Criatura criatura) {
    String evolucion = criatura.getEvolucion();
    if (evolucion == null || evolucion.isEmpty()) {
        textArea.append(criatura.getNombre() + " no tiene evolución definida.\n");
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader("criaturas.txt"))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(",");
            if (datos[0].equalsIgnoreCase(evolucion)) {
                // Crear la nueva criatura evolucionada
                Criatura evolucionada = new Criatura(
                        datos[0],
                        Integer.parseInt(datos[1]),
                        Integer.parseInt(datos[2]),
                        Integer.parseInt(datos[3]),
                        datos[4],
                        datos[5],
                        datos[6],
                        datos[7],
                        datos[8]
                    
                );

                // Reemplazar la criatura en el equipo
                entrenador1.getEquipo().remove(criatura); // Eliminar la criatura original
                entrenador1.getEquipo().add(evolucionada); // Agregar la criatura evolucionada

                textArea.append("\n¡" + criatura.getNombre() + " ha evolucionado a " + evolucionada.getNombre() + "!\n");
                return;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar la evolución", "Error", JOptionPane.ERROR_MESSAGE);
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
            criaturaSeleccionada.incrementarCombatesGanados();

            // Verificar si puede evolucionar
            if (criaturaSeleccionada.getCombatesGanados() >= 2) { // Por ejemplo, requiere 2 combates ganados
                evolucionarCriatura(criaturaSeleccionada);
            }

            if (entrenadorRival.getEquipo().isEmpty()) {
                textArea.append("¡Has ganado el combate! Todos los Pokémon enemigos han sido derrotados.\n");
                enemigo = null;

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
        actualizarImagenes();
    }
}

public void seleccionarYGenerarEquipos() {
    // 1. Mostrar la Pokédex del jugador
    System.out.println("Elige tu equipo (hasta 3 Pokémon):");
    entrenador1.getPokedex().mostrarPokedex();

    // 2. Seleccionar el equipo del jugador
    List<Criatura> equipoJugador = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
        try {
            int opcion = obtenerOpcionValida(); // Asume que devuelve un índice basado en entrada del usuario
            Criatura seleccionada = entrenador1.getColeccion().get(opcion - 1);

            if (equipoJugador.contains(seleccionada)) {
                System.out.println("Ya seleccionaste este Pokémon. Elige otro.");
                i--; // Permitir al usuario reintentar
                continue;
            }

            equipoJugador.add(seleccionada);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Opción fuera de rango. Intenta nuevamente.");
            i--; // Permitir al usuario reintentar
        }
    }
    entrenador1.setEquipo(equipoJugador);

    // 3. Generar el equipo del enemigo basado en el equipo del jugador
    List<Criatura> equipoEnemigo = generarEquipoEmparejado(entrenadorRival.getColeccion(), equipoJugador);
    entrenadorRival.setEquipo(equipoEnemigo);

    // 4. Mostrar equipos
    System.out.println("Tu equipo:");
    mostrarEquipo(entrenador1.getEquipo());
    System.out.println("\nEquipo enemigo:");
    mostrarEquipo(entrenadorRival.getEquipo());
}

private void mostrarPokedex() {
    if (pokedexGUI == null) {
        // Llama a la GUI de la Pokédex en el hilo JavaFX
        PokedexGUI.mostrarPokedexGUI(pokedex);
        pokedexGUI = new Object(); // Solo para evitar múltiples aperturas
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
    
    private void seleccionarEquipoInicial() {
        List<Criatura> coleccion = entrenador1.getColeccion();
    
        if (coleccion.size() < 3) {
            JOptionPane.showMessageDialog(this, 
                "Debes tener al menos a 3 Pokémon para formar un equipo.", 
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
 
private void mostrarEquipo(List<Criatura> equipo) {
    for (Criatura c : equipo) {
        System.out.printf("%s | Salud: %d | Ataque: %d | Defensa: %d | Puntuación: %d\n",
                          c.getNombre(), c.getSalud(), c.getAtaque(), c.getDefensa(), c.getPuntaje());
    }
}
@SuppressWarnings("resource")
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}