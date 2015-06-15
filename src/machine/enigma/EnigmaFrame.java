package machine.enigma;

import java.io.IOException;

import javax.swing.JFrame;

public class EnigmaFrame extends JFrame {

    /**
     * Serial ID for the frame.
     */
    private static final long serialVersionUID = -4908457653373357661L;

    public EnigmaFrame() throws IOException {
        super("Enigma Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new EnigmaPanel());
        setSize(675, 600);
        setResizable(false);
        setVisible(true);
    }

    public static void main(final String[] args) throws IOException {
        new EnigmaFrame();
    }
}
