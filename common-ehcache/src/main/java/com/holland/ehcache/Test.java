package com.holland.ehcache;

public class Test {
    private Ehcache cache = new Ehcache();

    public int getSquareValueOfNumber(int input) {
        if (cache.getSquareNumberCache().containsKey(input)) {
            return cache.getSquareNumberCache().get(input);
        }

        System.out.println("Calculating square value of " + input +
                " and caching result.");

        int squaredValue = (int) Math.pow(input, 2);
        cache.getSquareNumberCache().put(input, squaredValue);

        return squaredValue;
    }

    public static void main(String[] args) {
        final Test test = new Test();
        for (int i = 10; i < 15; i++) {
            System.out.println("Square value of " + i + " is: "
                    + test.getSquareValueOfNumber(i) + "\n");
        }

        for (int i = 10; i < 15; i++) {
            System.out.println("Square value of " + i + " is: "
                    + test.getSquareValueOfNumber(i) + "\n");
        }
    }
}
