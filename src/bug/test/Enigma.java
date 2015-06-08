package bug.test;

import java.util.Scanner;
import java.util.function.BiFunction;

public class Enigma {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().toCharArray();

    char[][] rotorKey = new char[3][26];
    char[][] rotorAlphabet = new char[3][26];
    char[][] originalKey;
    char[] reflectorKey;
    static int[] rotorOffset = new int[3];

    public static Enigma machine;
    static Scanner scan = new Scanner(System.in);

    public Enigma() {
        rotorKey[0] = "EKMFLGDQVZNTOWYHXUSPAIBRCJ".toCharArray();
        rotorKey[1] = "AJDKSIRUXBLHWTMCQGZNPYFVOE".toCharArray();
        rotorKey[2] = "BDFHJLCPRTXVZNYEIWGAKMUSQO".toCharArray();
        reflectorKey = "YRUHQSLDPXNGOKMIEBFZCWVJAT".toCharArray();
        originalKey = rotorKey.clone();

        rotorAlphabet[0] = ALPHABET.clone();
        rotorAlphabet[1] = ALPHABET.clone();
        rotorAlphabet[2] = ALPHABET.clone();

        for (int i = 0; i < rotorKey.length; i++) {
            rotorOffset[i] = 0;
        }
    }

    /*
     * This is the main method where I'm getting my logic error from.
     * It runs the rotors through an encryption method below it for each rotor
     * Then the reflector, followed by the rotors in reverse order
     */
    public char encode(final char sentLetter) throws Exception {
        final Integer[] rotorsChosen = { 0, 1, 2 };
        char output = sentLetter;
        final int length = rotorsChosen.length - 1;
        final BiFunction<Character, Integer, Character> letterShift = (
                final Character letter, final Integer rotorNumber) -> {
            return rotorEncryption(letter, rotorNumber);
        };

        // Normal encryption
        for (int i = length; i > -1; i--) {
            output = letterShift.apply(output, rotorsChosen[i]);
        }

        // Reflector
        output = letterShift.apply(output, 100);

        // Return back through the rotors
        for (final Integer rotor : rotorsChosen) {
            output = letterShift.apply(output, rotor);
        }

        /*
         * I use this to separate the debugging information between letters
         * Uncomment it if you are using the printout I have set up lower down
         */
        // System.out.println("\n\n\n\n\n");

        return output;
    }

    /*
     * This is the encryption method that is called to encipher the text
     * It uses a substitution cipher on the alphabet based on the rotor's offset
     * Then using that offset, it uses another substitution cipher to encrypt it using the rotor's key
     */
    private char rotorEncryption(final char letter, final int rotorNumber) {
        char[] rotKey;
        char[] alphabetKey;

        if (rotorNumber == 100) {
            rotKey = reflectorKey;
            alphabetKey = ALPHABET.clone();
        } else {
            rotKey = rotorKey[rotorNumber];
            alphabetKey = rotorAlphabet[rotorNumber];
        }

        char response = letter;

        for (int i = 0; i < alphabetKey.length; i++) {
            if (response == alphabetKey[i]) {
                response = ALPHABET[i];
                break;
            }
        }

        for (int i = 0; i < alphabetKey.length; i++) {
            if (response == alphabetKey[i]) {
                response = rotKey[i];
                break;
            }
        }

        /*
         * This is a massive print out I use for debugging It prints the rotor
         * number input output alphabet rotor offset rotor key and does so for
         * each step of the encoding process
         */
        // System.out.println(rotorNumber);
        //
        // System.out.println(letter + "\t" + response);
        // String one = "";
        // String two = "";
        // String three = "";
        // for (int i = 0; i < 26; i++) {
        // one += rotKey[i];
        // two += ALPHABET[i];
        // three += alphabetKey[i];
        // }
        // System.out.println(two + "\n" + three + "\n" + one);

        return response;
    }

    public void rotate(final int rotor) {
        // Shifting the key
        char temp = rotorKey[rotor][0];
        for (int i = 0; i < 25; i++) {
            rotorKey[rotor][i] = rotorKey[rotor][i + 1];
        }
        rotorKey[rotor][25] = temp;

        // Shifting the alphabet the key is compared to
        temp = rotorAlphabet[rotor][0];
        for (int i = 0; i < 25; i++) {
            rotorAlphabet[rotor][i] = rotorAlphabet[rotor][i + 1];
        }
        rotorAlphabet[rotor][25] = temp;
    }

    public void setRotation(final int rotateSteps, final int rotor) {
        rotorKey[rotor] = originalKey[rotor].clone();
        rotorAlphabet[rotor] = ALPHABET.clone();
        if (rotateSteps == 0) {
            return;
        } else if (rotateSteps > 0) {
            for (int i = 0; i < rotateSteps; i++) {
                rotate(rotor);
            }
        }
    }

    /*
     * This and everything below it are what I use to get the terminal/program running
     * The only line that communicated with the encryption is machine.encode(letter),
     * and that just calls the method and sends it the user's input.
     */
    public static void main(final String[] args) throws Exception {
        boolean isRunning = true;
        machine = new Enigma();
        String input = "";
        do {
            System.out
                    .println("Enter a command: \nType Help for a list of commands");
            input = scan.nextLine().toLowerCase();
            switch (input) {
                case "help":
                    System.out.println("Set rotors");
                    System.out.println("Start");
                    System.out.println("Quit");
                    break;
                case "set rotors":
                    System.out.println("Current rotors are at positions: "
                            + rotorOffset[0] + " " + rotorOffset[1] + " "
                            + rotorOffset[2]);
                    System.out
                            .println("Please enter the desired rotor postions separated by a space: ");
                    try {
                        rotorOffset[0] = Integer.parseInt(scan.next()) % 26;
                        machine.setRotation(rotorOffset[0], 0);
                        rotorOffset[1] = Integer.parseInt(scan.next()) % 26;
                        machine.setRotation(rotorOffset[1], 1);
                        rotorOffset[2] = Integer.parseInt(scan.next()) % 26;
                        machine.setRotation(rotorOffset[2], 2);
                    } catch (final NumberFormatException e) {
                        System.out.println("Those aren't all whole numbers");
                    }
                    break;
                case "quit":
                    isRunning = false;
                    break;
                case "start":
                    machine.run();
                    break;
                default:
                    System.out.println(input + " is not a valid input");
                    break;
            }
            scan.nextLine();
        } while (isRunning);
        scan.close();
    }

    public void run() throws Exception {
        String input = "";
        char letter = ' ';
        String output = "";
        do {
            System.out.println("Enter a letter to encode: \n Type '?' to exit");
            input = scan.next().toUpperCase().replaceAll("\\W", "");
            try {
                letter = input.charAt(0);
                output += machine.encode(letter);
                rotorOffset[2]++;
                formatRotorOffset();
            } catch (final StringIndexOutOfBoundsException e) {
                break;
            }
            System.out.println("Your current message: ");
            System.out.println(output);
            System.out.println("Current rotors are at positions: "
                    + rotorOffset[0] + " " + rotorOffset[1] + " "
                    + rotorOffset[2]);
        } while (letter != '?');
    }

    public void formatRotorOffset() {
        final int length = rotorOffset.length - 1;
        for (int i = length; i > -1; i--) {
            if (rotorOffset[i] < 0) {
                rotorOffset[i] = 25;
            } else if (rotorOffset[i] > 25) {
                if (i > 0) {
                    rotorOffset[i - 1]++;
                }
                rotorOffset[i] = 0;
            }
            machine.setRotation(rotorOffset[i], i);
        }
    }
}
