package machine.enigma;

import java.util.Random;
import java.util.Scanner;

public class Enigma {

    private static Random rand = new Random();
    private static Rotor[] rotors = new Rotor[5];
    private static int rotorLength = rotors.length;
    private static final Scanner scan = new Scanner(System.in);

    public Enigma() {
        for (int i = 0; i < rotorLength; i++) {
            rotors[i] = new Rotor();
        }
        System.out.println("Please enter your message: ");
        System.out.println(encode(scan.nextLine()));
    }

    private static String encode(String message) {
        char[] messageLetters = message.toLowerCase().toCharArray();
        char[] rotorKey = rotors[rand.nextInt(rotorLength)].generate()
                .toCharArray();
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
}
