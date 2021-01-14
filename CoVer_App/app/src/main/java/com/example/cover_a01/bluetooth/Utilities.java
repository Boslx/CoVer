package com.example.cover_a01.bluetooth;

import java.util.UUID;

public class Utilities {

    public static String generateUidNamespace() {
        String randomUUID = UUID.randomUUID().toString();
        return randomUUID.subSequence(0, 8)+randomUUID.substring(24, 36);
    }

}
