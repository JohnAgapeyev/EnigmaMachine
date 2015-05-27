package machine.enigma;

import java.util.Random;

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

    public String encode(String message) {
        return thirdRotor(secondRotor(firstRotor(message)));
    }

    private String firstRotor(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[0].getKey();
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

    private String secondRotor(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[1].getKey();
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

    private String thirdRotor(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[2].getKey();
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

    private String fourthRotor(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[3].getKey();
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

    private String fifthRotor(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[4].getKey();
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
