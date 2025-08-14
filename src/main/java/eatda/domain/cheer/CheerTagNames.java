package eatda.domain.cheer;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;

public class CheerTagNames {

    private static final int MAX_CHEER_TAGS_PER_TYPE = 2;

    private final List<CheerTagName> cheerTagNames;

    public CheerTagNames(List<CheerTagName> cheerTagNames) {
        validate(cheerTagNames);
        this.cheerTagNames = List.copyOf(cheerTagNames);
    }

    private void validate(List<CheerTagName> cheerTagNames) {
        if (isDuplicated(cheerTagNames)) {
            throw new BusinessException(BusinessErrorCode.CHEER_TAGS_DUPLICATED);
        }
        if (maxCountByType(cheerTagNames) > MAX_CHEER_TAGS_PER_TYPE) {
            throw new BusinessException(BusinessErrorCode.EXCEED_CHEER_TAGS_PER_TYPE);
        }
    }

    private boolean isDuplicated(List<CheerTagName> cheerTagNames) {
        long distinctCount = cheerTagNames.stream()
                .distinct()
                .count();
        return distinctCount != cheerTagNames.size();
    }

    private long maxCountByType(List<CheerTagName> cheerTagNames) {
        return cheerTagNames.stream()
                .collect(Collectors.groupingBy(CheerTagName::getType, Collectors.counting()))
                .values()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
    }

    public List<CheerTag> toCheerTags(Cheer cheer) {
        return cheerTagNames.stream()
                .map(name -> new CheerTag(cheer, name))
                .toList();
    }
}
