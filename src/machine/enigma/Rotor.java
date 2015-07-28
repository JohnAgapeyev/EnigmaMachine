package machine.enigma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This is the rotor class for the Enigma Machine. It has two constructors, one
 * which randomly generates a key, and the other which takes a key as a
 * parameter and uses that.
 *
 * @author John Agapeyev
 *
 */
public class Rotor {

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final List<Character> ALPHABET = Enigma.ALPHABET;

    /**
     * All documentation for this constant is provided in the Enigma Class.
     */
    private static final byte ALPHABET_LENGTH = Enigma.ALPHABET_LENGTH;

    /**
     * The original key that the rotor has.
     */
    private List<Character> originalKey = new ArrayList<>(ALPHABET_LENGTH);

    /**
     * The current version of the key that is rotated by the program.
     */
    private List<Character> rotatedKey = new ArrayList<>(ALPHABET_LENGTH);

    /**
     * The current offset of the rotor, that is rotated by the program.
     */
    private List<Character> rotatedAlphabet;

    /**
     * The turnover point for the rotor. When the rotor moves past this
     * position, it kicks the next rotor.
     */
    private final char turnoverPoint;

    /**
     * Main constructor that randomly generates a key. It does this by iterating
     * through the length of the key, randomly generating a letter that is then
     * used and added to a separate list that keeps track of all the letters
     * that have already been used.
     */
    public Rotor() {
        rotatedAlphabet = new ArrayList<>(ALPHABET);
        // Used to prevent null pointer exceptions.
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
        rotatedKey = new ArrayList<>(originalKey);
        turnoverPoint = ALPHABET.get(rand.nextInt(ALPHABET_LENGTH));
    }

    /**
     * This is the other constructor for this class that uses whatever key is
     * given to it.
     *
     * @param key
     *            The key to be used.
     */
    public Rotor(final List<Character> key, final char turnoverPoint) {
        rotatedAlphabet = ALPHABET;
        originalKey = key;
        rotatedKey = key;
        this.turnoverPoint = turnoverPoint;
    }

    /**
     * Getter method.
     *
     * @return The current Key.
     */
    public List<Character> getKey() {
        return rotatedKey;
    }

    /**
     * Getter method.
     *
     * @return The un-rotated key.
     */
    public List<Character> getOriginalKey() {
        return originalKey;
    }

    /**
     * Getter method.
     *
     * @return The current alphabet offset.
     */
    public List<Character> getAlphabet() {
        return rotatedAlphabet;
    }

    /**
     * Sets the rotation of the rotor based on rotateSteps. Resets the alphabet
     * and key to their original values, then rotates them however many steps
     * they need to be rotated. Because of how Collections.rotate works, the
     * value is made negative to allow it to work correctly with the rest of the
     * program.
     *
     * @param rotateSteps
     *            Number of steps to rotate
     */
    public void setRotation(final byte rotateSteps) {
        rotatedKey = originalKey;
        rotatedAlphabet = ALPHABET;
        if (rotateSteps == 0) {
            return;
        } else {
            Collections.rotate(rotatedKey, -rotateSteps);
            Collections.rotate(rotatedAlphabet, -rotateSteps);
        }
    }

    /**
     * Getter Method
     *
     * @return The rotor's turnover point.
     */
    public char getTurnover() {
        return turnoverPoint;
    }
}
