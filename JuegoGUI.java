import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.sound.sampled.*;



@SuppressWarnings("unused")
public class JuegoGUI extends JFrame {
    private Entrenador entrenador1;
    private Criatura criaturaSeleccionada;
    private Criatura enemigo;
    private JTextArea textArea;
    private JButton btnAtacar, btnSeleccionarCriatura, btnGuardarProgreso, btnCapturarPokemon;
    private JButton btnNuevoCombate;
    private Entrenador entrenadorRival;
    private ArrayList<Criatura> criaturasVencidas = new ArrayList<>();
    private JButton[] pocionesJugador;
    private JButton[] pocionesEnemigo;
    private JLabel imagenCriaturaSeleccionada;
    private JLabel imagenEnemigo;
    private JLabel imagenVersus;
    private JLabel efectoAtaque;
    private Pokedex pokedex;
    
    private static final String RUTA_IMAGENES = "Imagenes/";

public JuegoGUI() {
        // Llamar a la función para reproducir el sonido
        Sonido sonido = new Sonido();
        sonido.reproducirSonido("Sonidos/sonido.wav"); // Asegúrate de poner la ruta correcta al archivo
        // Configuración de la ventana
        setTitle("Juego de Criaturas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Obtener el tamaño de la pantalla
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ventanaAncho = getWidth();
        int ventanaAlto = getHeight();

        
        // Calcular el tamaño de la ventana como un porcentaje de la pantalla
        int ancho = (int) (pantalla.width * 1);  // 80% del ancho de la pantalla
        int alto = (int) (pantalla.height - 50);  // 80% del alto de la pantalla
        setSize(ancho, alto);
    
        // Hacer que la ventana sea redimensionable
        setResizable(true);
    
        // Centrar la ventana en la pantalla
            setLocationRelativeTo(null);
        // Panel principal dividido en dos áreas
        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2));
    
        // Crear el JLayeredPane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // Usar un diseño nulo para posicionar manualmente los componentes
    
        // Agregar el fondo
        ImageIcon fondoIcon = new ImageIcon(RUTA_IMAGENES + "fondo.png");
        JLabel fondoLabel = new JLabel(fondoIcon);
        fondoLabel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(fondoLabel, Integer.valueOf(0)); // Capa más baja
    
            // Panel izquierdo (imagen del Pokémon seleccionado y pociones)
        TransparentPanel panelIzquierdo = new TransparentPanel();
        panelIzquierdo.setLayout(new BorderLayout());
        imagenCriaturaSeleccionada = new JLabel();
        imagenCriaturaSeleccionada.setHorizontalAlignment(SwingConstants.CENTER); // Centrar la imagen
        imagenCriaturaSeleccionada.setOpaque(false); // Hacer que el fondo sea transparente
    
        int anchoImagen = (int) (ventanaAncho * 0.2); // 20% del ancho
        int altoImagen = (int) (ventanaAlto * 0.3);  // 30% del alto
        imagenCriaturaSeleccionada.setBounds((int)(ventanaAncho * 0.1), (int)(ventanaAlto * 0.3), anchoImagen, altoImagen);
        panelIzquierdo.add(imagenCriaturaSeleccionada, BorderLayout.CENTER);
    
        TransparentPanel panelPocionesIzquierdo = new TransparentPanel();
        panelPocionesIzquierdo.setLayout(new GridLayout(1, 4, 10, 10));
        pocionesJugador = new JButton[4];
        for (int i = 0; i < 4; i++) {
            pocionesJugador[i] = crearBotonPocion(i, true);
            panelPocionesIzquierdo.add(pocionesJugador[i]);
        }
    
        // Mover las pociones hacia abajo ajustando el BorderLayout
        panelIzquierdo.add(panelPocionesIzquierdo, BorderLayout.SOUTH);
        panelIzquierdo.setBounds(20, 400, 200, 300); // Ajusta la posición Y para todo el panel izquierdo
    
        layeredPane.add(panelIzquierdo, Integer.valueOf(1)); // Capa superior
    
        
    
