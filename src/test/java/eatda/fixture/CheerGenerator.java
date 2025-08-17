package eatda.fixture;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerImage;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.cheer.CheerImageRepository;
import eatda.repository.cheer.CheerRepository;
import eatda.util.DomainUtils;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class CheerGenerator {

    private static final String DEFAULT_DESCRIPTION = "응원합니다!";

    private final CheerRepository cheerRepository;
    private final CheerImageRepository cheerImageRepository;

    public CheerGenerator(CheerRepository cheerRepository,
                          CheerImageRepository cheerImageRepository) {
        this.cheerRepository = cheerRepository;
        this.cheerImageRepository = cheerImageRepository;
    }

    public Cheer generateAdmin(Member member, Store store, LocalDateTime createdAt) {
        Cheer cheer = new Cheer(member, store, DEFAULT_DESCRIPTION, true);
        DomainUtils.setCreatedAt(cheer, createdAt);
        return cheerRepository.save(cheer);
    }

    public Cheer generateCommon(Member member, Store store) {
        return generateCommon(member, store, false);
    }

    public Cheer generateCommon(Member member, Store store, boolean isAdmin) {
        Cheer cheer = new Cheer(member, store, DEFAULT_DESCRIPTION, isAdmin);
        return cheerRepository.save(cheer);
    }

    public void generateWithImages(Member member, Store store, boolean isAdmin, int imageCount) {
        Cheer cheer = generateCommon(member, store, isAdmin);

        IntStream.range(0, imageCount).forEach(i -> {
            CheerImage image = new CheerImage(
                    cheer,
                    "dummy/" + (i + 1) + ".png",
                    i,
                    "image/png",
                    1000L * (i + 1)
            );
            cheer.addImage(image);
            cheerImageRepository.save(image);
        });
    }
}
