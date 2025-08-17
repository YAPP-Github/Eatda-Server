package eatda.fixture;

import eatda.domain.cheer.Cheer;
import eatda.domain.member.Member;
import eatda.domain.store.Store;
import eatda.repository.cheer.CheerRepository;
import eatda.util.DomainUtils;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CheerGenerator {

    private static final String DEFAULT_DESCRIPTION = "응원합니다!";

    private final CheerRepository cheerRepository;

    public CheerGenerator(CheerRepository cheerRepository) {
        this.cheerRepository = cheerRepository;
    }

    public Cheer generateAdmin(Member member, Store store, LocalDateTime createdAt) {
        Cheer cheer = new Cheer(member, store, DEFAULT_DESCRIPTION, true);
        DomainUtils.setCreatedAt(cheer, createdAt);
        return cheerRepository.save(cheer);
    }

    public Cheer generateCommon(Member member, Store store) {
        return generateCommon(member, store, false, DEFAULT_DESCRIPTION);
    }

    public Cheer generateCommon(Member member, Store store, boolean isAdmin) {
        return generateCommon(member, store, isAdmin, DEFAULT_DESCRIPTION);
    }

    public Cheer generateCommon(Member member, Store store, LocalDateTime createdAt) {
        Cheer cheer = generateCommon(member, store, false, DEFAULT_DESCRIPTION);
        DomainUtils.setCreatedAt(cheer, createdAt);
        return cheerRepository.save(cheer);
    }

    public Cheer generateCommon(Member member, Store store, boolean isAdmin, String description) {
        Cheer cheer = new Cheer(member, store, description, isAdmin);
        return cheerRepository.save(cheer);
    }
}
