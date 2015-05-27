package machine.enigma;

import java.util.ArrayList;
import java.util.Random;

public class Rotor {
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    private static ArrayList<Character> letterList;

    private char[] originalKey;

    private char[] rotatedKey;

    public Rotor() {
        originalKey = new char[ALPHABET.length];
        rotatedKey = new char[ALPHABET.length];
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

    public char[] getKey() {
        return rotatedKey;
    }

    public void rotate() {
        char temp = rotatedKey[0];
        final int keyLength = rotatedKey.length - 1;
        for (int i = 0; i < keyLength; i++) {
            rotatedKey[i] = rotatedKey[i + 1];
        }
        rotatedKey[keyLength] = temp;
    }

    private void rotateBackwards() {
        char temp = rotatedKey[rotatedKey.length - 1];
        final int keyLength = rotatedKey.length - 1;
        for (int i = keyLength; i >= 0; i--) {
            rotatedKey[i] = rotatedKey[i - 1];
        }
        rotatedKey[0] = temp;
    }

    public void setRotation(int rotateSteps) {
        if (rotateSteps == 0) {
            return;
        } else if (rotateSteps > 0) {
            rotatedKey = originalKey.clone();
            for (int i = 0; i < rotateSteps; i++) {
                rotate();
            }
        } else {
            rotatedKey = originalKey.clone();
            for (int i = 0; i < Math.abs(rotateSteps); i++) {
                rotateBackwards();
            }
        }
    }
}
