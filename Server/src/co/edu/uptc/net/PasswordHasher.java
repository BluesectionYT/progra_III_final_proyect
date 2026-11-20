package co.edu.uptc.net;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IPasswordHasher;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordHasher implements IPasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final String SEPARATOR = "$";
    private SecureRandom random;

    public PasswordHasher() {
        this.random = new SecureRandom();
    }

    @Override
    public String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.PASSWORD_EMPTY.getText());
        }
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] hash = derive(password, salt, ITERATIONS, HASH_BITS);
        return ITERATIONS + SEPARATOR + Base64.getEncoder().encodeToString(salt)
                + SEPARATOR + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        String[] parts = storedHash.split(Pattern.quote(SEPARATOR));
        return parts.length == 3 && matches(password, parts);
    }

    private boolean matches(String password, String[] parts) {
        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = derive(password, salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] derive(String password, byte[] salt, int iterations, int bits) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, bits);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(ErrorMessages.HASH_FAILED.getText(), e);
        } finally {
            spec.clearPassword();
        }
    }
}
