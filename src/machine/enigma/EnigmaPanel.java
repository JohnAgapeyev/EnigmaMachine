package machine.enigma;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

public class EnigmaPanel extends JPanel {

    private Rotor[] rotors;

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;

    private JCheckBox[] rotorCheckBox = { new JCheckBox("Rotor 1"),
            new JCheckBox("Rotor 2"), new JCheckBox("Rotor 3"),
            new JCheckBox("Rotor 4"), new JCheckBox("Rotor 5") };
    private String rotorChangeDialog = "Please select three rotors";
    private JLabel selectedRotorsMessage = new JLabel(
            "You have selected rotors: ");
    private JLabel displayRotorsLabel = new JLabel("");
    private Object[] optionParams = { rotorChangeDialog, rotorCheckBox,
            selectedRotorsMessage, displayRotorsLabel };
    private Integer[] rotorsChosen = new Integer[3];

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
        chooseRotors();
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
                try {
                    outputMessage.setText(enigma.encode(message.getText(), rotorsChosen));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                rotorRotation[2]++;
                formatRotorSettings();
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
                    formatRotorSettings();
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
            } else if (rotorRotation[i] > 25) {
                if (i > 0) {
                    rotorRotation[i - 1]++;
                }
                rotorRotation[i] = 0;
            }
            rotors[rotorsChosen[i]].setRotation(rotorRotation[i]);
        }
        rotor1.setText(String.valueOf(rotorRotation[0]));
        rotor2.setText(String.valueOf(rotorRotation[1]));
        rotor3.setText(String.valueOf(rotorRotation[2]));
    }

    private void chooseRotors() {
        boolean areThreeRotorsChosen = false;
        CheckListener listener = new CheckListener();
        for (JCheckBox check : rotorCheckBox) {
            check.addItemListener(listener);
        }

        JOptionPane.showConfirmDialog(null, optionParams, "Rotor Selection",
                JOptionPane.DEFAULT_OPTION);
        while (!areThreeRotorsChosen) {
            if (listener.getCurrentSelections() == 3) {
                areThreeRotorsChosen = true;
            } else {
                JOptionPane.showConfirmDialog(null, optionParams,
                        "Rotor Selection", JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    private class CheckListener implements ItemListener {

        private final int MAX_SELECTIONS = 3;
        private int currentSelections = 0;

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source.isSelected()) {
                currentSelections++;
                for (int i = 0; i < rotorsChosen.length; i++) {
                    if (rotorsChosen[i] == null) {
                        rotorsChosen[i] = Arrays.asList(rotorCheckBox).indexOf(
                                source);
                        break;
                    }
                }
                if (currentSelections == MAX_SELECTIONS) {
                    for (JCheckBox check : rotorCheckBox) {
                        if (!check.isSelected()) {
                            check.setEnabled(false);
                        }
                    }
                }
            } else {
                currentSelections--;
                for (int i = 0; i < rotorsChosen.length; i++) {
                    if (rotorsChosen[i] != null
                            && rotorsChosen[i] == Arrays.asList(rotorCheckBox)
                                    .indexOf(source)) {
                        rotorsChosen[i] = null;

                        int firstNullIndex = 0;
                        for (int j = 0; j < rotorsChosen.length; j++) {
                            if (rotorsChosen[j] == null) {
                                firstNullIndex = j;
                                break;
                            }
                        }
                        Integer temp = rotorsChosen[firstNullIndex];
                        final int rotorsChooseLength = rotorsChosen.length - 1;
                        for (int j = firstNullIndex; j < rotorsChooseLength; j++) {
                            rotorsChosen[j] = rotorsChosen[j + 1];
                        }
                        rotorsChosen[rotorsChooseLength] = temp;
                    }
                }
                if (currentSelections < MAX_SELECTIONS) {
                    for (JCheckBox check : rotorCheckBox)
                        check.setEnabled(true);
                }
            }
            String display = "";
            for (int i = 0; i < rotorsChosen.length; i++) {
                if (rotorsChosen[i] == null) {
                    display += "Empty   ";
                } else {
                    display += ((int) rotorsChosen[i] + 1) + "   ";
                }
            }
            displayRotorsLabel.setText(display);
        }

        public int getCurrentSelections() {
            return currentSelections;
        }
    }
}
