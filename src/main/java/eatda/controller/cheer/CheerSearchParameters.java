package eatda.controller.cheer;

import eatda.controller.store.SearchDistrict;
import eatda.domain.cheer.CheerTagName;
import eatda.domain.store.District;
import eatda.domain.store.StoreCategory;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.lang.Nullable;

public class CheerSearchParameters {

    @Getter
    private final int page;
    @Getter
    private final int size;
    @Nullable
    private final StoreCategory category;
    private final List<CheerTagName> tag;
    private final List<SearchDistrict> location;

    public CheerSearchParameters(int page,
                                 int size,
                                 @Nullable StoreCategory category,
                                 @Nullable List<CheerTagName> tag,
                                 @Nullable List<SearchDistrict> location) {
        this.page = page;
        this.size = size;
        this.category = category;
        this.tag = tag != null ? tag : Collections.emptyList();
        this.location = location != null ? location : Collections.emptyList();
    }

    @Nullable
    public StoreCategory getCategory() {
        return category;
    }

    public List<CheerTagName> getCheerTagNames() {
        return tag;
    }

    public List<District> getDistricts() {
        return location.stream()
                .flatMap(district -> district.getDistricts().stream())
                .distinct()
                .toList();
    }
}
