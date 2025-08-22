package eatda.fixture;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerImage;
import eatda.repository.cheer.CheerImageRepository;
import org.springframework.stereotype.Component;

@Component
public class CheerImageGenerator {

    private final CheerImageRepository cheerImageRepository;

    public CheerImageGenerator(CheerImageRepository cheerImageRepository) {
        this.cheerImageRepository = cheerImageRepository;
    }

    public CheerImage generate(Cheer cheer) {
        return generate(cheer, "dummy/image.png", 1L, "image/png", 1000L);
    }

    public CheerImage generate(Cheer cheer, String imageKey, long orderIndex) {
        return generate(cheer, imageKey, orderIndex, "image/png", 1000L);
    }

    public CheerImage generate(Cheer cheer, String imageKey, long orderIndex, String contentType, long fileSize) {
        CheerImage cheerImage = new CheerImage(
                cheer,
                imageKey,
                orderIndex,
                contentType,
                fileSize
        );
        cheer.addImage(cheerImage);
        return cheerImageRepository.save(cheerImage);
    }
}
