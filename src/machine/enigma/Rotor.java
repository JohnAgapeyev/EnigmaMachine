package machine.enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class Rotor {

    private static final List<Character> ALPHABET = Arrays
            .asList("abcdefghijklmnopqrstuvwxyz".toUpperCase().chars()
                    .mapToObj(c -> (char) c).toArray(Character[]::new));

    private final List<Character> originalKey;

    private List<Character> rotatedKey;

    private List<Character> rotatedAlphabet = new ArrayList<>(26);

    private final BiFunction<List<Character>, List<Character>, List<Character>> clone = (
            parent, child) -> {
        child.clear();
        parent.forEach(child::add);
        return child;
    };

    public Rotor() {
        originalKey = new ArrayList<>(26);
        rotatedKey = new ArrayList<>(26);
        clone.apply(ALPHABET, rotatedAlphabet);
        // ALPHABET.forEach(rotatedAlphabet::add);
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
        rotatedKey = clone.apply(originalKey, rotatedAlphabet);
        ;
    }

    public Rotor(final List<Character> key) {
        originalKey = key;
        rotatedKey = key;
        rotatedAlphabet = clone.apply(ALPHABET, rotatedAlphabet);
    }

    public List<Character> getKey() {
        return rotatedKey;
    }

    public List<Character> getAlphabet() {
        return rotatedAlphabet;
    }

    public void setRotation(final int rotateSteps) {
        if (rotateSteps == 0) {
            return;
        } else {
            Collections.rotate(rotatedKey, -rotateSteps);
            Collections.rotate(rotatedAlphabet, -rotateSteps);
        }
    }
}
