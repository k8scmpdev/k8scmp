package org.k8scmp.util;

import java.util.UUID;

public class UUIDUtil {
    public static String generateUUID() {
        StringBuilder uuid = new StringBuilder(UUID.randomUUID().toString().replaceAll("-", ""));
        return uuid.toString();
    }
}
