package machine.enigma;

import javax.swing.JFrame;

/**
 * JFrame class to contain the main panel of the program.
 *
 * @author John Agapeyev
 *
 */
public class EnigmaFrame extends JFrame {

    /**
     * Serial ID for the frame.
     */
    private static final long serialVersionUID = -4908457653373357661L;

    /**
     * Width of the Frame.
     */
    private static final int FRAME_WIDTH = 675;

    /**
     * Height of the Frame.
     */
    private static final int FRAME_HEIGHT = 600;

    /**
     * Constructor for the frame.
     */
    public EnigmaFrame() {
        super("Enigma Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new EnigmaPanel());
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setVisible(true);
    }

    /**
     * Main method called when the project is run.
     *
     * @param args
     *            Command Line arguments
     */
    public static void main(final String[] args) {
        new EnigmaFrame();
    }
}
