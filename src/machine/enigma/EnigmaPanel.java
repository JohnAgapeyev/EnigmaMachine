package machine.enigma;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnigmaPanel extends JPanel {

    Rotor[] rotors;

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;

    private JButton rotorSwitch;
    private JButton rotorSet;
    private JButton[] rotorPlusMinus = new JButton[6];
    private JLabel outputMessage;
    private JLabel rotor1;
    private JLabel rotor2;
    private JLabel rotor3;
    private JTextField message;
    private Enigma enigma;
    private EnigmaActionListener enigmaActionListener;
    private boolean changingRotors;
    private int[] rotorRotation = new int[3];

    public EnigmaPanel() {
        setLayout(null);
        enigma = new Enigma();
        rotors = enigma.getRotors();
        enigmaActionListener = new EnigmaActionListener();
        outputMessage = new JLabel(
                "This is a test of the emergency broadcast system");
        rotor1 = new JLabel("0");
        rotor2 = new JLabel("0");
        rotor3 = new JLabel("0");
        message = new JTextField();
        rotorSwitch = new JButton("Switch Rotors");
        rotorSet = new JButton("Set Rotors");
        rotorPlusMinus[0] = new JButton("+");
        rotorPlusMinus[1] = new JButton("-");
        rotorPlusMinus[2] = new JButton("+");
        rotorPlusMinus[3] = new JButton("-");
        rotorPlusMinus[4] = new JButton("+");
        rotorPlusMinus[5] = new JButton("-");

        changingRotors = false;

        message.addActionListener(enigmaActionListener);
        rotorSwitch.addActionListener(enigmaActionListener);
        rotorSet.addActionListener(enigmaActionListener);

        rotorSwitch.setBounds(170, 250, 115, 30);
        rotorSet.setBounds(170, 210, 115, 30);

        message.setBounds(170, 40, 130, 24);
        outputMessage.setBounds(150, 80, 500, 20);

        rotor1.setBounds(187, 130, 20, 20);
        rotor2.setBounds(227, 130, 20, 20);
        rotor3.setBounds(267, 130, 20, 20);

        rotorPlusMinus[0].setBounds(180, 110, 20, 20);
        rotorPlusMinus[1].setBounds(180, 150, 20, 20);
        rotorPlusMinus[2].setBounds(220, 110, 20, 20);
        rotorPlusMinus[3].setBounds(220, 150, 20, 20);
        rotorPlusMinus[4].setBounds(260, 110, 20, 20);
        rotorPlusMinus[5].setBounds(260, 150, 20, 20);

        for (JButton button : rotorPlusMinus) {
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addActionListener(enigmaActionListener);
            add(button);
            button.setVisible(false);
        }

        add(outputMessage);
        add(message);
        add(rotorSwitch);
        add(rotorSet);
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

            } else if (e.getSource() == rotorSwitch) {
                formatRotorSettings();
            } else if (e.getSource() == rotorSet) {
                if (!changingRotors) {
                    rotorSet.setText("Done");
                    changingRotors = true;
                    for (JButton button : rotorPlusMinus) {
                        button.setVisible(true);
                    }
                } else {
                    rotorSet.setText("Set Rotors");
                    changingRotors = false;
                    for (JButton button : rotorPlusMinus) {
                        button.setVisible(false);
                    }
                    for (int i = 0; i < rotorRotation.length; i++) {
                        for (int j = 0; j < rotorRotation[i]; j++) {
                            rotors[i].rotate();
                        }
                    }
                }
            } else if (e.getSource() == rotorPlusMinus[0]) {
                rotorRotation[0]++;
                formatRotorSettings();
            } else if (e.getSource() == rotorPlusMinus[1]) {
                rotorRotation[0]--;
                formatRotorSettings();
            } else if (e.getSource() == rotorPlusMinus[2]) {
                rotorRotation[1]++;
                formatRotorSettings();
            } else if (e.getSource() == rotorPlusMinus[3]) {
                rotorRotation[1]--;
                formatRotorSettings();
            } else if (e.getSource() == rotorPlusMinus[4]) {
                rotorRotation[2]++;
                formatRotorSettings();
            } else if (e.getSource() == rotorPlusMinus[5]) {
                rotorRotation[2]--;
                formatRotorSettings();
            }
        }
    }

    public void formatRotorSettings() {
        for (int i = 0; i < rotorRotation.length; i++) {
            if (rotorRotation[i] < 0) {
                rotorRotation[i] = 25;
            }
        }
        // int first, second, third;
        // if (rotKey < 0) {
        // rotKey = 0;
        // }
        // third = rotKey % 26;
        // second = (rotKey / 26) % 26;
        // first = ((rotKey / 26) / 26) % 26;

        rotor1.setText(String.valueOf(rotorRotation[0]));
        rotor2.setText(String.valueOf(rotorRotation[1]));
        rotor3.setText(String.valueOf(rotorRotation[2]));
    }
}
