package io.github.karMiguel.capzip.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class ResetPasswordUtil {

    private static final int CODE_LENGTH = 16;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Set<String> generatedCodes = new HashSet<>();

    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        while (code.length() < CODE_LENGTH) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        String generatedCode = code.toString();
        generatedCodes.add(generatedCode);
        return generatedCode;
    }

    public static boolean validateCode(String code) {
        return generatedCodes.contains(code);
    }
}
