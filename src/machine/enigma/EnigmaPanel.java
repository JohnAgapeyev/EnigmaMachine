package machine.enigma;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EnigmaPanel extends JPanel {

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;
    
    private JLabel outputMessage;
    private JTextField message;

    public EnigmaPanel() {
        outputMessage = new JLabel("yep");
        message = new JTextField("", 5);
        add(outputMessage);
        add(message);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