        // Inicializar imagenVersus antes de cargar la imagen
        imagenVersus = new JLabel(); 
        imagenVersus.setHorizontalAlignment(SwingConstants.CENTER); // Centrar la imagen
        cargarImagenVersus(); // Ahora este método tiene un objeto inicializado para trabajar
        // Crear el JLabel para el efecto del ataque
        efectoAtaque = new JLabel();
        efectoAtaque.setHorizontalAlignment(SwingConstants.CENTER); // Centrar la imagen
        efectoAtaque.setVisible(false); // Inicialmente oculto

        // Configurar posición y agregar imagenVersus al JLayeredPane
        int anchoDisponible = getWidth() / 2; // La mitad izquierda
        int centroX = anchoDisponible / 2 - 50; // Ajustar horizontalmente dentro de esa mitad
        int centroY = getHeight() / 2 - 50; // Centrar verticalmente
        imagenVersus.setBounds(centroX-120, centroY, 400, 300);
        efectoAtaque.setBounds(centroX - 120, centroY - 100, 400, 300);  // Ajusta 100 píxeles hacia arriba
        layeredPane.add(imagenVersus, Integer.valueOf(2)); // Capa superiorÇ
        layeredPane.add(efectoAtaque, Integer.valueOf(3));
    
            // Panel derecho (imagen del Pokémon enemigo y pociones)
        TransparentPanel panelDerecho = new TransparentPanel();
        panelDerecho.setLayout(new BorderLayout());
        imagenEnemigo = new JLabel();
        imagenEnemigo.setHorizontalAlignment(SwingConstants.CENTER); // Centrar la imagen
        imagenEnemigo.setOpaque(false); // Hacer que el fondo sea transparente
        imagenEnemigo.setIcon(null);
    
        // Mover la imagen más abajo ajustando el setBounds
        imagenEnemigo.setBounds(0, 400, 200, 300); // Cambiar la posición Y
        panelDerecho.add(imagenEnemigo, BorderLayout.CENTER);
    
        TransparentPanel panelPocionesDerecho = new TransparentPanel();
        panelPocionesDerecho.setLayout(new GridLayout(1, 4, 10, 10));
        pocionesEnemigo = new JButton[4];
        for (int i = 0; i < 4; i++) {
            pocionesEnemigo[i] = crearBotonPocion(i, false);
            panelPocionesDerecho.add(pocionesEnemigo[i]);
        }
    
        // Mover las pociones hacia abajo ajustando el BorderLayout
        panelDerecho.add(panelPocionesDerecho, BorderLayout.SOUTH);
        panelDerecho.setBounds(anchoDisponible - 230, 400, 200, 300); // Ajustar la posición Y para todo el panel derecho
    
        layeredPane.add(panelDerecho, Integer.valueOf(1)); // Capa superior
    
    
    
