package machine.enigma;

import java.awt.Color;
import java.awt.Component;
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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * The panel that is used for all the visual elements of the program.
 *
 * @author John Agapeyev
 *
 */
public class EnigmaPanel extends JPanel {

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = 7298201505806512569L;

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final byte ALPHABET_LENGTH = Enigma.ALPHABET_LENGTH;

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final List<Character> ALPHABET = Enigma.ALPHABET;

    /**
     * A list containing all possible rotors that can be used.
     */
    private List<Rotor> rotors = new ArrayList<>(5);

    /**
     * A list of check boxes that are used in a popup at the start of the
     * program for the suer to pick which 3 rotors to use.
     */
    private final List<JCheckBox> rotorCheckBox = Arrays.asList(new JCheckBox(
            "Rotor 1"), new JCheckBox("Rotor 2"), new JCheckBox("Rotor 3"),
            new JCheckBox("Rotor 4"), new JCheckBox("Rotor 5"));

    /**
     * A display for the popup of which rotors are currently chosen.
     */
    private final JLabel displayRotorsLabel = new JLabel("");

    /**
     * A list representing the rotors that are chosen by the user, with their
     * index in the list representing the order of choice.
     */
    private final List<Byte> rotorsChosen = new ArrayList<>(3);

    /**
     * Button to enable the plugBoard.
     */
    private final JButton plugBoardButton;

    /**
     * Button to enable manually setting the rotor rotation.
     */
    private final JButton rotorSet;

    /**
     * List of buttons representing the plus and minus buttons the user uses to
     * set the rotors.
     */
    private final List<JButton> rotorPlusMinus = new ArrayList<>(6);

    /**
     * Button used to clear the original and coded message text.
     */
    private final JButton clearTextButton;

    /**
     * The original message typed by the user.
     */
    private final JTextArea originalMessage;

    /**
     * The encoded message returned by the machine.
     */
    private final JTextArea codedMessage;

    /**
     * A list of labels that show the current rotation of each rotor.
     */
    private final List<JLabel> rotorDisplay = new ArrayList<>(3);

    /**
     * The text field that the user enters letters into to encrypt them.
     */
    private final JTextField message;

    /**
     * The Enigma machine used by this panel.
     */
    private final Enigma enigma;

    /**
     * The listener used by the panel.
     */
    private final EnigmaListener listener = new EnigmaListener();

    /**
     * The amount of steps each rotor is currently rotated.
     */
    private final byte[] rotorRotation = new byte[3];

    /**
     * THe plugBoard. It's a list of Character lists, creating essentially a
     * list of pair representing the current plug pairs in use.
     */
    private List<List<Character>> plugBoard = new ArrayList<>(ALPHABET_LENGTH);

    /**
     * A list of text fields that represent the individual plugs on the
     * plugBoard.
     */
    private final List<JTextField> plugs = new ArrayList<>(ALPHABET_LENGTH);

    /**
     * A basic Consumer that I use to clear the text of whatever text component
     * I send to it.
     */
    private final Consumer<JTextComponent> clearText = (field) -> field
            .setText("");

    /**
     * The constructor for this panel. It calls a method to let the user pick 3
     * rotors for use in the program. Setting up multiple panels and complex
     * layouts was too complicated, so I remove the layout and position things
     * absolutely. This method mostly just gives values to variables, and adds
     * them to the panel.
     *
     * @throws IOException
     *             Exception thrown if config file cannot be found for the
     *             enigma constructor.
     */
    public EnigmaPanel() throws IOException {
        // Used to prevent null pointer exceptions.
        for (int i = 0; i < 3; i++) {
            rotorsChosen.add(null);
            rotorDisplay.add(i, new JLabel("0"));
        }

        chooseRotors();
        setLayout(null);
        enigma = new Enigma();
        rotors = enigma.getRotors();

        final int textWidth = 10;
        final int textHeight = 5;
        message = new JTextField();

        originalMessage = new JTextArea(10, 5);
        originalMessage.setEnabled(false);
        originalMessage.setDisabledTextColor(Color.black);

        codedMessage = new JTextArea(10, 5);
        codedMessage.setEnabled(false);
        codedMessage.setDisabledTextColor(Color.black);

        final FontMetrics metrics = originalMessage
                .getFontMetrics(originalMessage.getFont());
        final int fontHeight = metrics.getHeight();
        final int fontWidth = metrics.getMaxAdvance();

        final List<JLabel> plugLabels = new ArrayList<>(ALPHABET_LENGTH);
        JTextField temp;
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            temp = new JTextField();
            temp.setEnabled(false);
            temp.setDocument(new JTextFieldLimit(1));
            temp.addKeyListener(listener);
            temp.setDisabledTextColor(Color.black);
            plugs.add(temp);
            plugLabels.add(new JLabel(String.valueOf(ALPHABET.get(i))));
        }

