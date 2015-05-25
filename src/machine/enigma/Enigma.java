package machine.enigma;

import java.util.Random;

public class Enigma {

    private static Random rand = new Random();
    private static Rotor[] rotors = new Rotor[5];
    private static int rotorLength = rotors.length;

    public Enigma() {
        for (int i = 0; i < rotorLength; i++) {
            rotors[i] = new Rotor();
        }
    }

    public String encode(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[rand.nextInt(rotorLength)].getRotorKey();
        char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        String response = "";
        // response += "abcdefghijklmnopqrstuvwxyz\n";
        // for (char letter : rotorKey) {
        // response += letter;
        // }
        // response += "\n";
        // for (char letter : messageLetters) {
        // response += letter;
        // }
        // response += "\n";
        for (char letter : messageLetters) {
            if (letter == ' ') {
                response += ' ';
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
    
    public static Rotor[] getRotors() {
        return rotors;
    }
}
