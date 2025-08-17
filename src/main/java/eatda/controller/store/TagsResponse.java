package eatda.controller.store;

import eatda.domain.cheer.CheerTag;
import eatda.domain.cheer.CheerTagName;
import java.util.List;

public record TagsResponse(List<CheerTagName> tags) {

    public static TagsResponse from(List<CheerTag> cheerTags) {
        List<CheerTagName> cheerTagNames = cheerTags.stream()
                .map(CheerTag::getName)
                .distinct()
                .toList();
        return new TagsResponse(cheerTagNames);
    }
}
