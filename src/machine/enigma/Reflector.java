package machine.enigma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Reflector {

    private static final List<Character> ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().chars().mapToObj(c -> (char) c)
            .collect(Collectors.toList());

    private static final byte ALPHABET_LENGTH = 26;

    private List<Character> key = new ArrayList<>(ALPHABET_LENGTH);

    public Reflector() {
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            key.add(i, null);
        }

        final Random rand = new Random();
        final ArrayList<Integer> alreadyUsed = new ArrayList<>();
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            if (key.get(i) == null) {
                int letterIndex = rand.nextInt(ALPHABET_LENGTH);
                while (alreadyUsed.contains(letterIndex) || letterIndex == i) {
                    letterIndex = rand.nextInt(ALPHABET_LENGTH);
                }
                key.set(i, ALPHABET.get(letterIndex));
                alreadyUsed.add(letterIndex);
                for (final Character letter : ALPHABET) {
                    if (letter == key.get(i)) {
                        key.set(ALPHABET.indexOf(letter), ALPHABET.get(i));
                        alreadyUsed.add(i);
                    }
                }
            }
        }
    }

    public Reflector(final List<Character> key) {
        this.key = key;
    }

    public List<Character> getKey() {
        return key;
    }
}
