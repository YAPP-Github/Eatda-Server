package eatda.util;

import static org.springframework.util.ResourceUtils.getFile;

import java.io.File;
import java.io.FileNotFoundException;

public final class ImageUtils {

    private static final String TEST_IMAGE_PATH = "classpath:test/test-image.png";

    private ImageUtils() {
    }

    public static File getTestImage() {
        try {
            return getFile(TEST_IMAGE_PATH);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("테스트 이미지 파일을 찾을 수 없습니다: " + TEST_IMAGE_PATH, e);
        }
    }
}
