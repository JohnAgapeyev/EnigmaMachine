package machine.enigma;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Enigma {

    /*
     * FIXED MY MASSIVE LOGIC ERROR FEEL SO HAPPY HAPPY Still need to comment
     * stuff
     */

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
                if (!Boolean.valueOf(line[1])) {
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
            } else if (line[0].equals("default_reflector_rand")) {
                if (!Boolean.valueOf(line[1])) {
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
        boolean isReverse = false;

        // Normal encryption
        for (int i = length; i > -1; i--) {
            output = rotorEncryption(output, rotorsChosen[i], isReverse);
        }

        output = rotorEncryption(output, REFLECTOR_CODE, isReverse);

        isReverse = true;
        // Return back through the rotors
        for (Integer rotor : rotorsChosen) {
            output = rotorEncryption(output, rotor, isReverse);
        }

        System.out.println("\n\n\n\n\n");

        return output;
    }

    private char rotorEncryption(char letter, int rotorNumber, boolean isReverse) {
        char[] rotorKey;
        char[] alphabetKey;
        char response = letter;

        if (rotorNumber == REFLECTOR_CODE) {
            rotorKey = reflector.getKey();
            alphabetKey = ALPHABET.clone();
        } else {
            rotorKey = rotors[Math.abs(rotorNumber)].getKey();
            alphabetKey = rotors[Math.abs(rotorNumber)].getAlphabet();
        }

        if (!isReverse) {

            System.out.println(response);

            // External alphabet gets turned into internal alphabet
            for (int i = 0; i < alphabetKey.length; i++) {
                if (response == alphabetKey[i]) {
                    response = ALPHABET[i];
                    break;
                }
            }

            System.out.println(response);

            // Internal alphabet gets turned into rotor wiring
            for (int i = 0; i < alphabetKey.length; i++) {
                if (response == alphabetKey[i]) {
                    response = rotorKey[i];
                    break;
                }
            }

            System.out.println(response);

            // Rotor output is returned through the shifted alphabet key once
            // again on its way out
            for (int i = 0; i < ALPHABET.length; i++) {
                if (response == ALPHABET[i]) {
                    response = alphabetKey[i];
                    break;
                }
            }

            System.out.println(response);

        } else {
            System.out.println(response);

            for (int i = 0; i < alphabetKey.length; i++) {
                if (response == alphabetKey[i]) {
                    response = ALPHABET[i];
                    break;
                }
            }

            System.out.println(response);

            for (int i = 0; i < rotorKey.length; i++) {
                if (response == rotorKey[i]) {
                    response = alphabetKey[i];
                    break;
                }
            }
            System.out.println(response);

            for (int i = 0; i < ALPHABET.length; i++) {
                if (response == ALPHABET[i]) {
                    response = alphabetKey[i];
                    break;
                }
            }
            System.out.println(response);

        }

        System.out.println(rotorNumber);

        System.out.println(letter + "\t" + response);
        String one = "";
        String two = "";
        String three = "";
        for (int i = 0; i < 26; i++) {
            one += rotorKey[i];
            two += ALPHABET[i];
            three += alphabetKey[i];
        }

        System.out.println(two + "\n" + three + "\n" + one + "\n" + three
                + "\n" + two + "\n\n");

        return response;
    }

    public Rotor[] getRotors() {
        return rotors;
    }
}
