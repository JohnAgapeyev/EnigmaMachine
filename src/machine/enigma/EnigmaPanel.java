package machine.enigma;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
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
    private Enigma enigma;
    private EnigmaActionListener enigmaActionListener;
    int rotKey = 0;

    public EnigmaPanel() {
        setLayout(null);
        enigma = new Enigma();
        enigmaActionListener = new EnigmaActionListener();
        outputMessage = new JLabel(
                "This is a test of the emergency broadcast system");
        rotor1 = new JLabel("");
        rotor2 = new JLabel("");
        rotor3 = new JLabel("");
        message = new JTextField();
        rotorChange = new JButton("Switch Rotors");

        message.addActionListener(enigmaActionListener);
        rotorChange.addActionListener(enigmaActionListener);

        rotorChange.setBounds(90, 200, 115, 30);
        message.setBounds(90, 40, 130, 24);
        outputMessage.setBounds(85, 80, 150, 20);
        rotor1.setBounds(130, 150, 20, 20);
        rotor2.setBounds(150, 150, 20, 20);
        rotor3.setBounds(170, 150, 20, 20);

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
                // outputMessage.setText(enigma.encode(message.getText()));
            } else if (e.getSource() == rotorChange) {
                Rotor[] x = Enigma.getRotors();
                char[] key = x[0].getRotorKey();
                String output = "";
                for (char letter : key) {
                    output += letter;
                }
                System.out.println(output);
                x[0].rotate();
                int first, second, third;
                third = rotKey % 26;
                second = (rotKey / 26) % 26;
                first = ((rotKey / 26) / 26) % 26;
                rotor1.setText(String.valueOf(first));
                rotor2.setText(String.valueOf(second));
                rotor3.setText(String.valueOf(third));
                rotKey++;
            }
        }
    }
}
