package com.task.client.card.app.util;

public class OibValidator {

    public static boolean isValidOib(String oib) {
        if (oib == null || oib.length() != 11 || !oib.matches("\\d+")) {
            return false;
        }

        int a = 10;
        for (int i = 0; i < 10; i++) {
            a = a + Character.getNumericValue(oib.charAt(i));
            a = a % 10;
            if (a == 0) {
                a = 10;
            }
            a *= 2;
            a = a % 11;
        }
        int kontrolnaZnamenka = 11 - a;
        if (kontrolnaZnamenka == 10) {
            kontrolnaZnamenka = 0;
        }
        return kontrolnaZnamenka == Character.getNumericValue(oib.charAt(10));
    }
}