        message.setDocument(new JTextFieldLimit(1));

        plugBoardButton = new JButton("Set Plugboard");
        rotorSet = new JButton("Set Rotors");
        clearTextButton = new JButton("Clear Text");

        message.addActionListener(listener);
        plugBoardButton.addActionListener(listener);
        rotorSet.addActionListener(listener);
        clearTextButton.addActionListener(listener);

        final int buttonWidth = 115;
        final int buttonHeight = 30;
        final int buttonX = 270;
        final int rotorButtonY = 240;
        final int plugBoardButtonY = 280;
        final int clearTextButtonY = 320;
        plugBoardButton.setBounds(buttonX, plugBoardButtonY, buttonWidth,
                buttonHeight);
        rotorSet.setBounds(buttonX, rotorButtonY, buttonWidth, buttonHeight);
        clearTextButton.setBounds(buttonX, clearTextButtonY, buttonWidth,
                buttonHeight);

        final int messageX = 320;
        final int messageY = 20;
        message.setBounds(messageX, messageY, fontWidth, fontHeight);

        final int originalMessageX = 80;
        final int codedMessageX = 350;
        final int outputMessageY = 130;
        originalMessage.setBounds(originalMessageX, outputMessageY, fontWidth
                * textWidth, fontHeight * textHeight);
        codedMessage.setBounds(codedMessageX, outputMessageY, fontWidth
                * textWidth, fontHeight * textHeight);

