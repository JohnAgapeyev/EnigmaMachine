package machine.enigma;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Enigma {

    /*
     * FIXED MY MASSIVE LOGIC ERROR FEEL SO HAPPY HAPPY Still need to comment
     * stuff
     */

    private final List<Rotor> rotors = new ArrayList<>(5);
    private Reflector reflector;
    private final int rotorLength = 5;

    private static final List<Character> ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().chars().mapToObj(c -> (char) c)
            .collect(Collectors.toList());

    private final byte REFLECTOR_CODE = 100;

    public Enigma() throws IOException {

        for (int i = 0; i < 5; i++) {
            rotors.add(null);
        }

        final BiConsumer<Integer, String> setRotorFromFile = (index, key) -> {
            rotors.set(index,
                    new Rotor(key.toUpperCase().chars().mapToObj(c -> (char) c)
                            .collect(Collectors.toList())));
        };

        final Path configPath = FileSystems.getDefault().getPath("config.ini");
        final List<String[]> options = new ArrayList<>();
        Files.readAllLines(configPath).forEach(
                line -> options.add(line.split("\\s+")));

        for (final String[] line : options) {
            if (line[0].equals("default_rotor_rand")) {
                if (!Boolean.valueOf(line[1])) {
                    for (final String[] findValue : options) {
                        if (findValue[0].startsWith("rotor_")) {
                            setRotorFromFile.accept(Integer
                                    .valueOf(findValue[0].replaceAll("[\\D]",
                                            "")) - 1, findValue[1]);
                        }
                    }
                } else {
                    for (int i = 0; i < rotorLength; i++) {
                        rotors.set(i, new Rotor());
                    }
                }
            } else if (line[0].equals("default_reflector_rand")) {
                if (!Boolean.valueOf(line[1])) {
                    for (final String[] findValue : options) {
                        if (findValue[0].equals("reflector")) {
                            reflector = new Reflector(findValue[1]
                                    .toUpperCase().chars()
                                    .mapToObj(c -> (char) c)
                                    .collect(Collectors.toList()));
                            break;
                        }
                    }
                } else {
                    reflector = new Reflector();
                }
            }
        }
    }

    public char encode(final char sentLetter, final List<Byte> rotorsChosen,
            final List<List<Character>> plugBoard) {
        char output = sentLetter;
        final int length = rotorsChosen.size() - 1;
        boolean isReverse = false;

        output = plugBoardSwap(plugBoard, output);

        // Normal encryption
        for (int i = length; i > -1; i--) {
            output = rotorEncryption(output, rotorsChosen.get(i), isReverse);
        }

        output = rotorEncryption(output, REFLECTOR_CODE, isReverse);

        isReverse = true;

        // Return back through the rotors
        for (final Byte rotor : rotorsChosen) {
            output = rotorEncryption(output, rotor, isReverse);
        }

        output = plugBoardSwap(plugBoard, output);

        System.out.println("\n\n\n\n\n");

        return output;
    }

    private char rotorEncryption(final char letter, final byte rotorNumber,
            final boolean isReverse) {
        List<Character> rotorKey = new ArrayList<>(ALPHABET.size());
        List<Character> alphabetKey = new ArrayList<>(ALPHABET.size());
        char response = letter;

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
            for (int i = 0; i < alphabetKey.size(); i++) {
                if (response == alphabetKey.get(i)) {
                    response = ALPHABET.get(i);
                    break;
                }
            }

            // Internal alphabet gets turned into rotor wiring
            for (int i = 0; i < alphabetKey.size(); i++) {
                if (response == alphabetKey.get(i)) {
                    response = rotorKey.get(i);
                    break;
                }
            }

            // Rotor output is returned through the shifted alphabet key once
            // again on its way out
            for (int i = 0; i < ALPHABET.size(); i++) {
                if (response == ALPHABET.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }

        } else {
            for (int i = 0; i < alphabetKey.size(); i++) {
                if (response == alphabetKey.get(i)) {
                    response = ALPHABET.get(i);
                    break;
                }
            }

            for (int i = 0; i < rotorKey.size(); i++) {
                if (response == rotorKey.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }
            for (int i = 0; i < ALPHABET.size(); i++) {
                if (response == ALPHABET.get(i)) {
                    response = alphabetKey.get(i);
                    break;
                }
            }
        }
        return response;
    }

    private char plugBoardSwap(final List<List<Character>> plugBoard,
            final char letter) {

        char output = letter;

        for (final List<Character> pair : plugBoard) {
            if (pair.contains(letter)) {
                output = (output == pair.get(0)) ? pair.get(1) : pair.get(0);
            }
        }

        return output;
    }

    public List<Rotor> getRotors() {
        return rotors;
    }
}
