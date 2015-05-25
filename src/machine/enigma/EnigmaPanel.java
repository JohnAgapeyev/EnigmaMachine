package machine.enigma;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnigmaPanel extends JPanel {

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;

    private JButton rotorChange;
    private JLabel outputMessage;
    private JLabel rotor1;
    private JLabel rotor2;
    private JLabel rotor3;
    private JTextField message;
    private Enigma machine;

    public EnigmaPanel() {
        machine = new Enigma();
        outputMessage = new JLabel("yep");
        rotor1 = new JLabel("0");
        rotor2 = new JLabel("0");
        rotor3 = new JLabel("0");
        message = new JTextField("", 5);
        rotorChange = new JButton("Switch Rotors");
        message.addActionListener(new EnigmaActionListener());
        rotorChange.addActionListener(new EnigmaActionListener());
        add(outputMessage);
        add(message);
        add(rotorChange);
        add(rotor1);
        add(rotor2);
        add(rotor3);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private class EnigmaActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == message) {
                outputMessage.setText(machine.encode(message.getText()));
            } else if (e.getSource() == rotorChange) {
                System.out.println("");
            }
        }
    }
}
