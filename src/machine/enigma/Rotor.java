package machine.enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;

public class Rotor {

    private static final List<Character> ALPHABET = Arrays
            .asList("abcdefghijklmnopqrstuvwxyz".toUpperCase().chars()
                    .mapToObj(c -> (char) c).toArray(Character[]::new));

    private final List<Character> originalKey = new ArrayList<>(26);;

    private List<Character> rotatedKey = new ArrayList<>(26);;

    private final List<Character> rotatedAlphabet = new ArrayList<>(26);

    private final BinaryOperator<List<Character>> clone = (parent, child) -> {
        child.clear();
        parent.forEach(child::add);
        return child;
    };

    public Rotor() {
        clone.apply(ALPHABET, rotatedAlphabet);
        for (int i = 0; i < 26; i++) {
            originalKey.add(null);
            rotatedKey.add(null);
        }

        final Random rand = new Random();
        final int alphabetLength = ALPHABET.size();
        final ArrayList<Integer> alreadyUsed = new ArrayList<>();
        for (int i = 0; i < alphabetLength; i++) {
            int letterIndex = rand.nextInt(alphabetLength);
            while (alreadyUsed.contains(letterIndex)) {
                letterIndex = rand.nextInt(alphabetLength);
            }
            originalKey.set(i, ALPHABET.get(letterIndex));
            alreadyUsed.add(letterIndex);
        }
        rotatedKey = clone.apply(originalKey, rotatedKey);
        ;
    }

    public Rotor(final List<Character> key) {
        clone.apply(ALPHABET, rotatedAlphabet);
        clone.apply(key, originalKey);
        clone.apply(key, rotatedKey);
    }

    public List<Character> getKey() {
        return rotatedKey;
    }

    public List<Character> getAlphabet() {
        return rotatedAlphabet;
    }

    public void setRotation(final int rotateSteps) {
        clone.apply(originalKey, rotatedKey);
        clone.apply(ALPHABET, rotatedAlphabet);
        if (rotateSteps == 0) {
            return;
        } else {
            Collections.rotate(rotatedKey, -rotateSteps);
            Collections.rotate(rotatedAlphabet, -rotateSteps);
        }
    }
}
