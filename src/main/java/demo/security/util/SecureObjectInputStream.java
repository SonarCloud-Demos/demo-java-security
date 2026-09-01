package demo.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;

public class SecureObjectInputStream extends ObjectInputStream {

    private static final List<String> APPROVED_CLASSES = new ArrayList<>();

    static {
        APPROVED_CLASSES.add(SessionHeader.class.getName());
    }

    public SecureObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        if (!APPROVED_CLASSES.contains(osc.getName())) {
            throw new InvalidClassException("Unauthorized deserialization", osc.getName());
        }
        return super.resolveClass(osc);
    }
}
