package machine.enigma;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnigmaPanel extends JPanel {

    private FontMetrics metrics;

    private Rotor[] rotors;

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;

    private static final List<Character> ALPHABET = new ArrayList<Character>(26);

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

    private JButton plugBoardButton;
    private JButton rotorSet;
    private JButton[] rotorPlusMinus = new JButton[6];
    private JButton clearTextButton;

    private JTextArea originalMessage;
    private JTextArea codedMessage;

    private JLabel[] rotorDisplay = new JLabel[3];
    private JTextField message;
    private Enigma enigma;
    private EnigmaListener listener = new EnigmaListener();
    private boolean changingRotors;
    private int[] rotorRotation = new int[3];

    private List<JTextField> plugs = new ArrayList<JTextField>(26);
    private JLabel[] plugLabels = new JLabel[26];
    private boolean changingPlugs;

    private Runnable clearText = () -> {
        originalMessage.setText("");
        codedMessage.setText("");
    };

    public EnigmaPanel() throws IOException {

        char[] tempAlphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase()
                .toCharArray();
        for (char letter : tempAlphabet) {
            ALPHABET.add(new Character(letter));
        }

        chooseRotors();
        setLayout(null);
        enigma = new Enigma();
        rotors = enigma.getRotors();

        for (int i = 0; i < rotorDisplay.length; i++) {
            rotorDisplay[i] = new JLabel("0");
        }

        int textWidth = 10;
        int textHeight = 5;
        message = new JTextField();
        originalMessage = new JTextArea(10, 5);
        codedMessage = new JTextArea(10, 5);

        metrics = originalMessage.getFontMetrics(originalMessage.getFont());
        int fontHeight = metrics.getHeight();
        int fontWidth = metrics.getMaxAdvance();

        JTextField temp;
        for (int i = 0; i < 26; i++) {
            temp = new JTextField();
            temp.setEnabled(false);
            temp.setDocument(new JTextFieldLimit(1));
            temp.getDocument().putProperty("sourceIndex", i);
            temp.getDocument().addDocumentListener(listener);
            temp.addKeyListener(listener);
            plugs.add(temp);
            plugLabels[i] = new JLabel(String.valueOf(ALPHABET.get(i)));
        }

        message.setDocument(new JTextFieldLimit(1));

        plugBoardButton = new JButton("Set Plugboard");
        rotorSet = new JButton("Set Rotors");
        clearTextButton = new JButton("Clear Text");

        for (int i = 0; i < rotorPlusMinus.length; i++) {
            if (i % 2 == 0) {
                rotorPlusMinus[i] = new JButton("+");
            } else {
                rotorPlusMinus[i] = new JButton("-");
            }
        }

        changingRotors = false;
        changingPlugs = false;

        message.addActionListener(listener);
        plugBoardButton.addActionListener(listener);
        rotorSet.addActionListener(listener);
        clearTextButton.addActionListener(listener);

        plugBoardButton.setBounds(270, 280, 115, 30);
        rotorSet.setBounds(270, 240, 115, 30);
        clearTextButton.setBounds(270, 320, 115, 30);

        message.setBounds(320, 20, fontWidth, fontHeight);

        originalMessage.setBounds(80, 130, fontWidth * textWidth, fontHeight
                * textHeight);
        codedMessage.setBounds(350, 130, fontWidth * textWidth, fontHeight
                * textHeight);

        rotorDisplay[0].setBounds(292, 70, 20, 20);
        rotorDisplay[1].setBounds(332, 70, 20, 20);
        rotorDisplay[2].setBounds(372, 70, 20, 20);

        rotorPlusMinus[0].setBounds(285, 50, 20, 20);
        rotorPlusMinus[1].setBounds(285, 90, 20, 20);
        rotorPlusMinus[2].setBounds(325, 50, 20, 20);
        rotorPlusMinus[3].setBounds(325, 90, 20, 20);
        rotorPlusMinus[4].setBounds(365, 50, 20, 20);
        rotorPlusMinus[5].setBounds(365, 90, 20, 20);

        plugs.get(0).setBounds(20, 410, fontWidth, fontHeight);
        plugs.get(1).setBounds(20, 490, fontWidth, fontHeight);
        plugs.get(2).setBounds(70, 410, fontWidth, fontHeight);
        plugs.get(3).setBounds(70, 490, fontWidth, fontHeight);
        plugs.get(4).setBounds(120, 410, fontWidth, fontHeight);
        plugs.get(5).setBounds(120, 490, fontWidth, fontHeight);
        plugs.get(6).setBounds(170, 410, fontWidth, fontHeight);
        plugs.get(7).setBounds(170, 490, fontWidth, fontHeight);
        plugs.get(8).setBounds(220, 410, fontWidth, fontHeight);
        plugs.get(9).setBounds(220, 490, fontWidth, fontHeight);
        plugs.get(10).setBounds(270, 410, fontWidth, fontHeight);
        plugs.get(11).setBounds(270, 490, fontWidth, fontHeight);
        plugs.get(12).setBounds(320, 410, fontWidth, fontHeight);
        plugs.get(13).setBounds(320, 490, fontWidth, fontHeight);
        plugs.get(14).setBounds(370, 410, fontWidth, fontHeight);
        plugs.get(15).setBounds(370, 490, fontWidth, fontHeight);
        plugs.get(16).setBounds(420, 410, fontWidth, fontHeight);
        plugs.get(17).setBounds(420, 490, fontWidth, fontHeight);
        plugs.get(18).setBounds(470, 410, fontWidth, fontHeight);
        plugs.get(19).setBounds(470, 490, fontWidth, fontHeight);
        plugs.get(20).setBounds(520, 410, fontWidth, fontHeight);
        plugs.get(21).setBounds(520, 490, fontWidth, fontHeight);
        plugs.get(22).setBounds(570, 410, fontWidth, fontHeight);
        plugs.get(23).setBounds(570, 490, fontWidth, fontHeight);
        plugs.get(24).setBounds(620, 410, fontWidth, fontHeight);
        plugs.get(25).setBounds(620, 490, fontWidth, fontHeight);

        for (JTextField plug : plugs) {
            add(plug);
        }

        for (int i = 0; i < plugLabels.length; i++) {
            plugLabels[i].setBounds(plugs.get(i).getX() + (fontWidth / 3),
                    plugs.get(i).getY() - 20, fontWidth, fontHeight);
            add(plugLabels[i]);
        }

        for (JButton button : rotorPlusMinus) {
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addActionListener(listener);
            add(button);
            button.setVisible(false);
        }

        add(message);
        add(plugBoardButton);
        add(rotorSet);
        add(clearTextButton);
        add(rotorDisplay[0]);
        add(rotorDisplay[1]);
        add(rotorDisplay[2]);
        add(originalMessage);
        add(codedMessage);

        message.addKeyListener(listener);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void formatRotorSettings() {
        int length = rotorRotation.length - 1;
        for (int i = length; i > -1; i--) {
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
        for (int i = 0; i < rotorDisplay.length; i++) {
            rotorDisplay[i].setText(String.valueOf(rotorRotation[i]));
        }
    }

    private void chooseRotors() {
        boolean areThreeRotorsChosen = false;
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

    private class EnigmaListener extends KeyAdapter implements ItemListener,
            ActionListener, DocumentListener {

        private Pattern alpha = Pattern.compile("[a-zA-Z\b]");
        private Matcher match;

        private final int MAX_SELECTIONS = 3;
        private int currentSelections = 0;

        private List<char[]> textChangeCache = new ArrayList<char[]>();

        public EnigmaListener() {
            for (int i = 0; i < 26; i++) {
                textChangeCache.add(new char[2]);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            char letter = Character.toUpperCase(e.getKeyChar());
            match = alpha.matcher(Character.toString(letter));
            if (!match.matches()) {
                return;
            } else {
                if (e.getComponent() == message) {
                    // Backspace character
                    if (e.getKeyCode() == 8) {
                        return;
                    } else {
                        message.setText("");
                        originalMessage.setText(originalMessage.getText()
                                + letter);
                        rotorRotation[2]++;
                        formatRotorSettings();
                        codedMessage.setText(codedMessage.getText()
                                + enigma.encode(letter, rotorsChosen));

                        String originText = originalMessage.getText();
                        String codeText = codedMessage.getText();
                        int length = originText.length();

                        for (int i = 0; i < length; i++) {
                            if (originText.charAt(i) == ' ') {
                                length--;
                            }
                        }

                        if (length % 25 == 0) {
                            originalMessage.setText(originText + "\n");
                            codedMessage.setText(codeText + "\n");
                        } else if (length % 5 == 0) {
                            originalMessage.setText(originText + " ");
                            codedMessage.setText(codeText + " ");
                        }
                    }
                } else if (plugs.contains(e.getComponent())) {
                    if (e.getKeyCode() == 8) {

                        textChangeCache.get(plugs.indexOf(e.getComponent()))[0] = textChangeCache
                                .get(plugs.indexOf(e.getComponent()))[1];
                        textChangeCache.get(plugs.indexOf(e.getComponent()))[1] = ' ';

                        plugs.get(
                                ALPHABET.indexOf(textChangeCache.get(plugs
                                        .indexOf(e.getComponent()))[0]))
                                .setText("");

                        textChangeCache.get(ALPHABET.indexOf(textChangeCache
                                .get(plugs.indexOf(e.getComponent()))[0]))[0] = textChangeCache
                                .get(ALPHABET.indexOf(textChangeCache.get(plugs
                                        .indexOf(e.getComponent()))[0]))[1];
                        textChangeCache.get(ALPHABET.indexOf(textChangeCache
                                .get(plugs.indexOf(e.getComponent()))[0]))[1] = ' ';

                    } else {
                        int letterIndex = ALPHABET.indexOf(letter);

                        if (!plugs.get(letterIndex).getText().equals("")
                                && !plugs
                                        .get(letterIndex)
                                        .getText()
                                        .equals(String.valueOf(ALPHABET
                                                .get(plugs.indexOf(e
                                                        .getComponent()))))) {

                            JOptionPane.showMessageDialog(null, letter
                                    + " is already taken. Please "
                                    + "choose another plug or delete its"
                                    + " value first.");

                            ((JTextField) e.getComponent()).setText("");

                        } else {
                            textChangeCache
                                    .get(plugs.indexOf(e.getComponent()))[0] = textChangeCache
                                    .get(plugs.indexOf(e.getComponent()))[1];
                            textChangeCache
                                    .get(plugs.indexOf(e.getComponent()))[1] = letter;

                            System.out.println(textChangeCache.get(plugs
                                    .indexOf(e.getComponent()))[0]);
                            System.out.println(textChangeCache.get(plugs
                                    .indexOf(e.getComponent()))[1]);
                            System.out.println(plugs.indexOf(e.getComponent()));

                            plugs.get(letterIndex).setText(
                                    String.valueOf(ALPHABET.get(plugs.indexOf(e
                                            .getComponent()))));

                            textChangeCache.get(letterIndex)[0] = textChangeCache
                                    .get(letterIndex)[1];
                            textChangeCache.get(letterIndex)[1] = ALPHABET
                                    .get(plugs.indexOf(e.getComponent()));

                            // System.out.println(textChangeCache.get(letterIndex)[0]);
                            // System.out.println(textChangeCache.get(letterIndex)[1]);
                            // System.out.println(letterIndex);
                        }
                    }
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == plugBoardButton) {
                if (changingPlugs) {
                    changingPlugs = false;
                    plugBoardButton.setText("Set Plugboard");
                    for (JTextField plug : plugs) {
                        plug.setEnabled(false);
                    }
                    clearText.run();
                } else {
                    plugBoardButton.setText("Done");
                    changingPlugs = true;
                    for (JTextField plug : plugs) {
                        plug.setEnabled(true);
                    }
                }
            } else if (e.getSource() == rotorSet) {
                if (changingRotors) {
                    rotorSet.setText("Set Rotors");
                    changingRotors = false;
                    for (JButton button : rotorPlusMinus) {
                        button.setVisible(false);
                    }
                } else {
                    rotorSet.setText("Done");
                    changingRotors = true;
                    for (JButton button : rotorPlusMinus) {
                        button.setVisible(true);
                    }
                }
            } else if (e.getSource() == clearTextButton) {
                clearText.run();
            } else {
                if (e.getSource() == rotorPlusMinus[0]) {
                    rotorRotation[0]++;
                } else if (e.getSource() == rotorPlusMinus[1]) {
                    rotorRotation[0]--;
                } else if (e.getSource() == rotorPlusMinus[2]) {
                    rotorRotation[1]++;
                } else if (e.getSource() == rotorPlusMinus[3]) {
                    rotorRotation[1]--;
                } else if (e.getSource() == rotorPlusMinus[4]) {
                    rotorRotation[2]++;
                } else if (e.getSource() == rotorPlusMinus[5]) {
                    rotorRotation[2]--;
                }
                formatRotorSettings();
            }
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();
            final int length = rotorsChosen.length;
            if (source.isSelected()) {
                currentSelections++;
                for (int i = 0; i < length; i++) {
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
                for (int i = 0; i < length; i++) {
                    if (rotorsChosen[i] != null
                            && rotorsChosen[i] == Arrays.asList(rotorCheckBox)
                                    .indexOf(source)) {
                        rotorsChosen[i] = null;

                        int firstNullIndex = 0;
                        for (int j = 0; j < length; j++) {
                            if (rotorsChosen[j] == null) {
                                firstNullIndex = j;
                                break;
                            }
                        }
                        Integer temp = rotorsChosen[firstNullIndex];
                        for (int j = firstNullIndex; j < length - 1; j++) {
                            rotorsChosen[j] = rotorsChosen[j + 1];
                        }
                        rotorsChosen[length - 1] = temp;
                    }
                }
                if (currentSelections < MAX_SELECTIONS) {
                    for (JCheckBox check : rotorCheckBox)
                        check.setEnabled(true);
                }
            }
            String display = "";
            for (int i = 0; i < length; i++) {
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

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            // System.out
            // .println(plugs.get(
            // (int) e.getDocument().getProperty("sourceIndex"))
            // .getText());
            // System.out.println(e.getDocument().getProperty("sourceIndex"));
        }
    }
}
