package com.exercise4.utils;

import org.apache.commons.lang3.RandomStringUtils;


public class RandomUtils {
    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
