package util;

import java.util.List;

public class Statistics {

    public static void printNumbers(List<? extends Number> numbers) {
        for (Number number : numbers) {
            System.out.println(number);
        }
    }
}