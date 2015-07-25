package machine.enigma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

/**
 * This is where all the encryption happens in the program. It will read from a
 * config file and load up user settings if found, otherwise it will default to
 * random generation or pre-set values.
 *
 * @author John Agapeyev
 *
 */
public class Enigma {

    /**
     * This is a list of Characters, representing the alphabet.
     */
    public static final List<Character> ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().chars().mapToObj(c -> (char) c)
            .collect(Collectors.toList());

    /**
     * The length of the alphabet.
     */
    public static final int ALPHABET_LENGTH = 26;

    /**
     * An arbitrary number that is used to tell my encryption method when it is
     * using the reflector instead of a rotor.
     */
    private static final byte REFLECTOR_CODE = 100;

    /**
     * Total number of rotors to choose from.
     */
    private final int rotorLength = 5;

    /**
     * The rotors for the program.
     */
    private final List<Rotor> rotors = new ArrayList<>(rotorLength);

    /**
     * The reflector for the program.
     */
    private Reflector reflector;

    /**
     * The name of the config file.
     */
    private final String fileName = "config.ini";

    /**
     * Default names for all options.
     */
    private final String[] optionKey = {"user_settings_saved",
            "default_rotor_rand", "default_reflector_rand", "user_rotor_1",
            "user_rotor_2", "user_rotor_3", "user_reflector", "user_plugboard",
            "rotor_1", "rotor_2", "rotor_3", "rotor_4", "rotor_5", "reflector"};

    /**
     * Default values for all options.
     */
    private final String[] optionValue = {"false", "true", "true", "", "", "",
            "", "", "EKMFLGDQVZNTOWYHXUSPAIBRCJ    Q",
            "AJDKSIRUXBLHWTMCQGZNPYFVOE    E",
            "BDFHJLCPRTXVZNYEIWGAKMUSQO    V",
            "ESOVPZJAYQUIRHXLNFTGKDCMWB    J",
            "VZBRGITYUPSDNHLXAWMJQOFECK    Z", "YRUHQSLDPXNGOKMIEBFZCWVJAT"};

    /**
     * Does the config file contain user settings?
     */
    private boolean userRotorsLoaded = false;

    /**
     * The plugBoard. It's a list of Character lists, creating essentially a
     * list of pairs representing the current plug pairs in use.
     */
    private List<List<Character>> plugBoard = new ArrayList<>(
            ALPHABET_LENGTH / 2);

