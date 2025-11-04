package com.bookfair.bookfairreservationsystembackend.utils;

import java.util.Base64;
import java.security.SecureRandom;

public class GenerateKey {
    public static void main(String[] args) {
        byte[] key = new byte[64]; // 512-bit key for HS512
        new SecureRandom().nextBytes(key);
        String encoded = Base64.getEncoder().encodeToString(key);
        System.out.println(encoded);
    }
}
