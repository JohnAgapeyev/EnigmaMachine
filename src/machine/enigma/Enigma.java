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
 * config file and either use default rotors and a reflector, or generate them
 * randomly, depending on the value in the file.
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

    private final int rotorLength = 5;
    private final List<Rotor> rotors = new ArrayList<>(rotorLength);
    private Reflector reflector;

    private final String fileName = "config.ini";

    private final String[] optionKey = {"user_settings_saved",
            "default_rotor_rand", "default_reflector_rand", "user_rotor_1",
            "user_rotor_2", "user_rotor_3", "user_reflector", "user_plugboard",
            "rotor_1", "rotor_2", "rotor_3", "rotor_4", "rotor_5", "reflector"};

    private final String[] optionValue = {"true", "true", "false", "", "", "",
            "", "", "EKMFLGDQVZNTOWYHXUSPAIBRCJ    Q",
            "AJDKSIRUXBLHWTMCQGZNPYFVOE    E",
            "BDFHJLCPRTXVZNYEIWGAKMUSQO    V",
            "ESOVPZJAYQUIRHXLNFTGKDCMWB    J",
            "VZBRGITYUPSDNHLXAWMJQOFECK    Z", "YRUHQSLDPXNGOKMIEBFZCWVJAT"};

    private boolean userRotorsLoaded = false;

    private final List<List<Character>> plugBoard = new ArrayList<>(
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
         * turnover point.
         */
        final Path configPath = FileSystems.getDefault().getPath(fileName);

        try {
            Files.lines(configPath).forEach(line -> {
                final String[] keyValue = line.split("\\s+");
                if (keyValue[0].equals(optionKey[0])) {
                    userRotorsLoaded = Boolean.valueOf(keyValue[1]);
                    final int fourthRotorDefaultKeyIndex = 11;
                    final int fifthRotorDefaultKeyIndex = 12;
                    final String[] fourthRotorDefaultKey = optionValue[fourthRotorDefaultKeyIndex]
                            .split("\\s+");
                    final String[] fifthRotorDefaultKey = optionValue[fifthRotorDefaultKeyIndex]
                            .split("\\s+");
                    rotors.set(3,
                            new Rotor(
                                    fourthRotorDefaultKey[0].toUpperCase()
                                            .chars().mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    fourthRotorDefaultKey[1].charAt(0)));
                    rotors.set(4,
                            new Rotor(
                                    fifthRotorDefaultKey[0].toUpperCase()
                                            .chars().mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    fifthRotorDefaultKey[1].charAt(0)));
                } else if (userRotorsLoaded) {
                    if (keyValue[0].startsWith("user_rotor")) {
                        rotors.set(
                                Integer.parseInt(
                                        keyValue[0].replaceAll("[\\D]", ""))
                                - 1,
                                new Rotor(
                                        keyValue[1].toUpperCase().chars()
                                                .mapToObj(c -> (char) c)
                                                .collect(Collectors.toList()),
                                        keyValue[2].charAt(0)));
                    } else if (keyValue[0].equals(optionKey[6])) {
                        reflector = new Reflector(keyValue[1].toUpperCase()
                                .chars().mapToObj(c -> (char) c)
                                .collect(Collectors.toList()));
                    } else if (keyValue[0].equals(optionKey[7])) {
                        if (!keyValue[1].isEmpty()) {
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
                        if (!rotors.contains(null) && reflector != null) {
                            return;
                        } else {
                            if (keyValue[0].startsWith("rotor_")) {
                                rotors.set(
                                        Integer.parseInt(keyValue[0]
                                                .replaceAll("[\\D]", "")) - 1,
                                        new Rotor(
                                                keyValue[1].toUpperCase()
                                                        .chars()
                                                        .mapToObj(c -> (char) c)
                                                        .collect(Collectors
                                                                .toList()),
                                                keyValue[2].charAt(0)));
                            } else if (keyValue[0].equals(optionKey[13])) {
                                reflector = new Reflector(
                                        keyValue[1].toUpperCase().chars()
                                                .mapToObj(c -> (char) c)
                                                .collect(Collectors.toList()));
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
                rotors.set(i,
                        new Rotor(
                                keyTurnoverSplit[0].toUpperCase().chars()
                                        .mapToObj(c -> (char) c)
                                        .collect(Collectors.toList()),
                                keyTurnoverSplit[1].charAt(0)));
            }
            reflector = new Reflector(optionValue[13].toUpperCase().chars()
                    .mapToObj(c -> (char) c).collect(Collectors.toList()));
        }
    }

    private void saveSettings() {
        try {
            final BufferedWriter fileWriter = new BufferedWriter(
                    new FileWriter(fileName));
            final int fileLength = optionKey.length;
            for (int i = 0; i < fileLength; i++) {
                fileWriter.write(optionKey[i] + " " + optionValue[i]);
                fileWriter.newLine();
            }
            fileWriter.close();
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(null, "\" " + fileName
                    + "\" could not be located. No settings will be saved.");
        }
    }

    public void createUserSettings(final List<Byte> rotorsChosen,
            final List<List<Character>> plugBoard) {
        StringBuilder rotorBuild = new StringBuilder(ALPHABET_LENGTH + 1);
        final StringBuilder reflectorBuild = new StringBuilder(ALPHABET_LENGTH);
        final List<Character> reflectorKey = reflector.getKey();

        optionValue[2] = "true";

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

    public void deleteSettings() {
        optionValue[0] = "false";
        for (int i = 3; i < 7; i++) {
            optionValue[i] = "";
        }
        saveSettings();
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
    public char encode(final char sentLetter, final List<Byte> rotorsChosen,
            final List<List<Character>> plugBoard) {
        char output = sentLetter;
        boolean isReverse = false;

        // PlugBoard swap
        output = plugBoardSwap(plugBoard, output);

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
        output = plugBoardSwap(plugBoard, output);

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
    private char plugBoardSwap(final List<List<Character>> plugBoard,
            final char letter) {
        char output = letter;
        for (final List<Character> pair : plugBoard) {
            if (pair.contains(letter)) {
                output = (output == pair.get(0)) ? pair.get(1) : pair.get(0);
            }
            break;
        }
        return output;
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

    public List<List<Character>> getPlugBoard() {
        return plugBoard;
    }
}
