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

public class Utils {

    private static final File BASE_DIR = new File(System.getProperty("java.io.tmpdir"), "demo-files");

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

    public static void deleteFile(String fileName) throws IOException {
        deleteFile(BASE_DIR, fileName);
    }

    public static void deleteFile(File baseDir, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("fileName must not be null or empty");
        }

        if (new File(fileName).isAbsolute() || fileName.startsWith("/") || fileName.startsWith("\\")) {
            throw new IllegalArgumentException("fileName must not be an absolute path");
        }

        for (Path part : Paths.get(fileName)) {
            if (part.toString().equals("..")) {
                throw new IllegalArgumentException("fileName must not contain '..' segments");
            }
        }

        File base = baseDir.getCanonicalFile();
        File target = new File(base, fileName).getCanonicalFile();

        if (target.equals(base) || !target.toPath().startsWith(base.toPath())) {
            throw new IllegalArgumentException("fileName must resolve to a path inside the base directory");
        }

        FileUtils.forceDelete(target);
    }

    public static void executeJs(String input) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(input);
    }

    public static void encrypt(byte[] key, byte[] ptxt) throws Exception {
        byte[] nonce = "7cVgr5cbdCZV".getBytes("UTF-8");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec); // Noncompliant
    }
}