        int rotorLabelX = 292;
        final int rotorLabelY = 70;
        final int rotorLabelSize = 20;
        for (final JLabel display : rotorDisplay) {
            display.setBounds(rotorLabelX, rotorLabelY, rotorLabelSize,
                    rotorLabelSize);
            rotorLabelX += 40;
        }

        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                rotorPlusMinus.add(new JButton("+"));
            } else {
                rotorPlusMinus.add(new JButton("-"));
            }
        }

        int plusMinusX = 285;
        int plusMinusY = 50;
        final int plusMinusSize = 20;
        for (final JButton button : rotorPlusMinus) {
            button.setBounds(plusMinusX, plusMinusY, plusMinusSize,
                    plusMinusSize);
            plusMinusY = (plusMinusY == 50) ? 90 : 50;
            if (rotorPlusMinus.indexOf(button) % 2 != 0) {
                plusMinusX += 40;
            }
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addActionListener(listener);
            add(button);
            button.setVisible(false);
        }

        int plugX = 20;
        int plugY = 410;
        for (int i = 0; i < plugs.size(); i++) {
            plugs.get(i).setBounds(plugX, plugY, fontWidth, fontHeight);
            plugLabels.get(i).setBounds(plugs.get(i).getX() + (fontWidth / 3),
                    plugs.get(i).getY() - 20, fontWidth, fontHeight);

            plugY = (plugY == 410) ? 490 : 410;

            if (i % 2 != 0) {
                plugX += 50;
            }
            add(plugs.get(i));
            add(plugLabels.get(i));
        }

        add(message);
        add(plugBoardButton);
        add(rotorSet);
        add(clearTextButton);
        rotorDisplay.forEach(label -> add(label));
        add(originalMessage);
        add(codedMessage);

        message.addKeyListener(listener);
    }

    /**
     * Paint component for the panel.
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Formats the rotor rotation in the program. Bumps the next rotor if it
     * passes 25, then sets the rotation based on that value.
     */
    private void formatRotorSettings() {
        final int length = rotorRotation.length - 1;
        for (int i = length; i > -1; i--) {
            if (rotorRotation[i] < 0) {
                rotorRotation[i] = 25;
            } else if (rotorRotation[i] > 25) {
                if (i > 0) {
                    rotorRotation[i - 1]++;
                }
                rotorRotation[i] = 0;
            }
            rotors.get(rotorsChosen.get(i)).setRotation(rotorRotation[i]);
        }

        for (int i = 0; i < rotorDisplay.size(); i++) {
            rotorDisplay.get(i).setText(String.valueOf(rotorRotation[i]));
        }
    }

    /**
     * The method used to launch the popup where the user picks which rotors to
     * use.
     */
    private void chooseRotors() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        final JLabel rotorChangeDialog = new JLabel(
                "Please select three rotors");
        final JLabel selectedRotorsMessage = new JLabel(
                "You have selected rotors: ");

        panel.add(rotorChangeDialog);
        rotorCheckBox.forEach(check -> panel.add(check));
        panel.add(selectedRotorsMessage);
        panel.add(displayRotorsLabel);

        boolean areThreeRotorsChosen = false;
        rotorCheckBox.forEach(check -> check.addItemListener(listener));

        JOptionPane.showConfirmDialog(null, panel, "Rotor Selection",
                JOptionPane.DEFAULT_OPTION);
        while (!areThreeRotorsChosen) {
            if (listener.getCurrentSelections() == 3) {
                areThreeRotorsChosen = true;
            } else {
                JOptionPane.showConfirmDialog(null, panel, "Rotor Selection",
                        JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    /**
     * Adds a pair to the plugboard using the arguments passed to it.
     *
     * @param first
     *            The first letter in the pair.
     * @param secondIndex
     *            The index of the second letter in the alphabet.
     */
    private void updatePlugBoard(final char first, final int secondIndex) {
        final List<Character> pair = new ArrayList<>(2);
        pair.add(first);
        pair.add(ALPHABET.get(secondIndex));
        plugBoard.add(pair);
    }

    /**
     * This method removes a pair from the plugboard based on the char passed to
     * it. It streams the plugboard, filters it to contain every pair that
     * doesn't have firstHalf and then stores that back into plugBoard.
     *
     * @param firstHalf
     *            One of the letters of the pair that s to be removed.
     */
    private void removePlug(final char firstHalf) {
        plugBoard = plugBoard.stream()
                .filter(plug -> !(plug.contains(firstHalf)))
                .collect(Collectors.toList());
    }

    /**
     * Listener class that implements all necessary listeners for the panel.
     *
     * @author John Agapeyev
     *
     */
    private class EnigmaListener extends KeyAdapter implements ItemListener,
            ActionListener {

        /**
         * Any alphabetic character or a backspace.
         */
        private final Pattern alpha = Pattern.compile("[a-zA-Z\b]");

        /**
         * Matcher for the above pattern.
         */
        private Matcher match;

        /**
         * Max Number of rotor selections.
         */
        private final int MAX_SELECTIONS = 3;

        /**
         * Current number of rotor selections.
         */
        private int currentSelections = 0;

        /**
         * I was having trouble retrieving the text value of a plug after the
         * backspace was pressed, so this list is a cache of previous text
         * values for each plug.
         */
        private final List<char[]> textChangeCache = new ArrayList<>(
                ALPHABET_LENGTH);

        /**
         * Is the user currently changing rotors?
         */
        private boolean changingRotors;

        /**
         * Is the user currently changing the plugBoard?
         */
        private boolean changingPlugs;

        public EnigmaListener() {
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                textChangeCache.add(new char[2]);
                textChangeCache.get(i)[0] = ' ';
                textChangeCache.get(i)[1] = ' ';
            }
            changingRotors = false;
            changingPlugs = false;
        }

        /**
         * Method called whenever a keyboard key is released.
         */
        @Override
        public void keyReleased(final KeyEvent e) {

            final char letter = Character.toUpperCase(e.getKeyChar());
            final Component source = e.getComponent();
            match = alpha.matcher(Character.toString(letter));
            if (!match.matches()) {
                return;
            } else {
                if (source == message) {
                    /*
                     * Won't encrypt unless the text is alphabetic and neither
                     * the rotors nor plugboard are being altered
                     */
                    if (e.getKeyCode() == 8 || changingRotors || changingPlugs) {
                        clearText.accept(message);
                        return;
                    } else {
                        clearText.accept(message);
                        originalMessage.setText(originalMessage.getText()
                                + letter);
                        rotorRotation[2]++;
                        formatRotorSettings();
                        codedMessage
                                .setText(codedMessage.getText()
                                        + enigma.encode(letter, rotorsChosen,
                                                plugBoard));

                        final String originText = originalMessage.getText();
                        final String codeText = codedMessage.getText();
                        int length = originText.length();

                        for (int i = 0; i < length; i++) {
                            if (originText.charAt(i) == ' ') {
                                length--;
                            }
                        }

                        /*
                         * Basic function that groups output in groups of 5 and
                         * forces new lines after a certain length.
                         */
                        if (length % 25 == 0) {
                            originalMessage.setText(originText + "\n");
                            codedMessage.setText(codeText + "\n");
                        } else if (length % 5 == 0) {
                            originalMessage.setText(originText + " ");
                            codedMessage.setText(codeText + " ");
                        }
                    }
                } else if (plugs.contains(source)) {
                    final int userAlteredPlugIndex = plugs.indexOf(source);
                    final char[] userAlteredPlugCache = textChangeCache
                            .get(userAlteredPlugIndex);

                    if (e.getKeyCode() == 8) {
                        if (userAlteredPlugCache[1] == ' ') {
                            return;
                        }

                        /*
                         * The following code shifts the cache for both the plug
                         * the user typed in, and the other plug in the pair. It
                         * also clears the text and removes the plug from
                         * plugBoard.
                         */

                        final char[] userTypedPlugCache = textChangeCache
                                .get(ALPHABET.indexOf(userAlteredPlugCache[1]));

                        userAlteredPlugCache[0] = userAlteredPlugCache[1];
                        userAlteredPlugCache[1] = ' ';

                        clearText.accept(plugs.get(ALPHABET
                                .indexOf(userAlteredPlugCache[0])));

                        removePlug(userAlteredPlugCache[0]);

                        userTypedPlugCache[0] = userTypedPlugCache[1];
                        userTypedPlugCache[1] = ' ';

                    } else {
                        final int letterIndex = ALPHABET.indexOf(letter);

                        if (!plugs
                                .get(letterIndex)
                                .getText()
                                .equals(String.valueOf(ALPHABET
                                        .get(userAlteredPlugIndex)))) {

                            if (!(userAlteredPlugCache[1] == ' ')) {
                                JOptionPane
                                        .showMessageDialog(
                                                null,
                                                ALPHABET.get(userAlteredPlugIndex)
                                                        + " is already in use. Please delete its "
                                                        + "value before trying to change it.");
                                return;
                            }

                            if (!plugs.get(letterIndex).getText().equals("")) {
                                JOptionPane.showMessageDialog(null, letter
                                        + " is already taken. Please "
                                        + "choose another plug or delete its"
                                        + " value first.");
                                clearText.accept((JTextField) source);
                                return;
                            }

                        }

                        /*
                         * Similar to above, but adds plugs and text instead of
                         * removing it.
                         */

                        updatePlugBoard(letter, userAlteredPlugIndex);

                        userAlteredPlugCache[0] = userAlteredPlugCache[1];
                        userAlteredPlugCache[1] = letter;

                        plugs.get(letterIndex).setText(
                                String.valueOf(ALPHABET
                                        .get(userAlteredPlugIndex)));

                        textChangeCache.get(letterIndex)[0] = textChangeCache
                                .get(letterIndex)[1];
                        textChangeCache.get(letterIndex)[1] = ALPHABET
                                .get(userAlteredPlugIndex);
                    }
                }
            }
        }

        /**
         * Method called whenever an action event is generated.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object source = e.getSource();
            if (source == plugBoardButton) {
                if (changingPlugs) {
                    changingPlugs = false;
                    plugBoardButton.setText("Set Plugboard");
                    plugs.forEach(plug -> plug.setEnabled(false));
                    clearText.accept(originalMessage);
                    clearText.accept(codedMessage);
                } else {
                    plugBoardButton.setText("Done");
                    changingPlugs = true;
                    plugs.forEach(plug -> plug.setEnabled(true));
                }
            } else if (source == rotorSet) {
                if (changingRotors) {
                    rotorSet.setText("Set Rotors");
                    changingRotors = false;
                    rotorPlusMinus.forEach(button -> button.setVisible(false));
                } else {
                    rotorSet.setText("Done");
                    changingRotors = true;
                    rotorPlusMinus.forEach(button -> button.setVisible(true));
                }
            } else if (source == clearTextButton) {
                clearText.accept(originalMessage);
                clearText.accept(codedMessage);
            } else {
                if (source == rotorPlusMinus.get(0)) {
                    rotorRotation[0]++;
                } else if (e.getSource() == rotorPlusMinus.get(1)) {
                    rotorRotation[0]--;
                } else if (e.getSource() == rotorPlusMinus.get(2)) {
                    rotorRotation[1]++;
                } else if (e.getSource() == rotorPlusMinus.get(3)) {
                    rotorRotation[1]--;
                } else if (e.getSource() == rotorPlusMinus.get(4)) {
                    rotorRotation[2]++;
                } else if (e.getSource() == rotorPlusMinus.get(5)) {
                    rotorRotation[2]--;
                }
                formatRotorSettings();
            }
        }

        /**
         * Method called whenever an item's state is changed. In practicality,
         * that only refers to the checkboxes on the popup as nothing else calls
         * this method.
         */
        @Override
        public void itemStateChanged(final ItemEvent e) {
            final JCheckBox source = (JCheckBox) e.getSource();
            final int length = 3;
            if (source.isSelected()) {
                currentSelections++;
                for (int i = 0; i < length; i++) {
                    if (rotorsChosen.get(i) == null) {
                        rotorsChosen.set(i,
                                (byte) rotorCheckBox.indexOf(source));
                        break;
                    }
                }
                if (currentSelections == MAX_SELECTIONS) {
                    rotorCheckBox.forEach(check -> {
                        if (!check.isSelected()) {
                            check.setEnabled(false);
                        }
                    });
                }
            } else {
                currentSelections--;
                for (int i = 0; i < length; i++) {
                    if (rotorsChosen.get(i) != null
                            && rotorsChosen.get(i) == rotorCheckBox
                                    .indexOf(source)) {
                        rotorsChosen.set(i, null);

                        int firstNullIndex = 0;
                        for (int j = 0; j < length; j++) {
                            if (rotorsChosen.get(j) == null) {
                                firstNullIndex = j;
                                break;
                            }
                        }
                        for (int j = firstNullIndex; j < length - 1; j++) {
                            rotorsChosen.set(j, rotorsChosen.get(j + 1));
                        }
                        rotorsChosen.set(length - 1,
                                rotorsChosen.get(firstNullIndex));
                    }
                }
                if (currentSelections < MAX_SELECTIONS) {
                    rotorCheckBox.forEach(check -> check.setEnabled(true));
                }
            }
            String display = "";
            for (int i = 0; i < length; i++) {
                if (rotorsChosen.get(i) == null) {
                    display += "Empty   ";
                } else {
                    display += (rotorsChosen.get(i) + 1) + "   ";
                }
            }
            displayRotorsLabel.setText(display);
        }

        /**
         * Getter Method used to ensure the user picked 3 rotors.
         *
         * @return The number of selected checkboxes.
         */
        private int getCurrentSelections() {
            return currentSelections;
        }
    }
}
