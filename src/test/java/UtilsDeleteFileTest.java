import demo.security.util.Utils;

import java.io.File;

public class UtilsDeleteFileTest {

    public static void main(String[] args) throws Exception {
        File base = new File("target/nonexistent-delete-base");

        assertIllegalArgumentException(() -> Utils.deleteFile(base, "../nonexistent-sibling-file"));
        assertIllegalArgumentException(() -> Utils.deleteFile(base, new File("/nonexistent-abs-file-xyz").getAbsolutePath()));
        assertIllegalArgumentException(() -> Utils.deleteFile(base, "sub/../../nonexistent"));
        assertIllegalArgumentException(() -> Utils.deleteFile(base, null));

        System.out.println("UtilsDeleteFileTest passed");
    }

    private static void assertIllegalArgumentException(ThrowingRunnable runnable) throws Exception {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException to be thrown");
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
