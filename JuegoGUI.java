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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoGUI game = new JuegoGUI();
            game.setVisible(true);
        });
    }
}
