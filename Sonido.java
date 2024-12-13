import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Sonido implements Serializable {

    public void reproducirSonido(String rutaArchivo) {
        try {
            // Cargar el archivo de sonido
            File archivoSonido = new File(rutaArchivo);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);
            
            // Obtener el clip de audio
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Reproducir el sonido
            clip.start();

            // Esperar a que termine la reproducción (opcional)
            // clip.drain(); // Esto es útil si quieres esperar hasta que termine de sonar

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
