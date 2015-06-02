package machine.enigma;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Enigma {

    private Rotor[] rotors = new Rotor[5];
    private Reflector reflector;
    private int rotorLength = rotors.length;
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().toCharArray();
    private final int REFLECTOR_CODE = 100;

    public Enigma() throws IOException {
        Path configPath = FileSystems.getDefault().getPath("config.ini");
        List<String[]> options = new ArrayList<String[]>();
        Files.readAllLines(configPath).forEach(
                line -> options.add(line.split("\\s+")));

        for (String[] line : options) {
            if (line[0].equals("default_rotor_rand")) {
                if (!Boolean.getBoolean(line[1])) {
                    for (String[] findValue : options) {
                        switch (findValue[0]) {
                            case "rotor_1":
                                rotors[0] = new Rotor(
                                        findValue[1].toCharArray());
                                break;
                            case "rotor_2":
                                rotors[1] = new Rotor(
                                        findValue[1].toCharArray());
                                break;
                            case "rotor_3":
                                rotors[2] = new Rotor(
                                        findValue[1].toCharArray());
                                break;
                            case "rotor_4":
                                rotors[3] = new Rotor(
                                        findValue[1].toCharArray());
                                break;
                            case "rotor_5":
                                rotors[4] = new Rotor(
                                        findValue[1].toCharArray());
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    for (int i = 0; i < rotorLength; i++) {
                        rotors[i] = new Rotor();
                    }
                }
            }
            if (line[0].equals("default_reflector_rand")) {
                if (!Boolean.getBoolean(line[1])) {
                    for (String[] findValue : options) {
                        if (findValue[0].equals("reflector")) {
                            reflector = new Reflector(
                                    findValue[1].toCharArray());
                            break;
                        }
                    }
                } else {
                    reflector = new Reflector();
                }
            }
        }
    }

    public char encode(char sentLetter, Integer[] rotorsChosen)
            throws Exception {
        char output = sentLetter;
        int length = rotorsChosen.length - 1;
        BiFunction<Character, Integer, Character> letterShift = (
                Character letter, Integer rotorNumber) -> {
            return rotorEncryption(letter, rotorNumber);
        };

        // Normal encryption
        for (int i = length; i > -1; i--) {
            output = letterShift.apply(output, rotorsChosen[i]);
        }

        output = letterShift.apply(output, REFLECTOR_CODE);

        // Return back through the rotors
        for (Integer rotor : rotorsChosen) {
            output = letterShift.apply(output, rotor);
        }

        System.out.println("\n\n\n\n\n");

        return output;
    }

    private char rotorEncryption(char letter, int rotorNumber) {
        char[] rotorKey;
        if (rotorNumber == REFLECTOR_CODE) {
            rotorKey = reflector.getKey();
        } else {
            rotorKey = rotors[rotorNumber].getKey();
        }

        char response = letter;
        for (int i = 0; i < ALPHABET.length; i++) {
            if (response == ALPHABET[i]) {
                response = rotorKey[i];
                break;
            }
        }

        System.out.println(rotorNumber);

        System.out.println(letter + "\t" + response);
        String one = "";
        String two = "";
        for (int i = 0; i < 26; i++) {
            one += rotorKey[i];
            two += ALPHABET[i];
        }
        System.out.println(two + "\n" + one);

        return response;
    }

    public Rotor[] getRotors() {
        return rotors;
    }
}
