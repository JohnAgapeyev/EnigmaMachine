package machine.enigma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Rotor {

    private static final List<Character> ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().chars().mapToObj(c -> (char) c)
            .collect(Collectors.toList());

    private static final byte ALPHABET_LENGTH = 26;

    private final List<Character> originalKey = new ArrayList<>(ALPHABET_LENGTH);

    private List<Character> rotatedKey = new ArrayList<>(ALPHABET_LENGTH);

    private final List<Character> rotatedAlphabet = new ArrayList<>(
            ALPHABET_LENGTH);

    private final BinaryOperator<List<Character>> clone = (parent, child) -> {
        child.clear();
        parent.forEach(child::add);
        return child;
    };

    public Rotor() {
        clone.apply(ALPHABET, rotatedAlphabet);
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            originalKey.add(null);
            rotatedKey.add(null);
        }

        final Random rand = new Random();
        final ArrayList<Integer> alreadyUsed = new ArrayList<>();
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            int letterIndex = rand.nextInt(ALPHABET_LENGTH);
            while (alreadyUsed.contains(letterIndex)) {
                letterIndex = rand.nextInt(ALPHABET_LENGTH);
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

    public void setRotation(final byte rotateSteps) {
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