    /**
     * This is the constructor for this class. It reads all the lines in the
     * config file, splits them on the whitespace, and then iterates through
     * each one, performing actions based on the value.
     */
    public Enigma() {

        // Used to prevent null pointer exceptions further in the constructor.
        for (int i = 0; i < rotorLength; i++) {
            rotors.add(null);
        }

        /*
         * Read all lines of the config file and sets the rotors accordingly.
         * Each line is split based on whitespace so that index 0 is the name,
         * index 1 is the value, and in the cases of the rotors, index 2 is the
         * turnover point. Certain settings take priority, with user settings
         * being highest priority, followed by random generation, then the
         * default values.
         */
        try {
            final Path configPath = FileSystems.getDefault().getPath(fileName);
            Files.lines(configPath).forEach(line -> {
                final String[] keyValue = line.split("\\s+");
                // User Settings
                if (keyValue[0].equals(optionKey[0])) {
                    userRotorsLoaded = Boolean.valueOf(keyValue[1]);
                    final int fourthRotorDefaultKeyIndex = 11;
                    final int fifthRotorDefaultKeyIndex = 12;
                    final String[] fourthRotorDefaultKey = optionValue[fourthRotorDefaultKeyIndex]
                            .split("\\s+");
                    final String[] fifthRotorDefaultKey = optionValue[fifthRotorDefaultKeyIndex]
                            .split("\\s+");
                    setRotor(3, fourthRotorDefaultKey[0],
                            fourthRotorDefaultKey[1]);
                    setRotor(4, fifthRotorDefaultKey[0],
                            fifthRotorDefaultKey[1]);
                    // Loads rotor, reflector, and plugboard on further
                    // iterations if settings are found.
                } else if (userRotorsLoaded) {
                    if (keyValue[0].startsWith("user_rotor")) {
                        setRotor(Integer.parseInt(
                                keyValue[0].replaceAll("[\\D]", "")) - 1,
                                keyValue[1], keyValue[2]);
                    } else if (keyValue[0].equals(optionKey[6])) {
                        setReflector(keyValue[1]);
                    } else if (keyValue[0].equals(optionKey[7])) {
                        if (keyValue.length > 1) {
                            Arrays.asList(keyValue[1].split("#"))
                                    .forEach(pair -> {
                                final List<Character> plugPair = new ArrayList<>(
                                        2);
                                for (final char letter : pair.toCharArray()) {
                                    plugPair.add(letter);
                                }
                                plugBoard.add(plugPair);
                            });
                        }
                    }
                } else {
                    // Random Generation
                    if (keyValue[0].equals(optionKey[1])) {
                        if (Boolean.valueOf(keyValue[1])) {
                            for (int i = 0; i < rotorLength; i++) {
                                rotors.set(i, new Rotor());
                            }
                        }
                    } else if (keyValue[0].equals(optionKey[2])) {
                        if (Boolean.valueOf(keyValue[1])) {
                            reflector = new Reflector();
                        }
                    } else {
                        // Default values
                        if (!rotors.contains(null) && reflector != null) {
                            return;
                        } else {
                            if (keyValue[0].startsWith("rotor_")) {
                                setRotor(
                                        Integer.parseInt(keyValue[0]
                                                .replaceAll("[\\D]", "")) - 1,
                                        keyValue[1], keyValue[2]);
                            } else if (keyValue[0].equals(optionKey[13])) {
                                setReflector(keyValue[1]);
                            }
                        }
                    }
                }
            });
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "\" " + fileName
                    + "\" could not be located. No settings may be saved, and all "
                    + "encryptions keys will be set as their default values.");
            for (int i = 0; i < rotorLength; i++) {
                final String[] keyTurnoverSplit = optionValue[i + 8]
                        .split("\\s+");
                setRotor(i, keyTurnoverSplit[0], keyTurnoverSplit[1]);
            }
            setReflector(optionValue[13]);
        } catch (final ArrayIndexOutOfBoundsException f) {
            JOptionPane.showMessageDialog(null,
                    "User settings could not be found, yet " + fileName
                            + " says that values exist. Please change "
                            + optionKey[0] + " to false in " + fileName
                            + " then restart the program.");
            System.exit(1);
        }
    }

    /**
     * Writes to the config file using the settings provided. No settings are
     * changed, those are altered in the two methods lower down. This method
     * only writes to file.
     */
    private void saveSettings() {
        try {
            final BufferedWriter fileWriter = new BufferedWriter(
                    new FileWriter(fileName));
            for (int i = 0, fileLength = optionKey.length; i < fileLength; i++) {
                fileWriter.write(optionKey[i] + " " + optionValue[i]);
                fileWriter.newLine();
            }
            fileWriter.close();
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "\" " + fileName
                    + "\" could not be located. No settings will be saved.");
        }
    }

    /**
     * Sets the user settings based on the current rotors, reflector, and
     * pluboard, followed by saving the settings.
     *
     * @param rotorsChosen
     *            A list representing the rotors chosen and their index being
     *            the order.
     */
    public void createUserSettings(final List<Byte> rotorsChosen) {
        StringBuilder rotorBuild = new StringBuilder(ALPHABET_LENGTH + 1);
        final StringBuilder reflectorBuild = new StringBuilder(ALPHABET_LENGTH);
        final List<Character> reflectorKey = reflector.getKey();

        optionValue[0] = "true";

        for (int i = 0; i < 3; i++) {
            final List<Character> rotorKey = rotors.get(rotorsChosen.get(i))
                    .getOriginalKey();
            for (int j = 0; j < ALPHABET_LENGTH; j++) {
                rotorBuild.append(rotorKey.get(j));
            }
            rotorBuild.append("    ");
            rotorBuild.append(rotors.get(rotorsChosen.get(i)).getTurnover());
            optionValue[i + 3] = rotorBuild.toString();
            rotorBuild = new StringBuilder(ALPHABET_LENGTH + 1);
        }
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            reflectorBuild.append(reflectorKey.get(i));
        }
        final StringBuilder plugBuild = new StringBuilder();
        plugBoard.forEach(pair -> {
            pair.forEach(letter -> plugBuild.append(letter));
            plugBuild.append("#");
        });

        optionValue[6] = reflectorBuild.toString();
        optionValue[7] = plugBuild.toString();
        saveSettings();
    }

    /**
     * Removes any user settings before saving them.
     */
    public void deleteSettings() {
        optionValue[0] = "false";
        for (int i = 3; i < 7; i++) {
            optionValue[i] = "";
        }
        saveSettings();
    }

    /**
     * Sets the rotor of the given index using the key and turnover point
     * provided.
     *
     * @param index
     *            Index of the Rotor.
     * @param key
     *            Encryption Key.
     * @param turnOver
     *            TurnOverPoint of the rotor.
     */
    private void setRotor(final int index, final String key,
            final String turnOver) {
        rotors.set(index,
                new Rotor(
                        key.toUpperCase().chars().mapToObj(c -> (char) c)
                                .collect(Collectors.toList()),
                        turnOver.charAt(0)));
    }

    /**
     * Sets the reflector using the given key.
     *
     * @param key
     *            The key for the reflector.
     */
    private void setReflector(final String key) {
        reflector = new Reflector(key.toUpperCase().chars()
                .mapToObj(c -> (char) c).collect(Collectors.toList()));
    }

    /**
     * This is the main encryption program. It handles all the work of
     * encrypting the letter sent to it by the user. It does this by calling two
     * methods: One to swap the value through the plugboard, the other to make
     * it run through a rotor.
     *
     * @param sentLetter
     *            The letter sent to be encoded.
     * @param rotorsChosen
     *            A list of the three rotors chosen by the user, with their
     *            index representing the order.
     * @param plugBoard
     *            The plugBoard set by the user.
     * @return The encoded letter.
     */
    public char encode(final char sentLetter, final List<Byte> rotorsChosen) {
        char output = sentLetter;
        boolean isReverse = false;

        // PlugBoard swap
        output = plugBoardSwap(output);

        // Normal encryption
        for (int i = rotorsChosen.size() - 1; i > -1; i--) {
            output = rotorEncryption(output, rotorsChosen.get(i), isReverse);
        }

        // Reflector
        output = rotorEncryption(output, REFLECTOR_CODE, isReverse);

        isReverse = true;

        // Return back through the rotors in reverse order.
        for (final Byte rotor : rotorsChosen) {
            output = rotorEncryption(output, rotor, isReverse);
        }

        // PlugBoard swap again
        output = plugBoardSwap(output);

        return output;
    }

    /**
     * This is the class that does the main substitution cipher. It gets a key
     * depending on rotorNumber and then does a multi-step substitution on it
     * and returns the output.
     *
     * @param letter
     *            Letter sent to it by the encryption method.
     * @param rotorNumber
     *            The current rotor it encrypts through
     * @param isReverse
     *            Whether the process happens in reverse or not.
     * @return The encrypted letter for the current step.
     */
    private char rotorEncryption(final char letter, final byte rotorNumber,
            final boolean isReverse) {

        List<Character> rotorKey = new ArrayList<>(ALPHABET_LENGTH);
        List<Character> alphabetKey = new ArrayList<>(ALPHABET_LENGTH);
        char response = letter;

        /*
         * Gets the key to be used based on rotorNumber, along with the alphabet
         * representing the offset
         */
        if (rotorNumber == REFLECTOR_CODE) {
            rotorKey = reflector.getKey();
            alphabetKey.clear();
            ALPHABET.forEach(alphabetKey::add);
        } else {
            rotorKey = rotors.get(rotorNumber).getKey();
            alphabetKey = rotors.get(rotorNumber).getAlphabet();
        }

        if (!isReverse) {

            // External alphabet gets turned into internal alphabet
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == alphabetKey.get(i)) {
                    response = ALPHABET.get(i);
                    break;
                }
            }

            // Internal alphabet gets turned into rotor wiring
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == alphabetKey.get(i)) {
                    response = rotorKey.get(i);
                    break;
                }
            }

            /*
             * Rotor output is returned through the shifted alphabet key once
             * again on its way out
             */
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == ALPHABET.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }

        } else {

            // Input is shifted through the rotor offset
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == alphabetKey.get(i)) {
                    response = ALPHABET.get(i);
                    break;
                }
            }

            // Key is transformed into the rotor offset
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == rotorKey.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }

            // Offset is transformed into the original alphabet
            for (int i = 0; i < ALPHABET_LENGTH; i++) {
                if (response == ALPHABET.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }
        }
        return response;
    }

    /**
     * This method emulates the enigma plugboard, swapping letters in pairs
     * based on how it's set up. If the letter is found to be in a pair, it will
     * be returned as its partner letter. If the letter is not used, the loop
     * will end, and the original letter will be returned instead.
     *
     * @param plugBoard
     *            A list of pairs of characters representing the plugBoard.
     * @param letter
     *            The letter to be swapped.
     * @return The swapped letter.
     */
    private char plugBoardSwap(final char letter) {
        char output = letter;
        for (final List<Character> pair : plugBoard) {
            if (pair.contains(letter)) {
                output = (output == pair.get(0)) ? pair.get(1) : pair.get(0);
                break;
            }
        }
        return output;
    }

    /**
     * Adds a pair to the plugboard using the arguments passed to it.
     *
     * @param first
     *            The first letter in the pair.
     * @param secondIndex
     *            The index of the second letter in the alphabet.
     */
    public void updatePlugBoard(final char first, final int secondIndex) {
        final List<Character> pair = new ArrayList<>(2);
        pair.add(first);
        pair.add(ALPHABET.get(secondIndex));
        plugBoard.add(pair);
    }

    /**
     * This method removes a pair from the plugboard based on the char passed to
     * it.
     *
     * @param firstHalf
     *            One of the letters of the pair that is to be removed.
     */
    public void removePlug(final char firstHalf) {
        plugBoard.removeIf(plug -> plug.contains(firstHalf));
    }

    /**
     * Getter Method.
     *
     * @return All 5 of the rotors that can be used with the machine.
     */
    public List<Rotor> getRotors() {
        return rotors;
    }

    /**
     * Getter Method.
     *
     * @return Whether the program has loaded any rotors from file.
     */
    public boolean getUserRotorsLoaded() {
        return userRotorsLoaded;
    }

    /**
     * Getter Method.
     *
     * @return The plugboard.
     */
    public List<List<Character>> getPlugBoard() {
        return plugBoard;
    }
}
