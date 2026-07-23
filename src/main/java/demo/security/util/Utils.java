package demo.security.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class Utils {

    private static final String DOCUMENTS_ROOT = "/var/app/documents";

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

    public static void createFile(String fileName) throws IOException {
        File file = null;
        if (fileName == null){
            file = new File(fileName);
        } else {
            file = new File(fileName);
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileUtils.forceDelete(file);
    }

    /**
     * Advanced SAST demo: first-party hop through Apache Commons IO.
     * separatorsToUnix is not a sanitizer; taint continues into FileUtils sinks.
     */
    public static String prepareDocumentPath(String documentId) {
        return FilenameUtils.separatorsToUnix(documentId);
    }

    /**
     * Advanced SAST demo: first-party orchestration through Apache Commons IO.
     * FilenameUtils.concat + FileUtils.readFileToString are third-party path sinks.
     */
    public static String loadDocument(String documentId) throws IOException {
        String relative = prepareDocumentPath(documentId);
        String absolute = FilenameUtils.concat(DOCUMENTS_ROOT, relative);
        return FileUtils.readFileToString(new File(absolute), StandardCharsets.UTF_8);
    }

    /**
     * Advanced SAST demo: taint passes through commons-io IOUtils (third-party).
     */
    public static String readRequestBody(InputStream body) throws IOException {
        return IOUtils.toString(body, StandardCharsets.UTF_8);
    }

    /**
     * Advanced SAST demo: taint passes through commons-codec Base64 (third-party).
     */
    public static String decodeUserToken(String encodedToken) {
        byte[] decoded = Base64.decodeBase64(encodedToken);
        return new String(decoded, StandardCharsets.UTF_8);
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
