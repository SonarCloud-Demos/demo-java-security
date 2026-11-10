import demo.security.util.Utils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class AppTest {

    @Test
    public void testGenerateKey() {
        assertNotNull(Utils.generateKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFileRejectsTraversal() throws Exception {
        File base = new File(System.getProperty("java.io.tmpdir"), "demo-security-files-test");
        Utils.deleteFile(base, "../outside.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFileRejectsAbsolutePath() throws Exception {
        File base = new File(System.getProperty("java.io.tmpdir"), "demo-security-files-test");
        Utils.deleteFile(base, new File(System.getProperty("java.io.tmpdir"), "x").getAbsolutePath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteFileRejectsNull() throws Exception {
        File base = new File(System.getProperty("java.io.tmpdir"), "demo-security-files-test");
        Utils.deleteFile(base, null);
    }
}
