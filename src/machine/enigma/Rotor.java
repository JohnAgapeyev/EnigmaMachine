package machine.enigma;

import java.util.ArrayList;
import java.util.Random;

public class Rotor {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    private static ArrayList<Character> letterList;

    private char[] rotorKey;

    public Rotor() {
        rotorKey = new char[ALPHABET.length];
        if (letterList == null) {
            letterList = new ArrayList<Character>();
            for (int i = 0; i < ALPHABET.length; i++) {
                letterList.add(ALPHABET[i]);
            }
        }
        final Random rand = new Random();
        int alphabetLength = letterList.size();
        ArrayList<Integer> alreadyUsed = new ArrayList<Integer>();
        for (int i = 0; i < alphabetLength; i++) {
            int letterIndex = rand.nextInt(alphabetLength);
            while (alreadyUsed.contains(letterIndex)) {
                letterIndex = rand.nextInt(alphabetLength);
            }
            rotorKey[i] = letterList.get(letterIndex);
            alreadyUsed.add(letterIndex);
        }
    }

    public char[] getRotorKey() {
        return rotorKey;
    }

    public void rotate() {
        char temp = rotorKey[0];
        final int keyLength = rotorKey.length - 1;
        for (int i = 0; i < keyLength; i++) {
            rotorKey[i] = rotorKey[i + 1];
        }
        rotorKey[keyLength] = temp;
    }
}
