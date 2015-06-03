package machine.enigma;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Reflector {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();

    private static List<Character> letterList;

    private Character[] key = new Character[ALPHABET.length];;

    public Reflector() {
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
            if (key[i] == null) {
                int letterIndex = rand.nextInt(alphabetLength);
                while (alreadyUsed.contains(letterIndex) || letterIndex == i) {
                    letterIndex = rand.nextInt(alphabetLength);
                }
                key[i] = letterList.get(letterIndex);
                alreadyUsed.add(letterIndex);
                for (Character letter : letterList) {
                    if (letter == key[i]) {
                        key[letterList.indexOf(letter)] = letterList.get(i);
                        alreadyUsed.add(i);
                    }
                }
            }
        }
    }

    public Reflector(char[] key) {
        for (int i = 0; i < this.key.length; i++) {
            this.key[i] = key[i];
        }
    }

    public char[] getKey() {
        char[] returnKey = new char[key.length];
        for (int i = 0; i < key.length; i++) {
            returnKey[i] = key[i];
        }
        return returnKey;
    }

    public void showKey() {
        for (Character letter : key) {
            System.out.print(letter);
        }
        System.out.print("\n");
    }
}