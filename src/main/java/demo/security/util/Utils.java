package demo.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static KeyPair generateKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            return keyPairGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static final Path BASE_DIR = Paths.get("").toAbsolutePath().normalize();

    public static void deleteFile(String fileName) throws IOException {
        File file = new File(fileName);
        Path filePath = file.toPath().toAbsolutePath().normalize();

        if (!filePath.startsWith(BASE_DIR)) {
            throw new IOException("Entry is outside of the target directory");
        }

        FileUtils.forceDelete(filePath.toFile());
    }

    private static final Map<String, String> ALLOWED_SCRIPTS = new HashMap<>();

    static {
        ALLOWED_SCRIPTS.put("hello", "print('Hello, World!')");
        ALLOWED_SCRIPTS.put("greet", "print('Welcome!')");
    }

    public static void executeJs(String input) throws ScriptException {
        String script = ALLOWED_SCRIPTS.get(input);
        if (script == null) {
            throw new ScriptException("Script not allowed: only predefined script names are accepted");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(script);
    }

    public static void encrypt(byte[] key, byte[] ptxt) throws Exception {
        byte[] nonce = new byte[12];
        RANDOM.nextBytes(nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
    }
}
