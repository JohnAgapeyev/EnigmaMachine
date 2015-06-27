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
import java.util.function.BiConsumer;
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

    private List<String> optionKey = new ArrayList<>(Arrays.asList(
            "default_rotor_rand", "default_reflector_rand", "rotor_1",
            "rotor_2", "rotor_3", "rotor_4", "rotor_5", "reflector"));

    /**
     * This is the constructor for this class. It reads all the lines in the
     * config file, splits them on the whitespace, and then iterates through
     * each one, performing actions based on the value.
     *
     * @throws IOException
     *             Thrown if config.ini cannot be found by the program
     */
    public Enigma() throws IOException {

        // Used to prevent null pointer exceptions further in the constructor.
        for (int i = 0; i < rotorLength; i++) {
            rotors.add(null);
        }

        /*
         * A BiConsumer used to save space when creating rotors from the file.
         */
        // final BiConsumer<Integer, String> setRotorFromFile = (index, key) ->
        // {
        // rotors.set(index, new Rotor(key.toUpperCase().chars()
        // .mapToObj(c -> (char) c).collect(Collectors.toList())));
        // };

        /*
         * Read all lines of the config file, splits them on whitespace so that
         * index 0 is the name, and index 1 is the value
         */

        final Path configPath = FileSystems.getDefault().getPath(fileName);

        try {
            Files.lines(configPath).forEach(line -> {
                String[] keyValue = line.split("\\s+");
                if (keyValue[0].equals(optionKey.get(0))) {
                    if (Boolean.valueOf(keyValue[1])) {
                        for (int i = 0; i < rotorLength; i++) {
                            rotors.set(i, new Rotor());
                        }
                    }
                } else if (keyValue[0].equals(optionKey.get(1))) {
                    if (Boolean.valueOf(keyValue[1])) {
                        reflector = new Reflector();
                    }
                } else if (keyValue[0].equals(optionKey.get(2))
                        && rotors.get(0) == null) {
                    rotors.set(
                            Integer.parseInt(
                                    keyValue[0].replaceAll("[\\D]", "")) - 1,
                            new Rotor(
                                    keyValue[1].toUpperCase().chars()
                                            .mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    keyValue[2].charAt(0)));
                } else if (keyValue[0].equals(optionKey.get(3))
                        && rotors.get(1) == null) {
                    rotors.set(
                            Integer.parseInt(
                                    keyValue[0].replaceAll("[\\D]", "")) - 1,
                            new Rotor(
                                    keyValue[1].toUpperCase().chars()
                                            .mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    keyValue[2].charAt(0)));
                } else if (keyValue[0].equals(optionKey.get(4))
                        && rotors.get(2) == null) {
                    rotors.set(
                            Integer.parseInt(
                                    keyValue[0].replaceAll("[\\D]", "")) - 1,
                            new Rotor(
                                    keyValue[1].toUpperCase().chars()
                                            .mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    keyValue[2].charAt(0)));
                } else if (keyValue[0].equals(optionKey.get(5))
                        && rotors.get(3) == null) {
                    rotors.set(
                            Integer.parseInt(
                                    keyValue[0].replaceAll("[\\D]", "")) - 1,
                            new Rotor(
                                    keyValue[1].toUpperCase().chars()
                                            .mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    keyValue[2].charAt(0)));
                } else if (keyValue[0].equals(optionKey.get(6))
                        && rotors.get(4) == null) {
                    rotors.set(
                            Integer.parseInt(
                                    keyValue[0].replaceAll("[\\D]", "")) - 1,
                            new Rotor(
                                    keyValue[1].toUpperCase().chars()
                                            .mapToObj(c -> (char) c)
                                            .collect(Collectors.toList()),
                                    keyValue[2].charAt(0)));
                } else if (keyValue[0].equals(optionKey.get(7))
                        && reflector == null) {
                    reflector = new Reflector(keyValue[1].toUpperCase().chars()
                            .mapToObj(c -> (char) c)
                            .collect(Collectors.toList()));
                }
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "\" " + fileName
                            + "\" could not be located. No settings may be saved, and all "
                            + "encryptions keys will be randomly generated.");
            for (int i = 0; i < rotorLength; i++) {
                rotors.set(i, new Rotor());
            }
            reflector = new Reflector();
        }
    }

    public void saveSettings() {
        try {
            BufferedWriter fileWriter = new BufferedWriter(
                    new FileWriter(fileName));
            fileWriter.close();
        } catch (IOException e) {
            System.out.print("");
        }
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
        final int length = rotorsChosen.size() - 1;
        boolean isReverse = false;

        // PlugBoard swap
        output = plugBoardSwap(plugBoard, output);

        // Normal encryption
        for (int i = length; i > -1; i--) {
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
         * Gets the key to be sued based on rotorNumber, along with the alphabet
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
}
