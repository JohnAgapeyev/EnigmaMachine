package machine.enigma;

import java.util.Random;
import java.util.function.BiFunction;

public class Enigma {

    private static Random rand = new Random();
    private Rotor[] rotors = new Rotor[5];
    private int rotorLength = rotors.length;
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    public Enigma() {
        for (int i = 0; i < rotorLength; i++) {
            rotors[i] = new Rotor();
        }

    }

    public String encode(String userMessage, Integer[] rotorsChosen)
            throws Exception {
        String output = userMessage;
        int length = rotorsChosen.length - 1;
        BiFunction<String, Integer, String> letterShift = (String message,
                Integer rotorNumber) -> {
            return rotorEncryption(message, rotorNumber);
        };
        // Normal encryption
        for (Integer rotor : rotorsChosen) {
            output = letterShift.apply(output, rotor);
        }
        // Reflector
        for (int i = length; i > -1; i--) {
            output = letterShift.apply(output, rotorsChosen[i]);
        }
        return output;
    }

    private String rotorEncryption(String message, int rotorNumber) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[rotorNumber].getKey();
        String response = "";
        for (char letter : messageLetters) {
            if (letter == ' ') {
                response += letter;
            } else {
                for (int i = 0; i < ALPHABET.length; i++) {
                    if (letter == ALPHABET[i]) {
                        response += rotorKey[i];
                    }
                }
            }
        }
        return response;
    }

    public Rotor[] getRotors() {
        return rotors;
    }
}
