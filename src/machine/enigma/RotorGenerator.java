package machine.enigma;

import java.util.ArrayList;
import java.util.Random;

public class RotorGenerator {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    private static ArrayList<Character> letterList = new ArrayList<Character>();

    public RotorGenerator() {
        for (int i = 0; i < ALPHABET.length; i++) {
            letterList.add(ALPHABET[i]);
        }
    }

    public String generate() {
        String rotor = "";
        final Random rand = new Random();
        int alphabetLength = letterList.size();
        ArrayList<Integer> alreadyUsed = new ArrayList<Integer>();
        for (int i = 0; i < alphabetLength; i++) {
            int letterIndex = rand.nextInt(alphabetLength);
            while (alreadyUsed.contains(letterIndex)) {
                letterIndex = rand.nextInt(alphabetLength);
            }
            rotor += letterList.get(letterIndex);
            alreadyUsed.add(letterIndex);
        }

        return rotor;
    }
}
