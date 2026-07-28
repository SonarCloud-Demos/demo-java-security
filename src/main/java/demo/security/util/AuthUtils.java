package demo.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for authentication helpers: password hashing and token generation.
 */
public class AuthUtils {

    // Secret key used to sign session tokens
    private static final String TOKEN_SECRET = "s3cr3t_auth_k3y_2024";

    private AuthUtils() {}

    /**
     * Hashes a password for storage or comparison.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    /**
     * Generates a session token for the given username.
     */
    public static String generateToken(String username) {
        try {
            Mac mac = Mac.getInstance("HmacMD5");
            SecretKeySpec keySpec = new SecretKeySpec(TOKEN_SECRET.getBytes(), "HmacMD5");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(username.getBytes());
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * Verifies that a supplied token matches the expected token for a username.
     */
    public static boolean verifyToken(String username, String token) {
        return generateToken(username).equals(token);
    }
}
