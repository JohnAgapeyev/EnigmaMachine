package machine.enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Reflector {

    private static final List<Character> ALPHABET = Arrays
            .asList("abcdefghijklmnopqrstuvwxyz".toUpperCase().chars()
                    .mapToObj(c -> (char) c).toArray(Character[]::new));

    private List<Character> key = new ArrayList<>(26);

    public Reflector() {
        for (int i = 0; i < 26; i++) {
            key.add(i, null);
        }

        final Random rand = new Random();
        final int alphabetLength = ALPHABET.size();
        final ArrayList<Integer> alreadyUsed = new ArrayList<>();
        for (int i = 0; i < alphabetLength; i++) {
            if (key.get(i) == null) {
                int letterIndex = rand.nextInt(alphabetLength);
                while (alreadyUsed.contains(letterIndex) || letterIndex == i) {
                    letterIndex = rand.nextInt(alphabetLength);
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

    public void showKey() {
        for (final Character letter : key) {
            System.out.print(letter);
        }
        System.out.print("\n");
    }
}
