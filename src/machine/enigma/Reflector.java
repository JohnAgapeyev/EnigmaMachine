package machine.enigma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The reflector for the program. The no argument constructor is really all it
 * does. Otherwise it's just a holder for a predetermined key.
 *
 * @author John Agapeyev
 *
 */
public class Reflector {

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final List<Character> ALPHABET = Enigma.ALPHABET;

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final byte ALPHABET_LENGTH = Enigma.ALPHABET_LENGTH;

    /**
     * The reflector's key.
     */
    private List<Character> key = new ArrayList<>(ALPHABET_LENGTH);

    /**
     * Default constructor for the reflector. It randomly generates a key by
     * iterating through each space in the key, and if its null, filling it with
     * an unused character. The index of that character is then filled with the
     * letter represented by the original index. For example: if i is equal to
     * 3, and a G was generated, then index 6 would contain a D, creating a pair
     * D <-> G. Finally those letters are added to a separate list that prevents
     * them being used elsewhere in the key.
     */
    public Reflector() {
        // Used to prevent null pointer exceptions.
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            key.add(null);
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

    /**
     * Secondary constructor that sets the key based on the parameter sent to
     * it.
     *
     * @param key
     *            The key to be used.
     */
    public Reflector(final List<Character> key) {
        this.key = key;
    }

    /**
     * Getter Method.
     *
     * @return The reflector's key.
     */
    public List<Character> getKey() {
        return key;
    }
}
