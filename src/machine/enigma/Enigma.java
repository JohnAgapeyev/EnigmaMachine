package machine.enigma;

public class Enigma {

    public static void main(String[] args) {
        RotorGenerator gen = new RotorGenerator();
        System.out.println(gen.generate());
    }

}
