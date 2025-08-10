package eatda.controller.cheer;

import eatda.domain.cheer.Cheer;

public record CheerInStoreResponse(
        long id,
        long memberId,
        String memberNickname,
        String description
) {

    public CheerInStoreResponse(Cheer cheer) {
        this(
                cheer.getId(),
                cheer.getMember().getId(),
                cheer.getMember().getNickname(),
                cheer.getDescription()
        );
    }
}