        // Agregar el layeredPane al JFrame
        add(layeredPane, BorderLayout.CENTER);
    // Panel derecho para el área de texto con fondo negro
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BorderLayout());
        panelTexto.setBackground(Color.darkGray); // Fondo negro

        // Área de texto estilizada
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("DialogInput", Font.BOLD, 22)); // Fuente en negrita
        textArea.setForeground(Color.WHITE); // Texto verde
        textArea.setOpaque(false); // Hacer que el área de texto sea transparente
        textArea.setLineWrap(true); // Ajuste de línea
        textArea.setWrapStyleWord(true); // Ajuste de palabra

        // ScrollPane con área de texto
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Agregar el ScrollPane al panelTexto
        panelTexto.add(scrollPane, BorderLayout.CENTER);

        // Agregar ambos paneles al principal
        panelPrincipal.add(layeredPane);
        panelPrincipal.add(panelTexto);
        add(panelPrincipal, BorderLayout.CENTER);

        // Panel inferior con botones de acción
        JPanel panelBotones = new JPanel();
        btnSeleccionarCriatura = new JButton("Seleccionar Criatura");
        btnAtacar = new JButton("Atacar");
        btnGuardarProgreso = new JButton("Guardar Progreso");
        btnCapturarPokemon = new JButton("Capturar Pokémon");
        btnNuevoCombate = new JButton("Nuevo Combate");
        JButton btnVerPokedex = new JButton("Ver Pokédex");

        btnVerPokedex.addActionListener(e -> mostrarPokedex());
        panelBotones.add(btnSeleccionarCriatura);
        // Botón para abrir la Pokédex
        panelBotones.add(btnSeleccionarCriatura);
        btnCapturarPokemon.setVisible(true);
        panelBotones.add(btnVerPokedex);
        panelBotones.add(btnNuevoCombate);  
        estilizarBoton(btnVerPokedex);
        estilizarBoton(btnGuardarProgreso);
        estilizarBoton(btnSeleccionarCriatura);
        estilizarBoton(btnAtacar);
        estilizarBoton(btnNuevoCombate);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnGuardarProgreso);
        panelBotones.add(btnGuardarProgreso);
        add(panelBotones, BorderLayout.SOUTH);

    
        // Inicialización de entrenadores y Pokédex
        entrenador1 = new Entrenador("Ash");
        entrenadorRival = new Entrenador("Rival");
        pokedex = new Pokedex();
    
        // Cargar criaturas desde archivo y preparar el juego
        cargarCriaturasDesdeArchivo("criaturas.txt");
        seleccionarEquipoInicial();
        crearEntrenadorRival();
    
        btnSeleccionarCriatura.addActionListener(e -> seleccionarCriatura());
        btnAtacar.addActionListener(e -> atacar());
        btnGuardarProgreso.addActionListener(e -> guardarProgreso());
        btnCapturarPokemon.addActionListener(e -> capturarPokemon());
        btnNuevoCombate.addActionListener(e -> iniciarNuevoCombate());
        actualizarTexto();
        actualizarImagenes();
        setVisible(true);
    }


    private JButton crearBotonPocion(int index, boolean esJugador) {
        JButton boton = new JButton(new ImageIcon(RUTA_IMAGENES + "pocion.png"));
        boton.addActionListener(e -> {
            if (esJugador && criaturaSeleccionada != null) {
                criaturaSeleccionada.curar(20);
                boton.setEnabled(false);
            } else if (!esJugador && enemigo != null) {
                enemigo.curar(20);
                boton.setEnabled(false);
            }
            actualizarTexto();
        });
        return boton;
    }

    private void cargarImagenVersus() {
        String rutaVersus = RUTA_IMAGENES + "versus.png";
        File archivoImagen = new File(rutaVersus);

        if (archivoImagen.exists()) {
            ImageIcon icon = new ImageIcon(rutaVersus);
            imagenVersus.setIcon(new ImageIcon(icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH)));
        } else {
            imagenVersus.setText("VS");
            imagenVersus.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(new Color(169, 169, 169)); // Gris oscuro
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("DialogInput", Font.BOLD, 14));
        boton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        boton.setFocusPainted(false);
    }

    private void actualizarImagenes() {
        if (criaturaSeleccionada != null) {
            ImageIcon icon = new ImageIcon(criaturaSeleccionada.getRutaImagen());
            int ancho = imagenCriaturaSeleccionada.getWidth(); // Usa el tamaño actual del JLabel
            int alto = imagenCriaturaSeleccionada.getHeight();
            imagenCriaturaSeleccionada.setIcon(new ImageIcon(icon.getImage().getScaledInstance(ancho*2, alto*2, Image.SCALE_SMOOTH)));
        } else {
            imagenCriaturaSeleccionada.setIcon(null);
        }
    
        if (enemigo != null) {
            ImageIcon icon = new ImageIcon(enemigo.getRutaImagen());
            int ancho = imagenEnemigo.getWidth(); // Usa el tamaño actual del JLabel
            int alto = imagenEnemigo.getHeight();
            imagenEnemigo.setIcon(new ImageIcon(icon.getImage().getScaledInstance(ancho*2, alto*2, Image.SCALE_SMOOTH)));
        } else {
            imagenEnemigo.setIcon(null);
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
    
                if (datos.length < 9) { // Validar que existan suficientes columnas en cada línea
                    JOptionPane.showMessageDialog(this, "Datos incompletos en la línea: " + linea, 
                                                  "Error", JOptionPane.WARNING_MESSAGE);
                    continue; // Saltar a la siguiente línea
                }
    
                // Extracción de datos
                String nombre = datos[0];
                int salud = parseIntSeguro(datos[1]);
                int ataque = parseIntSeguro(datos[2]);
                int defensa = parseIntSeguro(datos[3]);
                String tipo = datos[4];
                String habilidad = datos[5];
                String evolucion = datos[6];
                String rutaImagen = RUTA_IMAGENES + nombre + " delante.png";
                String descripcion = datos[8];
    
                // Creación de la criatura
                Criatura criatura = new Criatura(nombre, salud, ataque, defensa, tipo, habilidad, evolucion, rutaImagen, descripcion);
    
                // Agregar a la Pokédex y a la colección del entrenador
                pokedex.agregarCriatura(criatura);
                entrenador1.agregarAColeccion(criatura);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo de criaturas: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en el formato de datos numéricos: " + e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void seleccionarCriatura() {
        if (entrenador1.getEquipo().size() > 0) {
            actualizarImagenes();
            // Crear una nueva lista de nombres para evitar duplicados
            List<String> nombresCriaturas = entrenador1.getEquipo().stream()
            .map(Criatura::getNombre)
            .distinct() // Evitar duplicados
            .collect(Collectors.toList());

            String[] opciones = nombresCriaturas.toArray(new String[0]);

            String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona tu criatura:",
            "Seleccionar Criatura",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]      
                 
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
                        actualizarImagenes();
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
                    String rutaImagenEvolucionada = RUTA_IMAGENES + datos[0] + " delante.png";
                    Criatura evolucionada = new Criatura(
                            datos[0],
                            Integer.parseInt(datos[1]),
                            Integer.parseInt(datos[2]),
                            Integer.parseInt(datos[3]),
                            datos[4],
                            datos[5],
                            datos[6],
                            rutaImagenEvolucionada,
                            datos[8]
                    );
    
                    // Reemplazar la criatura en el equipo
                    if (!entrenador1.getEquipo().remove(criatura)) {
                        textArea.append("Error: No se encontró a " + criatura.getNombre() + " en el equipo.\n");
                        return;
                    }
                    entrenador1.getEquipo().add(evolucionada);
    
                    // Actualizar criatura seleccionada
                    criaturaSeleccionada = evolucionada;
    
                    // Actualizar la interfaz
                    textArea.append("\n¡" + criatura.getNombre() + " ha evolucionado a " + evolucionada.getNombre() + "!\n");
                    actualizarImagenes();
                    actualizarTexto();
                    return; // Salir después de evolucionar
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar la evolución", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


private void atacar() {
    if (criaturaSeleccionada != null && enemigo != null) {
        criaturaSeleccionada.atacar(enemigo);
         // Generar número aleatorio para seleccionar una imagen
         Random random = new Random();
         int numeroImagen = random.nextInt(5) + 1; // Genera un número entre 1 y 3
         String rutaImagen = RUTA_IMAGENES + "efecto_ataque_" + numeroImagen + ".png";
 
         // Mostrar la imagen del efecto
         ImageIcon iconoEfecto = new ImageIcon(rutaImagen);
         efectoAtaque.setIcon(new ImageIcon(iconoEfecto.getImage().getScaledInstance(
             200,
             200,
             Image.SCALE_SMOOTH
         )));
         efectoAtaque.setVisible(true);
 
         // Colocar el efecto justo por encima de la imagen del 'versus'
         int xPos = imagenVersus.getX() + (random.nextInt(250) - 130);  // Desplazar aleatoriamente en el eje X, 50% más a la izquierda
        int yPos = imagenVersus.getY() + (random.nextInt(20) - 10)-150;  // Desplazar aleatoriamente en el eje Y, entre -10 y 10 píxeles
         xPos = Math.max(0, Math.min(xPos, getWidth() - efectoAtaque.getWidth()));
         yPos = Math.max(0, Math.min(yPos, getHeight() +150- efectoAtaque.getHeight()));

        efectoAtaque.setBounds(xPos, yPos, imagenVersus.getWidth(), imagenVersus.getHeight());
        
         // Asegurarse de que la imagen del efecto está en la capa correcta
        
         efectoAtaque.repaint();
 
         // Ocultar la imagen después de 500 ms
         Timer timer = new Timer(500, new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 efectoAtaque.setVisible(false);
             }
         });
         timer.setRepeats(false); // Solo ejecutar una vez
         timer.start();
 
        if (enemigo.getSalud() <= 0) {
            textArea.append(enemigo.getNombre() + " ha sido derrotado!\n");
            criaturasVencidas.add(enemigo);
            entrenadorRival.getEquipo().remove(enemigo);
            criaturaSeleccionada.incrementarCombatesGanados();

            // Verificar si puede evolucionar
            if (criaturaSeleccionada.getCombatesGanados() >= 2) { // Por ejemplo, requiere 2 combates ganados
                evolucionarCriatura(criaturaSeleccionada);
                actualizarImagenes();
                actualizarTexto();
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
                actualizarTexto();
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
}private void mostrarPokedex() {
    if (pokedex != null && !pokedex.getCriaturas().isEmpty())
        // Llamar a la GUI de PokedexSwing
        SwingUtilities.invokeLater(() -> new PokedexSwing(pokedex));
    else
        JOptionPane.showMessageDialog(this, "La Pokédex no contiene criaturas o no está inicializada.", 
                                      "Error", JOptionPane.ERROR_MESSAGE);
}

private void mostrarColeccion(List<Criatura> coleccion) {
    for (int i = 0; i < coleccion.size(); i++) {
        Criatura c = coleccion.get(i);
        System.out.printf("%d. %s (Puntuación: %d)\n", i + 1, c.getNombre(), c.getPuntaje());
    }
}
private void crearEntrenadorRival() {
    entrenadorRival = new Entrenador("Rival");
    List<Criatura> equipoEnemigo = generarEquipoEmparejado(entrenador1.getColeccion(), entrenador1.getEquipo());
    entrenadorRival.setEquipo(equipoEnemigo);
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
    for (int i = 0; i < 3 && !posiblesEnemigos.isEmpty(); i++) {
        Criatura seleccionada = posiblesEnemigos.remove(random.nextInt(posiblesEnemigos.size()));
        equipoEnemigo.add(seleccionada);
    }

    return equipoEnemigo;
}

private int obtenerOpcionValida() {
    @SuppressWarnings("resource")
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

private void mostrarEquipo(List<Criatura> equipo) {
    for (Criatura c : equipo) {
        System.out.printf("%s | Salud: %d | Ataque: %d | Defensa: %d | Puntuación: %d\n",
            c.getNombre(), c.getSalud(), c.getAtaque(), c.getDefensa(), c.getPuntaje());
    }
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
    private void iniciarNuevoCombate() {
                // Reiniciar el estado del juego
        criaturaSeleccionada = null;
        enemigo = null;
        entrenador1.getEquipo().clear(); // Limpiar el equipo del jugador
        entrenadorRival.getEquipo().clear(); // Limpiar el equipo del rival
    
        // Restablecer el texto
        textArea.setText("¡Selecciona un nuevo equipo para el próximo combate!\n");
    
        // Permitir al usuario seleccionar un nuevo equipo
        seleccionarEquipoInicial();
    
        // Crear un nuevo equipo para el rival
        crearEntrenadorRival();
    
        // Actualizar las imágenes y la información de la interfaz
        actualizarImagenes();
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
 
    private void guardarProgreso() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("progreso.dat"))) {
            out.writeObject(entrenador1);  // Guardar el entrenador (y sus criaturas)
            JOptionPane.showMessageDialog(this, "Progreso guardado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el progreso", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoGUI().setVisible(true));
        Sonido sonido = new Sonido();
        sonido.reproducirSonido("Sonidos/sonido.mp3");
    }
}