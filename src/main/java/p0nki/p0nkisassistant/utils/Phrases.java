package p0nki.p0nkisassistant.utils;

public class Phrases {

    public static final String[] YOU_ARE_AN_IDIOT = new String[]{"ur idot", " hi dumdum", "ok poopoo head"};

    private static String choice(String[] arr) {
        return arr[(int) (Math.random() * arr.length)];
    }

    public static String youAreAnIdiot() {
        return choice(YOU_ARE_AN_IDIOT);
    }

}
