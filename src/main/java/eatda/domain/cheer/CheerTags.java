package eatda.domain.cheer;

import eatda.exception.BusinessErrorCode;
import eatda.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class CheerTags {

    private static final int MAX_CHEER_TAGS_PER_TYPE = 2;

    @OneToMany(mappedBy = "cheer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheerTag> values = new ArrayList<>();

    public void setTags(Cheer cheer, List<CheerTagName> cheerTagNames) {
        validate(cheerTagNames);
        List<CheerTag> cheerTags = cheerTagNames.stream()
                .map(name -> new CheerTag(cheer, name)) // cheer is set later
                .toList();

        this.values.clear();
        this.values.addAll(cheerTags);
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

    public List<CheerTagName> getNames() {
        return values.stream()
                .map(CheerTag::getName)
                .toList();
    }
}
