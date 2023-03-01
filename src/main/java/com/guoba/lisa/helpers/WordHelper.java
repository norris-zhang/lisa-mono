package com.guoba.lisa.helpers;

import java.util.Random;

public class WordHelper {
    private static final String[] dict = {"ice", "water", "book", "smile", "cookie", "home", "love", "onion", "angel", "apple", "arrow", "beach", "begin", "bella", "birth", "black", "blink", "brave", "brush", "camel", "candy", "chair", "child", "clear", "clock", "craft", "creek", "cross", "circle", "dance"};

    public static String randomWord() {
        Random r = new Random();
        return dict[r.nextInt(dict.length)] + r.nextInt(10);
    }
}
