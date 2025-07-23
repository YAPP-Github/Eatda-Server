package eatda.util;

import static org.springframework.util.ResourceUtils.getFile;

import java.io.File;
import java.io.FileNotFoundException;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static File getTestImage() {
        try {
            return getFile("classpath:test/test-image.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
