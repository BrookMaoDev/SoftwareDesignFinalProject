package com.example.b07demosummer2024;

public final class StringUtil {
    private StringUtil() {}

    public static boolean containsIgnoreCase(String a, String b) {
        return a.toUpperCase().contains(b.toUpperCase());
    }
}
