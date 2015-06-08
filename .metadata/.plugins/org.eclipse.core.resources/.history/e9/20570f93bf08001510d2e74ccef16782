package machine.enigma;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

public class EnigmaFrame extends JFrame {

    /**
     * Serial ID for the panel.
     */
    private static final long serialVersionUID = -4908457653373357661L;

    public EnigmaFrame() throws IOException {
        super("Enigma Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new EnigmaPanel());
        setSize(300, 300);
        setMinimumSize(new Dimension(580, 600));
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new EnigmaFrame();
    }
}
