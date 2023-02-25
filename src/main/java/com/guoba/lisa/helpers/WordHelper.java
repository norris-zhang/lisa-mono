package com.guoba.lisa.helpers;

import java.util.Random;

public class WordHelper {
    private static final String[] dict = {"ice", "water", "book", "smile", "cookie", "home", "love", "onion"};
    public static String randomWord() {
        Random r = new Random();
        return dict[r.nextInt(dict.length)] + r.nextInt(10);
    }
}
