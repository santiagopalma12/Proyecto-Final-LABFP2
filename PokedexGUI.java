import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PokedexGUI extends Application {

    private Pokedex pokedex;
    private ComboBox<String> comboBox;
    private Label lblDetalles;

    public PokedexGUI(Pokedex pokedex) {
        this.pokedex = pokedex;
    }

    @Override
    public void start(Stage primaryStage) {
        inicializarVentana(primaryStage);
    }

    @SuppressWarnings("unused")
    public void inicializarVentana(Stage stage) {
        // Cargar criaturas desde el archivo CSV
        cargarCriaturasDesdeArchivo("criaturas.txt");

        // Llenar el ComboBox con los nombres de los Pokémon
        comboBox = new ComboBox<>();
        lblDetalles = new Label("Selecciona un Pokémon para ver sus detalles.");

        for (Criatura criatura : pokedex.getCriaturas()) {
            comboBox.getItems().add(criatura.getNombre());
        }

        // Acción al seleccionar un Pokémon en la lista
        comboBox.setOnAction(e -> mostrarDetallesPokemon());

        // Configurar layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(comboBox, lblDetalles);

        // Crear la escena
        Scene scene = new Scene(vbox, 400, 300);

        // Configurar la ventana
        stage.setTitle("Pokédex");
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarDetallesPokemon() {
        String nombreSeleccionado = comboBox.getValue();

        if (nombreSeleccionado != null) {
            Criatura criaturaSeleccionada = obtenerCriaturaPorNombre(nombreSeleccionado);
            if (criaturaSeleccionada != null) {
                lblDetalles.setText("Nombre: " + criaturaSeleccionada.getNombre() +
                                    "\nSalud: " + criaturaSeleccionada.getSalud() +
                                    "\nAtaque: " + criaturaSeleccionada.getAtaque() +
                                    "\nDefensa: " + criaturaSeleccionada.getDefensa() +
                                    "\nEvoluciona a: " + criaturaSeleccionada.getEvolucion() +
                                    "\nDescripción: " + criaturaSeleccionada.getDescripcion());
            } else {
                lblDetalles.setText("No se pudo encontrar detalles.");
            }
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
                String rutaImagen = "imagenes/" + nombre + " delante.png";
                String descripcion = datos[8];

                Criatura criatura = new Criatura(nombre, salud, ataque, defensa, tipo, habilidad, evolucion, rutaImagen, descripcion);
                pokedex.agregarCriatura(criatura);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de criaturas: " + e.getMessage());
        }
    }

    public static void mostrarPokedexGUI(Pokedex pokedex) {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                PokedexGUI gui = new PokedexGUI(pokedex);
                gui.inicializarVentana(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

