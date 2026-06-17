package demo.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

public class Utils {

    public static KeyPair generateKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(512);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static final Path BASE_DIR = Paths.get(System.getProperty("user.dir")).normalize();

    public static void deleteFile(String fileName) throws IOException {
        File file = new File(fileName);
        Path filePath = file.toPath().normalize();

        if (!filePath.startsWith(BASE_DIR)) {
            throw new IOException("Entry is outside of the target directory");
        }

        FileUtils.forceDelete(file);
    }

    public static void executeJs(String input) throws ScriptException {
        if (input == null) {
            throw new ScriptException("Input must not be null");
        }
    }

    public static void encrypt(byte[] key) throws GeneralSecurityException {
        byte[] nonce = "7cVgr5cbdCZV".getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec); // Noncompliant
    }
}
