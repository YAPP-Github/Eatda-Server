package eatda.fixture;

import eatda.domain.member.Member;
import eatda.domain.store.Cheer;
import eatda.domain.store.Store;
import eatda.repository.store.CheerRepository;
import org.springframework.stereotype.Component;

@Component
public class CheerGenerator {

    private static final String DEFAULT_IMAGE_KEY = "default-image-key";
    private static final String DEFAULT_DESCRIPTION = "응원합니다!";

    private final CheerRepository cheerRepository;

    public CheerGenerator(CheerRepository cheerRepository) {
        this.cheerRepository = cheerRepository;
    }

    public Cheer generateAdmin(Member member, Store store) {
        Cheer cheer = new Cheer(member, store, DEFAULT_IMAGE_KEY, DEFAULT_DESCRIPTION, true);
        return cheerRepository.save(cheer);
    }

    public Cheer generateCommon(Member member, Store store) {
        Cheer cheer = new Cheer(member, store, DEFAULT_IMAGE_KEY, DEFAULT_DESCRIPTION, false);
        return cheerRepository.save(cheer);
    }
}
