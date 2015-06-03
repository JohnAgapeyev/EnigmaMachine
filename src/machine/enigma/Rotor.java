package machine.enigma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rotor {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().toCharArray();

    private static List<Character> letterList;

    private char[] originalKey;

    private char[] rotatedKey;

    private char[] rotatedAlphabet;

    public Rotor() {
        originalKey = new char[ALPHABET.length];
        rotatedKey = new char[ALPHABET.length];
        rotatedAlphabet = ALPHABET.clone();

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
            originalKey[i] = letterList.get(letterIndex);
            alreadyUsed.add(letterIndex);
        }
        rotatedKey = originalKey.clone();
    }

    public Rotor(char[] key) {
        originalKey = key;
        rotatedKey = key;
        rotatedAlphabet = ALPHABET.clone();
    }

    public char[] getKey() {
        return rotatedKey;
    }

    public char[] getAlphabet() {
        return rotatedAlphabet;
    }

    public void rotate() {
        // Shifting the key
        char temp = rotatedKey[0];
        final int keyLength = rotatedKey.length - 1;
        for (int i = 0; i < keyLength; i++) {
            rotatedKey[i] = rotatedKey[i + 1];
        }
        rotatedKey[keyLength] = temp;

        // Shifting the alphabet the key is compared to
        temp = rotatedAlphabet[0];
        for (int i = 0; i < keyLength; i++) {
            rotatedAlphabet[i] = rotatedAlphabet[i + 1];
        }
        rotatedAlphabet[keyLength] = temp;
    }

    private void rotateBackwards() {
        // Shifting the key
        final int keyLength = rotatedKey.length - 1;
        char temp = rotatedKey[keyLength];
        for (int i = keyLength; i >= 0; i--) {
            rotatedKey[i] = rotatedKey[i - 1];
        }
        rotatedKey[0] = temp;

        // Shifting the alphabet the key is compared to
        temp = rotatedAlphabet[keyLength];
        for (int i = keyLength; i >= 0; i--) {
            rotatedAlphabet[i] = rotatedAlphabet[i - 1];
        }
        rotatedAlphabet[0] = temp;
    }

    public void setRotation(int rotateSteps) {
        rotatedKey = originalKey.clone();
        rotatedAlphabet = ALPHABET.clone();
        if (rotateSteps == 0) {
            return;
        } else if (rotateSteps > 0) {
            for (int i = 0; i < rotateSteps; i++) {
                rotate();
            }
        } else {
            for (int i = 0; i < Math.abs(rotateSteps); i++) {
                rotateBackwards();
            }
        }
    }
}
