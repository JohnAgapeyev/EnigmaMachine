package machine.enigma;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Box;
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
    private final List<JCheckBox> rotorCheckBox = Arrays.asList(
            new JCheckBox("Rotor 1"), new JCheckBox("Rotor 2"),
            new JCheckBox("Rotor 3"), new JCheckBox("Rotor 4"),
            new JCheckBox("Rotor 5"));

    /**
     * A display for the popup of which rotors are currently chosen.
     */
    private final JLabel displayRotorsLabel = new JLabel("");

    /**
     * A list representing the rotors that are chosen by the user, with their
     * index in the list representing the order of choice.
     */
    private List<Byte> rotorsChosen;

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
     * Button used if the user wants to reselect the rotors to be used.
     */
    private final JButton chooseRotors;

    /**
     * Button used if the user wants to change the options.
     */
    private final JButton optionsButton;

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
    private List<JLabel> rotorDisplay;

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
     * layouts was outside of my abilities when I made this project, so I
     * removed the layout and positioned things absolutely. This method mostly
     * just gives values to variables, and adds them to the panel.
     *
     * @throws IOException
     *             Exception thrown if config file cannot be found for the
     *             enigma constructor.
     */
    public EnigmaPanel() {
        chooseRotors();
        setLayout(null);
        enigma = new Enigma();
        rotors = enigma.getRotors();

        message = new JTextField();

        originalMessage = new JTextArea();
        originalMessage.setEnabled(false);
        originalMessage.setDisabledTextColor(Color.black);

        codedMessage = new JTextArea();
        codedMessage.setEnabled(false);
        codedMessage.setDisabledTextColor(Color.black);

        final FontMetrics metrics = originalMessage
                .getFontMetrics(originalMessage.getFont());
        final int fontHeight = metrics.getHeight();
        final int fontWidth = metrics.getMaxAdvance();

        final int characterLimit = 1;
        final List<JLabel> plugLabels = new ArrayList<>(ALPHABET_LENGTH);
        JTextField temp;
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            temp = new JTextField();
            temp.setEnabled(false);
            temp.setDocument(new JTextFieldLimit(characterLimit));
            temp.addKeyListener(listener);
            temp.setDisabledTextColor(Color.black);
            plugs.add(temp);
            plugLabels.add(new JLabel(String.valueOf(ALPHABET.get(i))));
        }

        message.setDocument(new JTextFieldLimit(characterLimit));

        plugBoardButton = new JButton("Set Plugboard");
        rotorSet = new JButton("Set Rotors");
        clearTextButton = new JButton("Clear Text");
        chooseRotors = new JButton("Choose Rotors");
        optionsButton = new JButton("Options");

        message.addActionListener(listener);
        plugBoardButton.addActionListener(listener);
        rotorSet.addActionListener(listener);
        clearTextButton.addActionListener(listener);
        chooseRotors.addActionListener(listener);
        optionsButton.addActionListener(listener);

        final int buttonWidth = 125;
        final int buttonHeight = 30;
        final int buttonX = 270;
        final int rotorButtonY = 240;
        final int plugBoardButtonY = 280;
        final int clearTextButtonY = 320;
        final int chooseRotorsButtonY = 360;
        final int optionButtonY = 400;
        plugBoardButton.setBounds(buttonX, plugBoardButtonY, buttonWidth,
                buttonHeight);
        rotorSet.setBounds(buttonX, rotorButtonY, buttonWidth, buttonHeight);
        clearTextButton.setBounds(buttonX, clearTextButtonY, buttonWidth,
                buttonHeight);
        chooseRotors.setBounds(buttonX, chooseRotorsButtonY, buttonWidth,
                buttonHeight);
        optionsButton.setBounds(buttonX, optionButtonY, buttonWidth,
                buttonHeight);

        final int messageX = 320;
        final int messageY = 20;
        message.setBounds(messageX, messageY, fontWidth, fontHeight);

        final int textWidth = 10;
        final int textHeight = 5;
        final int originalMessageX = 80;
        final int codedMessageX = 350;
        final int outputMessageY = 130;
        originalMessage.setBounds(originalMessageX, outputMessageY,
                fontWidth * textWidth, fontHeight * textHeight);
        codedMessage.setBounds(codedMessageX, outputMessageY,
                fontWidth * textWidth, fontHeight * textHeight);

        int rotorLabelX = 292;
        final int rotorLabelY = 70;
        final int rotorLabelSize = 20;
        for (final JLabel display : rotorDisplay) {
            display.setBounds(rotorLabelX, rotorLabelY, rotorLabelSize,
                    rotorLabelSize);
            rotorLabelX += 40;
        }

        for (int i = 0, limit = 6; i < limit; i++) {
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
        int plugY = 460;
        final int plugTopY = plugY;
        final int plugBottomY = 540;
        final int plugLabelOffset = 20;
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            plugs.get(i).setBounds(plugX, plugY, fontWidth, fontHeight);
            plugLabels.get(i).setBounds(plugs.get(i).getX() + (fontWidth / 3),
                    plugs.get(i).getY() - plugLabelOffset, fontWidth,
                    fontHeight);

            plugY = (plugY == plugTopY) ? plugBottomY : plugTopY;

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
        add(chooseRotors);
        add(optionsButton);
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
     * Formats the rotor rotation in the program. Bumps the next rotor if the
     * previous one reaches its turnover point. Also checks the rotation of the
     * middle and fast rotor to create the double-stepping mechanism.
     */
    private void formatRotorSettings() {
        final int length = rotorRotation.length - 1;
        for (int i = length; i > -1; i--) {
            if (rotorRotation[i] < 0) {
                rotorRotation[i] = 25;
            } else if (rotorRotation[i] > 25) {
                rotorRotation[i] = 0;
            }
            if (!listener.changingRotors) {
                if (rotorRotation[1] == ALPHABET.indexOf(
                        rotors.get(rotorsChosen.get(1)).getTurnover()) + 1
                        && rotorRotation[2] == ALPHABET.indexOf(
                                rotors.get(rotorsChosen.get(2)).getTurnover())
                                + 2) {

                    rotorRotation[1]++;

                } else if (rotorRotation[i] == ALPHABET.indexOf(
                        rotors.get(rotorsChosen.get(i)).getTurnover()) + 1) {
                    if (i > 0) {
                        rotorRotation[i - 1]++;
                    }
                }
            }
            rotors.get(rotorsChosen.get(i)).setRotation(rotorRotation[i]);
        }
        for (int i = 0, limit = rotorDisplay.size(); i < limit; i++) {
            rotorDisplay.get(i)
                    .setText(String.valueOf(ALPHABET.get(rotorRotation[i])));
        }
    }

    /**
     * The method used to launch the popup where the user picks which rotors to
     * use.
     */
    private void chooseRotors() {
        if (rotorsChosen == null && rotorDisplay == null) {
            rotorsChosen = new ArrayList<>(Arrays.asList(null, null, null));
            rotorDisplay = new ArrayList<>(Arrays.asList(new JLabel("A"),
                    new JLabel("A"), new JLabel("A")));
        } else {
            for (int i = 0, rotorChooseLimit = 3; i < rotorChooseLimit; i++) {
                rotorsChosen.set(i, null);
                rotorDisplay.get(i).setText("A");
            }
        }

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final int panelWidth = 10;
        final int panelHeight = 200;
        panel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        final JLabel rotorChangeDialog = new JLabel(
                "Please select three rotors");
        final JLabel selectedRotorsMessage = new JLabel(
                "You have selected rotors: ");
        final String rotorChooseTitle = "Rotor Selection";

        rotorChangeDialog.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedRotorsMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayRotorsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(rotorChangeDialog);
        panel.add(Box.createVerticalGlue());
        rotorCheckBox.forEach(check -> {
            check.addItemListener(listener);
            check.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(check);
        });
        panel.add(selectedRotorsMessage);
        panel.add(Box.createVerticalGlue());
        panel.add(displayRotorsLabel);
        panel.add(Box.createVerticalGlue());

        boolean areThreeRotorsChosen = false;

        do {
            if (listener.currentSelections == listener.MAX_SELECTIONS) {
                areThreeRotorsChosen = true;
            } else {
                JOptionPane.showConfirmDialog(getParent(), panel,
                        rotorChooseTitle, JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
            }
        } while (!areThreeRotorsChosen);
    }

    private void launchOptionMenu() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final String optionsTitle = "Options";
        final JButton deleteSettingsButton = new JButton("Delete settings");
        final JButton saveButton = new JButton("Save");
        final Component elementSeparator = Box
                .createRigidArea(new Dimension(20, 10));

        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteSettingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveButton.addActionListener(actionEvent -> {
            if (listener.changingRotors || listener.changingPlugs) {
                JOptionPane.showMessageDialog(getParent(),
                        "You are in the process of changing values in the program."
                                + " Please complete your changes and press any buttons labeled"
                                + " \"Done\" to lock in your changes. Until changes are locked in,"
                                + " you will be unable to save or delete any settings.");
                return;
            }
            enigma.createUserSettings(rotorsChosen, plugBoard);
        });

        deleteSettingsButton.addActionListener(actionEvent -> {
            if (listener.changingRotors || listener.changingPlugs) {
                JOptionPane.showMessageDialog(getParent(),
                        "You are in the process of changing values in the program."
                                + " Please complete your changes and press any buttons labeled"
                                + " \"Done\" to lock in your changes. Until changes are locked in,"
                                + " you will be unable to save or delete any settings.");
                return;
            }
            enigma.deleteSettings();
        });

        panel.add(saveButton);
        panel.add(elementSeparator);
        panel.add(deleteSettingsButton);

        JOptionPane.showConfirmDialog(getParent(), panel, optionsTitle,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
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
    private class EnigmaListener extends KeyAdapter
            implements ItemListener, ActionListener {

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
                    if (e.getKeyCode() == 8 || changingRotors
                            || changingPlugs) {
                        clearText.accept(message);
                        return;
                    } else {
                        clearText.accept(message);
                        originalMessage
                                .setText(originalMessage.getText() + letter);
                        rotorRotation[2]++;
                        formatRotorSettings();
                        codedMessage.setText(codedMessage.getText() + enigma
                                .encode(letter, rotorsChosen, plugBoard));

                        final String originText = originalMessage.getText();
                        final String codeText = codedMessage.getText();
                        int length = originText.length();

                        /*
                         * Prevent blank spaces from being counted towards the
                         * function below that adds spacing.
                         */
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

                        clearText.accept(plugs.get(
                                ALPHABET.indexOf(userAlteredPlugCache[0])));

                        removePlug(userAlteredPlugCache[0]);

                        userTypedPlugCache[0] = userTypedPlugCache[1];
                        userTypedPlugCache[1] = ' ';

                    } else {
                        final int letterIndex = ALPHABET.indexOf(letter);

                        if (!plugs.get(letterIndex).getText().equals(String
                                .valueOf(ALPHABET.get(userAlteredPlugIndex)))) {

                            if (!(userAlteredPlugCache[1] == ' ')) {
                                JOptionPane.showMessageDialog(getParent(),
                                        ALPHABET.get(userAlteredPlugIndex)
                                                + " is already in use. Please delete its "
                                                + "value before trying to change it.");
                                return;
                            }

                            if (!plugs.get(letterIndex).getText().equals("")) {
                                JOptionPane.showMessageDialog(getParent(),
                                        letter + " is already taken. Please "
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

                        plugs.get(letterIndex).setText(String
                                .valueOf(ALPHABET.get(userAlteredPlugIndex)));

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
            } else if (rotorPlusMinus.contains(source)) {
                final int rotorPlusMinusClickedIndex = rotorPlusMinus
                        .indexOf(source);
                if (rotorPlusMinusClickedIndex % 2 == 0) {
                    rotorRotation[rotorPlusMinusClickedIndex / 2]++;
                } else {
                    rotorRotation[rotorPlusMinusClickedIndex / 2]--;
                }
                formatRotorSettings();
            } else if (source == chooseRotors) {
                rotorCheckBox.forEach(check -> {
                    check.removeItemListener(listener);
                    check.setEnabled(true);
                    check.setSelected(false);
                });
                displayRotorsLabel.setText("");
                clearText.accept(originalMessage);
                clearText.accept(codedMessage);
                listener.currentSelections = 0;
                rotorRotation[0] = 0;
                rotorRotation[1] = 0;
                rotorRotation[2] = 0;
                chooseRotors();
            } else if (source == optionsButton) {
                launchOptionMenu();
            }
        }

        /**
         * Method called whenever an item's state is changed. In practicality,
         * that only refers to the checkboxes on the popup as nothing else calls
         * this method. Whenever a checkbox is selected, it adds that rotor to
         * rotorsChosen and updates the display. When one is deselected, it
         * removes the rotor and then updates the display.
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
                    if (rotorsChosen.get(i) != null && rotorsChosen
                            .get(i) == rotorCheckBox.indexOf(source)) {

                        rotorsChosen.set(i, null);

                        final int numListRotateSteps = -1;
                        Collections.rotate(rotorsChosen
                                .subList(rotorsChosen.indexOf(null), length),
                                numListRotateSteps);
                    }
                }
                if (currentSelections < MAX_SELECTIONS) {
                    rotorCheckBox.forEach(check -> check.setEnabled(true));
                }
            }
            final StringBuilder display = new StringBuilder();
            rotorsChosen.forEach(choice -> {
                if (choice == null) {
                    display.append("Empty   ");
                } else {
                    display.append((choice + 1) + "   ");
                }
            });
            displayRotorsLabel.setText(display.toString());
        }
    }
}
