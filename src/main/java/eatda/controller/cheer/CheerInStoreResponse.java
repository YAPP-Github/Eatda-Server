package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTagName;
import java.util.List;

public record CheerInStoreResponse(
        long id,
        long memberId,
        String memberNickname,
        String description,
        List<CheerTagName> tags
) {

    public CheerInStoreResponse(Cheer cheer) {
        this(
                cheer.getId(),
                cheer.getMember().getId(),
                cheer.getMember().getNickname(),
                cheer.getDescription(),
                cheer.getCheerTagNames()
        );
    }
}
